<template>
  <div class="attraction-card group cursor-pointer" @click="handleClick">
    <!-- 이미지 섹션 -->
    <div class="relative overflow-hidden rounded-lg mb-3">
      <div class="aspect-w-4 aspect-h-3 bg-gray-200">
        <img
          v-if="attraction.image"
          :src="attraction.image"
          :alt="attraction.attractionName"
          class="w-full h-48 object-cover transition-transform duration-300 group-hover:scale-105"
          @error="handleImageError"
        />
        <img
          v-else
          :src="defaultImage"
          :alt="attraction.attractionName"
          class="w-full h-48 object-cover transition-transform duration-300 group-hover:scale-105"
        />
      </div>

      <!-- 카테고리 배지 -->
      <!-- <div class="absolute top-3 left-3">
        <span
          class="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-white/90 text-gray-800 backdrop-blur-sm"
        >
          {{ getCategoryName(attraction.categoryId) }}
        </span>
      </div> -->
      <div class="category-badge">{{ getCategoryName(attraction.categoryId) }}</div>
    </div>

    <!-- 콘텐츠 섹션 -->
    <div class="px-1">
      <!-- 제목 -->
      <h3
        class="font-semibold text-gray-900 mb-2 line-clamp-2 group-hover:text-[var(--primary-color)] transition-colors"
      >
        {{ attraction.attractionName }}
      </h3>

      <!-- 주소 -->
      <p class="text-sm text-gray-600 mb-3 line-clamp-1">
        <i class="pi pi-map-marker mr-1"></i>
        {{ formatAddress(attraction) }}
      </p>

      <!-- 평점 및 리뷰 -->
      <div class="flex items-center justify-between">
        <div class="flex items-center gap-2">
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
import { computed, ref, onMounted } from 'vue'
import attractionAPI from '@/api/attraction'

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

// 카테고리 데이터
const categories = ref([])

const averageRating = computed(() => {
  return props.attraction.reviewAvgScore || 0
})

const reviewCount = computed(() => {
  return props.attraction.reviewCount || 0
})

// 카테고리 이름 반환
const getCategoryName = (categoryId) => {
  const category = categories.value.find((cat) => cat.categoryId === categoryId)
  return category ? category.categoryName : '기타'
}

// 주소 포맷팅
const formatAddress = (attraction) => {
  const parts = []
  if (attraction.stateName && attraction.cityName) {
    parts.push(attraction.stateName, attraction.cityName)
  } else if (attraction.addr1) {
    const addressParts = attraction.addr1.split(' ')
    if (addressParts.length >= 2) {
      parts.push(addressParts[0], addressParts[1])
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

// 컴포넌트 마운트 시 카테고리 데이터 로드
onMounted(async () => {
  try {
    attractionAPI.attractionApi
      .getAllCategories()
      .then((response) => {
        categories.value = response.data
      })
      .catch((error) => {
        console.error('카테고리 목록 불러오기 실패', error)
      })
  } catch (error) {
    console.error('카테고리 데이터 로드 중 오류:', error)
  }
})
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

.category-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  background: linear-gradient(135deg, #00edb3 0%, #00c297 100%);
  color: white;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 600;
  box-shadow: 0 4px 12px rgba(0, 237, 179, 0.3);
  backdrop-filter: blur(10px);
}
</style>
