<template>
  <section
    class="relative h-[550px] bg-cover bg-center text-white flex items-center overflow-hidden"
    :style="{ backgroundImage: `url(${bannerImage})` }"
  >
    <!-- 배경 오버레이 -->
    <div class="absolute inset-0 bg-gradient-to-b from-black/40 to-black/70"></div>

    <div class="container relative z-10 max-w-7xl mx-auto px-4">
      <div class="banner-content max-w-2xl p-8">
        <!-- 메인 제목 -->
        <h1 class="text-4xl font-bold mb-4 leading-tight text-shadow-lg">
          {{ mainTitle }}
        </h1>

        <!-- 고양이 아이콘 -->
        <div class="flex items-center gap-2 mb-4">
          <span class="text-[var(--accent-color)] text-lg">계획 세우는 걸 도와줄게, 냥!</span>
        </div>

        <!-- 부제목 -->
        <p class="text-xl mb-8 text-shadow-sm">
          {{ subtitle }}
        </p>

        <!-- 검색 폼 -->
        <form
          @submit.prevent="handleSearch"
          class="flex gap-3 w-full bg-white/10 backdrop-blur-md p-3 rounded-2xl shadow-lg border border-white/20"
        >
          <div class="flex-1 relative">
            <i
              class="pi pi-home absolute left-4 top-1/2 transform -translate-y-1/2 text-[var(--primary-color)]"
            ></i>
            <InputText
              v-model="searchQuery"
              :placeholder="searchPlaceholder"
              class="w-full py-3 pl-10 pr-4 rounded-xl border-0 shadow-inner text-gray-900"
              size="large"
            />
          </div>
          <Button
            label="검색하기"
            icon="pi pi-search"
            :loading="isSearching"
            @click="handleSearch"
            class="paw-cursor"
            pt:root="bg-gradient-to-r from-[var(--primary-color)] to-[var(--secondary-color)] hover:from-[#FF7A54] hover:to-[#4E249A] active:scale-95 transition-all duration-200 ease-in-out rounded-xl px-6 py-2 shadow-md text-white border-none flex items-center gap-2 cursor-pointer"
            pt:label="text-white font-semibold text-base tracking-wide"
          />
        </form>

        <!-- 타입라이터 효과 -->
        <div class="typewriter-container mt-6 h-6 overflow-hidden">
          <p class="typewriter-text text-sm text-white/80">
            <i class="pi pi-info-circle mr-2 text-[var(--accent-color)]"></i>
            <span>{{ currentTypewriterText }}</span>
          </p>
        </div>
      </div>
    </div>

    <!-- 스크롤 다운 버튼 -->
    <button
      @click="scrollDown"
      class="absolute bottom-6 left-1/2 transform -translate-x-1/2 flex flex-col items-center text-white/70 hover:text-white transition-colors paw-cursor"
    >
      <span class="text-sm mb-2">더 알아보기</span>
      <i class="pi pi-chevron-down animate-bounce text-xl"></i>
    </button>
  </section>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import InputText from 'primevue/inputtext'
import Button from 'primevue/button'

// Props 정의
const props = defineProps({
  bannerImage: {
    type: String,
    default:
      'https://images.unsplash.com/photo-1580137189272-c9379f8864fd?q=80&w=1920&h=1200&fit=crop',
  },
  mainTitle: {
    type: String,
    default: '어디가냥과 함께하는 스마트한 여행 계획',
  },
  subtitle: {
    type: String,
    default: '가냥이에게 물어보세요~',
  },
  searchPlaceholder: {
    type: String,
    default: '예: 서울 2박 3일 여행 스케쥴 짜줘',
  },
})

// 반응형 데이터
const searchQuery = ref('')
const isSearching = ref(false)
const currentTypewriterText = ref('')

// 타입라이터 텍스트 배열
const typewriterTexts = [
  '고양이와 함께 가는 여행, 어디가냥이 찾아드리겠습니다',
  '"서울 2박 3일 고양이 동반 카페" 처럼 검색해보세요',
  '가냥이가 추천하는 반려동물 동반 여행지 TOP 10',
  '스페셜 프로모션: 유저 후기 작성 시 포인트 적립!',
]

// 타입라이터 효과 관련 변수
let currentTextIndex = 0
let charIndex = 0
let isTyping = true
let typingTimer

// 타입라이터 효과 함수
const typeWriter = () => {
  const currentText = typewriterTexts[currentTextIndex]

  if (isTyping) {
    if (charIndex < currentText.length) {
      currentTypewriterText.value = currentText.substring(0, charIndex + 1)
      charIndex++
    } else {
      isTyping = false
      // 2초 대기 후 삭제 시작
      setTimeout(() => {
        isTyping = false
      }, 2000)
    }
  } else {
    if (charIndex > 0) {
      currentTypewriterText.value = currentText.substring(0, charIndex - 1)
      charIndex--
    } else {
      isTyping = true
      currentTextIndex = (currentTextIndex + 1) % typewriterTexts.length
    }
  }

  // 타이머 속도 조정 (타이핑은 빠르게, 삭제는 조금 더 빠르게)
  const timerDelay = isTyping ? 100 : 50
  typingTimer = setTimeout(typeWriter, timerDelay)
}

// 스크롤 다운 함수
const scrollDown = () => {
  const nextSection = document.querySelector('.main-view > :nth-child(2)')
  if (nextSection) {
    nextSection.scrollIntoView({ behavior: 'smooth' })
  }
}

// Events
const emit = defineEmits(['search'])

// 검색 핸들러
const handleSearch = async () => {
  if (!searchQuery.value.trim()) return

  isSearching.value = true

  try {
    emit('search', searchQuery.value.trim())
    // 실제 검색 API 호출 로직
    console.log('검색어:', searchQuery.value)
  } finally {
    isSearching.value = false
  }
}

onMounted(() => {
  // 타입라이터 효과 시작
  typeWriter()
})

onBeforeUnmount(() => {
  // 타이머 정리
  clearTimeout(typingTimer)
})
</script>

<style scoped>
/* 컨테이너 스타일 */
.container {
  max-width: 1200px;
}

/* 배너 콘텐츠 애니메이션 */
.banner-content {
  animation: fadeInUp 1s ease-out;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 고양이 귀 효과 */
.cat-ears {
  position: relative;
}

.cat-ears:before,
.cat-ears:after {
  content: '';
  position: absolute;
  width: 0;
  height: 0;
  border-left: 8px solid transparent;
  border-right: 8px solid transparent;
  border-bottom: 16px solid var(--accent-color);
  top: -14px;
  opacity: 0.6;
  animation: earWiggle 3s infinite alternate;
}

.cat-ears:before {
  left: 0;
  transform: rotate(30deg);
  animation-delay: 0.5s;
}

.cat-ears:after {
  left: 10px;
  transform: rotate(-10deg);
}

@keyframes earWiggle {
  0%,
  100% {
    transform: rotate(30deg);
  }
  50% {
    transform: rotate(20deg);
  }
}

/* 텍스트 샤도우 효과 */
.text-shadow-lg {
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
}

.text-shadow-sm {
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
}

/* 고양이 발자국 패턴 */
.cat-paw-pattern {
  background-image: url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cpath d='M30 16c-2.2 0-4 1.8-4 4 0 1.2.5 2.3 1.3 3-1.2 1-2 2.5-2 4.2 0 3 2.5 5.5 5.5 5.5s5.5-2.5 5.5-5.5c0-1.7-.8-3.2-2-4.2.8-.7 1.3-1.8 1.3-3 0-2.2-1.8-4-4-4zM15 26c-2.2 0-4 1.8-4 4 0 1.2.5 2.3 1.3 3-1.2 1-2 2.5-2 4.2 0 3 2.5 5.5 5.5 5.5s5.5-2.5 5.5-5.5c0-1.7-.8-3.2-2-4.2.8-.7 1.3-1.8 1.3-3 0-2.2-1.8-4-4-4zm30 0c-2.2 0-4 1.8-4 4 0 1.2.5 2.3 1.3 3-1.2 1-2 2.5-2 4.2 0 3 2.5 5.5 5.5 5.5s5.5-2.5 5.5-5.5c0-1.7-.8-3.2-2-4.2.8-.7 1.3-1.8 1.3-3 0-2.2-1.8-4-4-4z' fill='white' fill-opacity='.03'/%3E%3C/svg%3E");
  opacity: 0.5;
}

/* 타입라이터 효과 */
.typewriter-container {
  border-left: 3px solid var(--primary-color);
  padding-left: 10px;
  opacity: 0.9;
}

.typewriter-text {
  display: inline-block;
  overflow: hidden;
  white-space: nowrap;
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .banner-content {
    padding: 1.5rem;
    max-width: 100%;
  }

  h1 {
    font-size: 2rem;
    line-height: 1.2;
  }

  p {
    font-size: 1rem;
  }
}
</style>
