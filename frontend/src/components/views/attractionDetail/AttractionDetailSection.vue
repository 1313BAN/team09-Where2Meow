<template>
  <div class="min-h-screen bg-white">
    <div class="container mx-auto px-4 sm:px-6 lg:px-8 max-w-7xl py-8">
      <!-- 로딩 상태 -->
      <div v-if="isLoading" class="flex justify-center py-12">
        <div class="flex items-center gap-3 text-gray-600">
          <i class="pi pi-spinner pi-spin text-xl"></i>
          <span>여행지 정보를 불러오는 중...</span>
        </div>
      </div>

      <!-- 여행지 상세 정보 -->
      <div v-else-if="attraction" class="space-y-8">
        <!-- 뒤로 가기 버튼 -->
        <button
          @click="goBack"
          class="cursor-pointer flex items-center gap-2 text-gray-600 hover:text-gray-900 transition-colors"
        >
          <i class="pi pi-arrow-left"></i>
          <span>뒤로 가기</span>
        </button>

        <!-- 여행지 기본 정보 -->
        <div class="space-y-8">
          <!-- 이미지 섹션 -->
          <div class="space-y-4">
            <div class="relative overflow-hidden rounded-xl">
              <img
                v-if="
                  (attraction.image && attraction.image !== '/images/default-attraction.jpg') ||
                  attraction.firstImage1
                "
                :src="
                  attraction.firstImage1 ||
                  (attraction.image !== '/images/default-attraction.jpg' ? attraction.image : null)
                "
                :alt="attraction.attractionName"
                class="w-full h-96 md:h-[500px] object-cover"
                @error="handleImageError"
              />
              <img
                v-else
                :src="defaultImage"
                :alt="attraction.attractionName"
                class="w-full h-96 md:h-[500px] object-cover"
              />

              <!-- 카테고리 배지 -->
              <div class="category-badge">{{ getCategoryName(attraction.categoryId) }}</div>
            </div>
          </div>

          <!-- 정보 섹션 -->
          <div class="space-y-6">
            <!-- 제목 및 기본 정보 -->
            <div>
              <h1 class="text-3xl font-bold text-gray-900 mb-2">{{ attraction.attractionName }}</h1>
              <div class="flex items-center gap-4 text-gray-600 mb-4">
                <div class="flex items-center gap-1">
                  <i class="pi pi-map-marker"></i>
                  <span>{{ getAddressDisplay(attraction) }}</span>
                </div>
                <div v-if="attraction.tel" class="flex items-center gap-1">
                  <i class="pi pi-phone"></i>
                  <span>{{ attraction.tel }}</span>
                </div>
              </div>

              <!-- 평점 표시 -->
              <div class="flex items-center gap-4 mb-6">
                <div class="flex items-center gap-2">
                  <div class="flex">
                    <i
                      v-for="star in 5"
                      :key="star"
                      :class="[
                        'pi text-lg',
                        star <= averageRating
                          ? 'pi-star-fill text-yellow-400'
                          : 'pi-star text-gray-300',
                      ]"
                    >
                    </i>
                  </div>
                  <span class="text-lg font-medium text-gray-900">{{
                    averageRating.toFixed(1)
                  }}</span>
                  <span class="text-gray-600">({{ reviewStats.totalReviews }}개 리뷰)</span>
                </div>
                <button
                  @click="shareAttraction"
                  class="cursor-pointer flex items-center gap-2 px-4 py-2 border border-gray-300 rounded-xl hover:bg-gray-50 transition-colors"
                >
                  <i class="pi pi-share-alt"></i>
                  <span>공유</span>
                </button>
              </div>
            </div>

            <!-- 상세 설명 -->
            <div v-if="attraction.overview" class="bg-gray-50 rounded-xl p-6">
              <h3 class="text-lg font-semibold text-gray-900 mb-3">상세 정보</h3>
              <p class="text-gray-700 leading-relaxed">{{ attraction.overview }}</p>
            </div>

            <!-- 지도 -->
            <div
              v-if="attraction.latitude && attraction.longitude"
              class="bg-gray-50 rounded-xl p-6"
            >
              <h3 class="text-lg font-semibold text-gray-900 mb-4">위치</h3>
              <GoogleMap
                :latitude="attraction.latitude"
                :longitude="attraction.longitude"
                :marker-title="attraction.attractionName"
                height="300px"
                :zoom="15"
              />
            </div>

            <!-- 지도 데이터가 없는 경우 -->
            <div v-else class="bg-gray-100 rounded-xl p-6 h-64 flex items-center justify-center">
              <div class="text-center text-gray-500">
                <i class="pi pi-map text-2xl mb-2"></i>
                <p>위치 정보가 없습니다</p>
              </div>
            </div>
          </div>
        </div>

        <!-- 리뷰 섹션 -->
        <div class="bg-gray-50 rounded-xl p-6">
          <div class="flex items-center justify-between mb-6">
            <h2 class="text-2xl font-bold text-gray-900">리뷰</h2>
            <button
              @click="openReviewModal"
              class="px-4 py-2 bg-gradient-to-r from-[var(--primary-color)] to-[var(--secondary-color)] text-white rounded-xl shadow-md hover:shadow-lg active:scale-95 transition-all duration-200 ease-in-out cursor-pointer"
            >
              리뷰 작성
            </button>
          </div>

          <!-- 리뷰 목록 -->
          <ReviewList :attraction-id="attractionId" @review-updated="loadAttractionDetail" />
        </div>
      </div>

      <!-- 에러 상태 -->
      <div v-else class="text-center py-12">
        <div
          class="w-16 h-16 rounded-full bg-gray-100 flex items-center justify-center mx-auto mb-4"
        >
          <i class="pi pi-exclamation-triangle text-gray-400 text-2xl"></i>
        </div>
        <h3 class="text-lg font-medium text-gray-900 mb-2">여행지를 찾을 수 없습니다</h3>
        <p class="text-gray-600 mb-4">요청하신 여행지 정보를 불러올 수 없습니다.</p>
        <button
          @click="goBack"
          class="px-4 py-2 bg-gradient-to-r from-[var(--primary-color)] to-[var(--secondary-color)] text-white rounded-xl shadow-md hover:shadow-lg active:scale-95 transition-all duration-200 ease-in-out cursor-pointer"
        >
          뒤로 가기
        </button>
      </div>
    </div>

    <!-- 리뷰 작성 모달 -->
    <ReviewModal
      v-if="showReviewModal"
      :attraction-id="attractionId"
      :attraction-title="attraction?.attractionName"
      @close="closeReviewModal"
      @review-created="handleReviewCreated"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { storeToRefs } from 'pinia'
import attractionAPI from '@/api/attraction'
import ReviewList from './ReviewList.vue'
import ReviewModal from './ReviewModal.vue'
import GoogleMap from '@/components/common/map/GoogleMap.vue'
import { toast } from 'vue-sonner'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const { isLoggedIn } = storeToRefs(authStore)

// Props
const attractionId = computed(() => parseInt(route.params.attractionId))

// 상태 관리
const attraction = ref(null)
const isLoading = ref(false)
const showReviewModal = ref(false)
const categories = ref([])

// 기본 이미지
const defaultImage =
  'https://images.unsplash.com/photo-1469474968028-56623f02e42e?w=800&h=600&fit=crop&crop=center'

// 리뷰 통계
const reviewStats = computed(() => {
  const totalReviews = attraction.value?.reviews?.length || 0
  const recommendedCount =
    attraction.value?.reviews?.filter((review) => review.isRecommended).length || 0
  const recommendationRate =
    totalReviews > 0 ? ((recommendedCount / totalReviews) * 100).toFixed(1) : 0

  return {
    totalReviews,
    recommendationRate,
  }
})

const averageRating = computed(() => {
  return attraction.value?.reviewAvgScore || 0
})

// 여행지 상세 정보 로드
const loadAttractionDetail = async () => {
  if (!attractionId.value) return

  isLoading.value = true

  try {
    // attractionApi 객체의 getAttractionDetail 메서드 사용 (Promise 반환)
    const response = await attractionAPI.attractionApi.getAttractionDetail(attractionId.value)
    attraction.value = response.data
    console.log('여행지 상세 정보 로드 성공:', response.data)
  } catch (error) {
    console.error('여행지 상세 정보 로드 실패:', error)
    toast.error('여행지 정보를 불러오는데 실패했습니다')
    attraction.value = null
  } finally {
    isLoading.value = false
  }
}

// 유틸리티 함수들
const getCategoryName = (categoryId) => {
  // API 응답에서 categoryName이 직접 제공되므로 이를 사용
  return attraction.value?.categoryName || '기타'
}

const getAddressDisplay = (attractionData) => {
  if (attractionData.addr1 && attractionData.addr1.trim()) {
    return attractionData.addr1
  }
  // addr1이 비어있으면 stateName과 cityName으로 구성
  const parts = []
  if (attractionData.stateName) parts.push(attractionData.stateName)
  if (attractionData.cityName) parts.push(attractionData.cityName)
  return parts.join(' ') || '주소 정보 없음'
}

const handleImageError = (event) => {
  event.target.src = defaultImage
}

// 이벤트 핸들러들
const goBack = () => {
  router.go(-1)
}

const shareAttraction = async () => {
  const url = window.location.href

  if (navigator.share) {
    try {
      await navigator.share({
        title: attraction.value?.attractionName,
        text: `${attraction.value?.attractionName} - Where2Meow에서 확인해보세요`,
        url: url,
      })
    } catch (error) {
      if (error.name !== 'AbortError') {
        console.error('공유 실패:', error)
      }
    }
  } else {
    // 클립보드에 복사
    try {
      await navigator.clipboard.writeText(url)
      toast.success('링크가 클립보드에 복사되었습니다')
    } catch (error) {
      console.error('클립보드 복사 실패:', error)
      toast.error('링크 복사에 실패했습니다')
    }
  }
}

const openReviewModal = () => {
  if (!isLoggedIn.value) {
    toast.error('로그인이 필요합니다')
    router.push('/login')
    return
  }

  showReviewModal.value = true
}

const closeReviewModal = () => {
  showReviewModal.value = false
}

const handleReviewCreated = () => {
  closeReviewModal()
  loadAttractionDetail()
  toast.success('리뷰가 작성되었습니다')
}

// 컴포넌트 마운트 시 데이터 로드
onMounted(async () => {
  // 카테고리 데이터 로드
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

  loadAttractionDetail()
})
</script>

<style scoped>
/* 반응형 그리드 조정 */
@media (max-width: 768px) {
  .grid.grid-cols-1.md\:grid-cols-3 {
    grid-template-columns: 1fr;
  }
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
