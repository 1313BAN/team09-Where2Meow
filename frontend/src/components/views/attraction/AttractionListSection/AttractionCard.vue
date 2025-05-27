<template>
  <div class="attraction-card group cursor-pointer" @click="handleClick">
    <!-- 이미지 섹션 -->
    <div class="relative overflow-hidden rounded-lg mb-3">
      <div class="aspect-w-4 aspect-h-3 bg-gray-200">
        <img
          v-if="attraction.firstimage"
          :src="attraction.firstimage"
          :alt="attraction.title"
          class="w-full h-48 object-cover transition-transform duration-300 group-hover:scale-105"
          @error="handleImageError"
        />
        <img
          v-else
          :src="defaultImage"
          :alt="attraction.title"
          class="w-full h-48 object-cover transition-transform duration-300 group-hover:scale-105"
        />
      </div>

      <!-- 카테고리 배지 -->
      <div class="absolute top-3 left-3">
        <span
          class="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-white/90 text-gray-800 backdrop-blur-sm"
        >
          {{ getCategoryName(attraction.contenttypeid) }}
        </span>
      </div>

      <!-- 좋아요/북마크 버튼 (추후 구현용) -->
      <!-- <div
        class="absolute top-3 right-3 flex gap-2 opacity-0 group-hover:opacity-100 transition-opacity duration-200"
      >
        <button
          class="p-2 bg-white/90 backdrop-blur-sm rounded-full hover:bg-white transition-colors"
          @click.stop="toggleLike"
        >
          <i class="pi pi-heart text-gray-600 hover:text-red-500 transition-colors"></i>
        </button>
        <button
          class="p-2 bg-white/90 backdrop-blur-sm rounded-full hover:bg-white transition-colors"
          @click.stop="toggleBookmark"
        >
          <i class="pi pi-bookmark text-gray-600 hover:text-yellow-500 transition-colors"></i>
        </button>
      </div>-->
    </div>

    <!-- 콘텐츠 섹션 -->
    <div class="px-1">
      <!-- 제목 -->
      <h3
        class="font-semibold text-gray-900 mb-2 line-clamp-2 group-hover:text-[var(--primary-color)] transition-colors"
      >
        {{ attraction.title }}
      </h3>

      <!-- 주소 -->
      <p class="text-sm text-gray-600 mb-3 line-clamp-1">
        <i class="pi pi-map-marker mr-1"></i>
        {{ formatAddress(attraction) }}
      </p>

      <!-- 평점 및 리뷰 -->
      <div class="flex items-center justify-between">
        <div class="flex items-center gap-2">
          <!-- 평점 표시 (추후 리뷰 데이터와 연동) -->
          <div class="flex items-center gap-1">
            <div class="flex">
              <i
                v-for="star in 5"
                :key="star"
                :class="[
                  'pi text-sm',
                  star <= averageRating ? 'pi-star-fill text-yellow-400' : 'pi-star text-gray-300',
                ]"
              >
              </i>
            </div>
            <span class="text-sm text-gray-600">{{ averageRating.toFixed(1) }}</span>
          </div>
        </div>

        <!-- 리뷰 개수 -->
        <div class="text-sm text-gray-500">
          <i class="pi pi-comment mr-1"></i>
          리뷰 {{ reviewCount }}개
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  attraction: {
    type: Object,
    required: true,
  },
})

const emit = defineEmits(['click', 'like', 'bookmark'])

// 기본 이미지
const defaultImage =
  'https://images.unsplash.com/photo-1469474968028-56623f02e42e?w=400&h=300&fit=crop&crop=center'

// 카테고리 매핑
const categoryNames = {
  12: '관광지',
  14: '문화시설',
  15: '축제공연행사',
  25: '여행코스',
  28: '레저/스포츠',
  32: '숙박',
  38: '쇼핑',
  39: '음식점',
}

// 임시 평점 데이터 (추후 실제 리뷰 데이터와 연동)
const averageRating = computed(() => {
  // 임시로 3.5 ~ 4.8 사이의 랜덤 평점 생성
  return Math.random() * 1.3 + 3.5
})

const reviewCount = computed(() => {
  // 임시로 0 ~ 50 사이의 랜덤 리뷰 개수 생성
  return Math.floor(Math.random() * 51)
})

// 카테고리 이름 반환
const getCategoryName = (contentTypeId) => {
  return categoryNames[contentTypeId] || '기타'
}

// 주소 포맷팅
const formatAddress = (attraction) => {
  const parts = []
  if (attraction.addr1) {
    // 주소를 간단하게 표시 (시/도, 시/군/구만)
    const addressParts = attraction.addr1.split(' ')
    if (addressParts.length >= 2) {
      parts.push(addressParts[0], addressParts[1])
    } else {
      parts.push(attraction.addr1)
    }
  }
  return parts.join(' ') || '주소 정보 없음'
}

// 이벤트 핸들러
const handleClick = () => {
  emit('click', props.attraction)
}

const handleImageError = (event) => {
  event.target.src = defaultImage
}

// const toggleLike = () => {
//   emit('like', props.attraction)
// }

// const toggleBookmark = () => {
//   emit('bookmark', props.attraction)
// }
</script>

<style scoped>
/* 반응형 이미지 비율 유지 */
.aspect-w-4 {
  position: relative;
  width: 100%;
}

.aspect-h-3 {
  padding-bottom: 75%; /* 4:3 비율 */
}

.aspect-w-4 > img {
  position: absolute;
  height: 100%;
  width: 100%;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
}
</style>
