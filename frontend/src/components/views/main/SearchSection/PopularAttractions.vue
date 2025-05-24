<template>
  <section class="py-8 px-4">
    <div class="container mx-auto px-4 sm:px-6 lg:px-8 max-w-7xl">
      <!-- 섹션 제목 -->
      <h2 class="text-2xl font-bold mb-8 text-gray-900">{{ title }}</h2>

      <!-- 로딩 상태 -->
      <div v-if="isLoading" class="flex justify-center items-center py-12">
        <div class="text-center">
          <div
            class="animate-spin rounded-full h-12 w-12 border-b-2 border-[var(--primary-color)] mx-auto mb-4"
          ></div>
          <p class="text-gray-600">인기 장소를 검색 중입니다...</p>
        </div>
      </div>

      <!-- 검색 결과 없음 (초기 상태) -->
      <div v-else-if="!hasSearched" class="text-center py-12">
        <div
          class="w-24 h-24 mx-auto mb-6 rounded-full bg-gray-100 flex items-center justify-center"
        >
          <i class="pi pi-search text-3xl text-gray-400"></i>
        </div>
        <h3 class="text-xl font-semibold text-gray-900 mb-2">인기 장소를 검색해보세요!</h3>
        <p class="text-gray-600 mb-6">
          원하는 지역과 카테고리를 선택하여 인기 장소 TOP 6을 확인해보세요.
        </p>
        <div class="flex justify-center">
          <div
            class="bg-gradient-to-r from-[var(--primary-color)] to-[var(--secondary-color)] text-white px-6 py-2 rounded-full text-sm"
          >
            <i class="pi pi-arrow-up mr-2"></i>
            위의 검색 옵션을 이용해보세요
          </div>
        </div>
      </div>

      <!-- 검색 결과 있음 -->
      <div
        v-else-if="attractions && attractions.length > 0"
        class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6"
      >
        <div
          v-for="(attraction, index) in attractions"
          :key="attraction.attractionId"
          class="flex items-center p-4 bg-white rounded-lg shadow-sm hover:shadow-md transition-shadow cursor-pointer"
          @click="handleAttractionClick(attraction)"
        >
          <!-- 순위 번호 -->
          <span class="text-3xl font-bold text-gray-400 mr-4">
            {{ index + 1 }}
          </span>

          <!-- 컨텐츠 -->
          <div class="flex items-center flex-1">
            <!-- 이미지 -->
            <div class="w-20 h-14 rounded-lg overflow-hidden mr-4 flex-shrink-0">
              <img
                :src="attraction.image || defaultImage"
                :alt="attraction.attractionName"
                class="w-full h-full object-cover"
                @error="handleImageError"
              />
            </div>

            <!-- 정보 -->
            <div class="flex-1 min-w-0">
              <h3 class="text-sm font-bold text-gray-900 mb-1 line-clamp-2">
                {{ attraction.attractionName }}
              </h3>
              <p class="text-xs text-gray-600 mb-2">
                {{ attraction.stateName }} {{ attraction.cityName }}
              </p>

              <!-- 평점과 리뷰 수 -->
              <div class="flex items-center text-xs text-gray-500">
                <div v-if="attraction.reviewAvgScore" class="flex items-center mr-3">
                  <i class="pi pi-star-fill text-yellow-400 mr-1"></i>
                  <span>{{ attraction.reviewAvgScore.toFixed(1) }}</span>
                </div>
                <div v-if="attraction.reviewCount">
                  <span>리뷰 {{ attraction.reviewCount }}개</span>
                </div>
                <div
                  v-if="!attraction.reviewAvgScore && !attraction.reviewCount"
                  class="text-gray-400"
                >
                  <span>리뷰 없음</span>
                </div>
              </div>
            </div>
          </div>

          <!-- 화살표 아이콘 -->
          <i class="pi pi-chevron-right text-gray-400 ml-4"></i>
        </div>
      </div>

      <!-- 검색 결과 없음 -->
      <div v-else class="text-center py-12">
        <div
          class="w-24 h-24 mx-auto mb-6 rounded-full bg-gray-100 flex items-center justify-center"
        >
          <i class="pi pi-exclamation-triangle text-3xl text-gray-400"></i>
        </div>
        <h3 class="text-xl font-semibold text-gray-900 mb-2">검색 결과가 없습니다</h3>
        <p class="text-gray-600 mb-6">다른 조건으로 다시 검색해보세요.</p>
        <button
          @click="$emit('retry-search')"
          class="bg-gradient-to-r from-[var(--primary-color)] to-[var(--secondary-color)] text-white px-6 py-2 rounded-lg hover:shadow-md transition-shadow"
        >
          <i class="pi pi-refresh mr-2"></i>
          다시 검색하기
        </button>
      </div>
    </div>
  </section>
</template>

<script setup>
import { computed } from 'vue'

// Props
const props = defineProps({
  attractions: {
    type: Array,
    default: () => [],
  },
  isLoading: {
    type: Boolean,
    default: false,
  },
  hasSearched: {
    type: Boolean,
    default: false,
  },
  searchParams: {
    type: Object,
    default: () => ({}),
  },
})

// Events
const emit = defineEmits(['attraction-click', 'retry-search'])

// 기본 이미지
const defaultImage =
  'https://images.unsplash.com/photo-1469474968028-56623f02e42e?w=80&h=56&fit=crop&crop=center'

// 제목 생성
const title = computed(() => {
  const { searchParams } = props

  if (!props.hasSearched) {
    return '인기 장소 TOP 6 검색 결과'
  }

  let titleParts = []

  // 지역 정보 추가 (state, city)
  if (searchParams.stateName) {
    titleParts.push(searchParams.stateName)
  }
  if (searchParams.cityName) {
    titleParts.push(searchParams.cityName)
  }

  // 카테고리 정보 추가
  if (searchParams.categoryName) {
    titleParts.push(searchParams.categoryName)
  }

  // 제목 조합
  if (titleParts.length > 0) {
    return `${titleParts.join(' ')} 검색 결과`
  } else {
    return '전체 인기 장소 TOP 6'
  }
})

// 장소 클릭 핸들러
const handleAttractionClick = (attraction) => {
  emit('attraction-click', attraction)
  console.log('선택된 장소:', attraction)
}

// 이미지 로드 에러 처리
const handleImageError = (event) => {
  event.target.src = defaultImage
}
</script>

<style scoped>
/* 텍스트 클램프 */
.line-clamp-2 {
  display: -webkit-box;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 호버 효과 */
.group:hover {
  transform: translateY(-2px);
  transition: transform 0.2s ease;
}

/* 로딩 애니메이션 */
@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

.animate-spin {
  animation: spin 1s linear infinite;
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .flex {
    flex-direction: column;
    align-items: flex-start;
  }

  .text-3xl {
    font-size: 1.5rem;
    margin-bottom: 0.5rem;
  }

  .w-20 {
    width: 100%;
    height: 120px;
    margin-bottom: 0.75rem;
  }
}
</style>
