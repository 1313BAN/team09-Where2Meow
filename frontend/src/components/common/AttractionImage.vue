<template>
  <div class="image-container">
    <!-- 디버그 정보 (개발 완료) -->
    <div v-if="false" class="debug-info">
      <small>
        L: {{ loading }}, E: {{ error }}, A: {{ loadAttempts }}<br />
        Src: {{ imgSrc ? imgSrc.substring(imgSrc.length - 20) : 'none' }}
      </small>
    </div>

    <div v-if="loading && !imgSrc" class="image-skeleton">
      <div class="skeleton-animation" role="img" :aria-label="`${alt} 로딩 중`"></div>
    </div>
    <img
      v-show="!error"
      ref="imageElement"
      :src="imgSrc"
      @load="handleLoad"
      @error="handleError"
      :alt="alt"
      :class="className"
      loading="lazy"
      :aria-hidden="loading"
    />
    <div
      v-if="error"
      class="image-error"
      role="img"
      :aria-label="`${alt} 로딩 실패, 기본 이미지 표시`"
    >
      <img
        :src="DEFAULT_IMAGE_URL"
        :alt="alt"
        :class="className"
        loading="lazy"
        aria-hidden="true"
      />
    </div>
  </div>
</template>

<script>
import {
  DEFAULT_IMAGE_URL,
  getFullImageUrl,
  handleImageError,
  preloadImage,
} from '@/utils/image-utils'

export default {
  name: 'AttractionImage',
  props: {
    imageUrl: {
      type: String,
      default: '',
    },
    alt: {
      type: String,
      default: '관광 명소',
    },
    className: {
      type: String,
      default: '',
    },
    preload: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      imgSrc: '',
      loading: true,
      error: false,
      DEFAULT_IMAGE_URL,
      lastImageUrl: null, // 이전 이미지 URL 추적용
      loadAttempts: 0, // 로드 시도 횟수
      debugInfo: {
        componentMounted: false,
        imageUrlReceived: false,
        imageSrcSet: false,
        imageLoadStarted: false,
        imageLoadCompleted: false,
      },
    }
  },
  methods: {
    handleLoad() {
      this.loading = false
      this.error = false
      this.$emit('imageLoaded', this.imageUrl)
    },
    handleError(event) {
      this.loading = false
      this.error = true
      this.$emit('imageError', this.imageUrl)

      // 에러 발생 시 3초 후 자동 재시도
      const MAX_RETRY_ATTEMPTS = 3
      setTimeout(() => {
        if (this.error && this.imageUrl && this.loadAttempts <= MAX_RETRY_ATTEMPTS) {
          this.refreshImage()
        } else if (this.loadAttempts > MAX_RETRY_ATTEMPTS) {
          log.warn(`최대 재시도 횟수 초과: ${this.imageUrl}`)
        }
      }, 3000)

      // 기본 이미지 에러 핸들러 사용
      handleImageError(event)
    },

    // 임시 캐시 버스팅 함수 (가져오기 실패 대비)
    addCacheBustingLocal(imageUrl) {
      if (!imageUrl || imageUrl === DEFAULT_IMAGE_URL) return imageUrl

      const timestamp = Date.now()
      const separator = imageUrl.includes('?') ? '&' : '?'
      return `${imageUrl}${separator}t=${timestamp}`
    },
    async updateImageSrc() {
      this.loadAttempts++

      if (!this.imageUrl) {
        this.imgSrc = DEFAULT_IMAGE_URL
        this.loading = false
        this.lastImageUrl = null
        return
      }

      const processedUrl = getFullImageUrl(this.imageUrl)
      const isUrlChanged = this.lastImageUrl !== processedUrl

      let finalUrl = processedUrl

      // URL이 변경되었거나 에러 상태인 경우 캐시 버스팅 적용
      if (isUrlChanged || this.error) {
        finalUrl = this.addCacheBustingLocal(processedUrl)
      }

      // 프리로드 옵션이 켜진 경우 미리 로딩
      if (this.preload) {
        try {
          await preloadImage(finalUrl)
        } catch (error) {
          // 프리로드 실패는 조용히 무시
        }
      }

      this.imgSrc = finalUrl
      this.loading = true
      this.error = false
      this.lastImageUrl = processedUrl

      // 이미지 로드 이벤트가 없는 경우 대비 타이머 설정
      this.$nextTick(() => {
        const imgElement = this.$refs.imageElement
        if (imgElement && imgElement.complete && imgElement.naturalWidth > 0) {
          // 이미 로드된 이미지인 경우
          this.loading = false
          this.error = false
        } else {
          // 로드 이벤트를 기다리는 타이머 (안전장치)
          setTimeout(() => {
            if (this.loading && imgElement && imgElement.complete) {
              this.loading = false
            }
          }, 2000)
        }
      })
    },

    /**
     * 이미지 강제 새로고침
     */
    refreshImage() {
      this.error = false
      this.loading = true

      if (this.imageUrl) {
        const processedUrl = getFullImageUrl(this.imageUrl)
        const cacheBustedUrl = this.addCacheBustingLocal(processedUrl)
        this.imgSrc = cacheBustedUrl
      }
    },
  },
  watch: {
    imageUrl: {
      immediate: true,
      handler() {
        this.updateImageSrc()
      },
    },
  },
  mounted() {
    // 컴포넌트가 마운트된 후 이미지 로딩 시작
    this.updateImageSrc()

    // 부모 컴포넌트에서 사용할 수 있도록 메서드 노출
    this.$emit('imageComponent', this)
  },
}
</script>

<style scoped>
.debug-info {
  position: absolute;
  top: 0;
  left: 0;
  background: rgba(0, 0, 0, 0.9);
  color: lime;
  padding: 2px 4px;
  font-size: 8px;
  z-index: 1000;
  max-width: 100px;
  word-break: break-all;
  font-family: monospace;
}

.image-container {
  position: relative;
  width: 100%;
  height: 100%;
  min-width: 60px; /* 좌측 패널에 맞는 작은 최소 크기 */
  min-height: 60px; /* 좌측 패널에 맞는 작은 최소 크기 */
  border-radius: 8px;
  overflow: hidden;
  background-color: #f8f9fa;
  display: flex;
  align-items: center;
  justify-content: center;
}

.image-container img {
  width: 100%;
  height: 100%;
  min-width: 40px; /* 이미지 최소 크기 */
  min-height: 40px; /* 이미지 최소 크기 */
  object-fit: cover;
  border-radius: 8px;
  display: block;
  transition: opacity 0.3s ease;
}

/* 고화질 이미지를 위한 최적화 */
.image-container img {
  image-rendering: -webkit-optimize-contrast;
  image-rendering: crisp-edges;
}

.image-skeleton {
  width: 100%;
  height: 100%;
  background-color: #e9ecef;
  border-radius: 8px;
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}

.skeleton-animation {
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.6), transparent);
  animation: skeleton-loading 1.8s infinite ease-in-out;
}

@keyframes skeleton-loading {
  0% {
    left: -100%;
    opacity: 0;
  }
  50% {
    opacity: 1;
  }
  100% {
    left: 100%;
    opacity: 0;
  }
}

.image-error {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f8f9fa;
}

.image-error img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 8px;
  display: block;
  opacity: 0.7;
}

/* 반응형 이미지 최적화 */
@media (min-width: 768px) {
  .image-container img {
    image-rendering: auto;
    image-rendering: high-quality;
  }
}

/* 다크 모드 지원 */
@media (prefers-color-scheme: dark) {
  .image-skeleton {
    background-color: #343a40;
  }

  .skeleton-animation {
    background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.1), transparent);
  }

  .image-error {
    background-color: #343a40;
  }
}
</style>
