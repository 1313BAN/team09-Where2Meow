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
          class="flex items-center gap-2 text-gray-600 hover:text-gray-900 transition-colors"
        >
          <i class="pi pi-arrow-left"></i>
          <span>뒤로 가기</span>
        </button>

        <!-- 여행지 기본 정보 -->
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-8">
          <!-- 이미지 섹션 -->
          <div class="space-y-4">
            <div class="relative overflow-hidden rounded-xl">
              <img
                v-if="attraction.firstimage"
                :src="attraction.firstimage"
                :alt="attraction.title"
                class="w-full h-96 object-cover"
                @error="handleImageError"
              />
              <img
                v-else
                :src="defaultImage"
                :alt="attraction.title"
                class="w-full h-96 object-cover"
              />
              
              <!-- 카테고리 배지 -->
              <div class="absolute top-4 left-4">
                <span class="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-white/90 text-gray-800 backdrop-blur-sm">
                  {{ getCategoryName(attraction.contenttypeid) }}
                </span>
              </div>
            </div>
            
            <!-- 추가 이미지 (있는 경우) -->
            <div v-if="attraction.firstimage2" class="grid grid-cols-2 gap-2">
              <img
                :src="attraction.firstimage2"
                :alt="attraction.title"
                class="w-full h-48 object-cover rounded-lg"
                @error="handleImageError"
              />
            </div>
          </div>

          <!-- 정보 섹션 -->
          <div class="space-y-6">
            <!-- 제목 및 기본 정보 -->
            <div>
              <h1 class="text-3xl font-bold text-gray-900 mb-2">{{ attraction.title }}</h1>
              <div class="flex items-center gap-4 text-gray-600 mb-4">
                <div class="flex items-center gap-1">
                  <i class="pi pi-map-marker"></i>
                  <span>{{ attraction.addr1 }}</span>
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
                    <i v-for="star in 5" :key="star" 
                       :class="['pi text-lg', star <= averageRating ? 'pi-star-fill text-yellow-400' : 'pi-star text-gray-300']">
                    </i>
                  </div>
                  <span class="text-lg font-medium text-gray-900">{{ averageRating.toFixed(1) }}</span>
                  <span class="text-gray-600">({{ reviewStats.totalReviews }}개 리뷰)</span>
                </div>
              </div>

              <!-- 액션 버튼들 -->
              <div class="flex gap-3 mb-6">
                <button
                  @click="toggleLike"
                  class="flex items-center gap-2 px-4 py-2 border border-gray-300 rounded-xl hover:bg-gray-50 transition-colors"
                >
                  <i :class="['pi', isLiked ? 'pi-heart-fill text-red-500' : 'pi-heart text-gray-600']"></i>
                  <span>좋아요</span>
                </button>
                <button
                  @click="toggleBookmark"
                  class="flex items-center gap-2 px-4 py-2 border border-gray-300 rounded-xl hover:bg-gray-50 transition-colors"
                >
                  <i :class="['pi', isBookmarked ? 'pi-bookmark-fill text-yellow-500' : 'pi-bookmark text-gray-600']"></i>
                  <span>저장</span>
                </button>
                <button
                  @click="shareAttraction"
                  class="flex items-center gap-2 px-4 py-2 border border-gray-300 rounded-xl hover:bg-gray-50 transition-colors"
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

            <!-- 지도 (추후 구현) -->
            <div class="bg-gray-100 rounded-xl p-6 h-64 flex items-center justify-center">
              <div class="text-center text-gray-500">
                <i class="pi pi-map text-2xl mb-2"></i>
                <p>지도는 준비 중입니다</p>
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

          <!-- 리뷰 통계 -->
          <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
            <div class="text-center bg-white rounded-lg p-4">
              <div class="text-2xl font-bold text-[var(--primary-color)] mb-1">{{ averageRating.toFixed(1) }}</div>
              <div class="flex justify-center mb-2">
                <div class="flex">
                  <i v-for="star in 5" :key="star" 
                     :class="['pi text-sm', star <= averageRating ? 'pi-star-fill text-yellow-400' : 'pi-star text-gray-300']">
                  </i>
                </div>
              </div>
              <div class="text-sm text-gray-600">평균 평점</div>
            </div>
            <div class="text-center bg-white rounded-lg p-4">
              <div class="text-2xl font-bold text-[var(--primary-color)] mb-1">{{ reviewStats.totalReviews }}</div>
              <div class="text-sm text-gray-600">전체 리뷰</div>
            </div>
            <div class="text-center bg-white rounded-lg p-4">
              <div class="text-2xl font-bold text-[var(--primary-color)] mb-1">{{ reviewStats.recommendationRate }}%</div>
              <div class="text-sm text-gray-600">추천율</div>
            </div>
          </div>

          <!-- 리뷰 목록 -->
          <ReviewList 
            :attraction-id="attractionId"
            @review-updated="loadAttractionDetail"
          />
        </div>
      </div>

      <!-- 에러 상태 -->
      <div v-else class="text-center py-12">
        <div class="w-16 h-16 rounded-full bg-gray-100 flex items-center justify-center mx-auto mb-4">
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
      :attraction-title="attraction?.title"
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
const isLiked = ref(false)
const isBookmarked = ref(false)
const showReviewModal = ref(false)
const categories = ref([])

// 기본 이미지
const defaultImage = 'https://images.unsplash.com/photo-1469474968028-56623f02e42e?w=800&h=600&fit=crop&crop=center'

// 임시 리뷰 통계 (추후 실제 데이터와 연동)
const reviewStats = ref({
  totalReviews: 23,
  recommendationRate: 87
})

const averageRating = computed(() => {
  // 임시 평점 (추후 실제 리뷰 데이터로 계산)
  return 4.2
})

// 여행지 상세 정보 로드
const loadAttractionDetail = async () => {
  if (!attractionId.value) return

  isLoading.value = true

  try {
    attractionAPI.getAttractionDetail(
      attractionId.value,
      (response) => {
        attraction.value = response.data
        console.log('여행지 상세 정보:', response.data)
      },
      (error) => {
        console.error('여행지 상세 정보 로드 실패:', error)
        toast.error('여행지 정보를 불러오는데 실패했습니다')
      }
    )
  } catch (error) {
    console.error('여행지 상세 정보 로드 중 오류:', error)
  } finally {
    isLoading.value = false
  }
}

// 유틸리티 함수들
const getCategoryName = (categoryId) => {
  const category = categories.value.find(cat => cat.categoryId === categoryId)
  return category ? category.categoryName : '기타'
}

const handleImageError = (event) => {
  event.target.src = defaultImage
}

// 이벤트 핸들러들
const goBack = () => {
  router.go(-1)
}

const toggleLike = () => {
  if (!isLoggedIn.value) {
    toast.error('로그인이 필요합니다')
    return
  }
  
  isLiked.value = !isLiked.value
  toast.success(isLiked.value ? '좋아요를 눌렀습니다' : '좋아요를 취소했습니다')
}

const toggleBookmark = () => {
  if (!isLoggedIn.value) {
    toast.error('로그인이 필요합니다')
    return
  }
  
  isBookmarked.value = !isBookmarked.value
  toast.success(isBookmarked.value ? '북마크에 저장했습니다' : '북마크에서 제거했습니다')
}

const shareAttraction = async () => {
  const url = window.location.href
  
  if (navigator.share) {
    try {
      await navigator.share({
        title: attraction.value?.title,
        text: `${attraction.value?.title} - Where2Meow에서 확인해보세요`,
        url: url
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
    attractionAPI.attractionApi.getAllCategories()
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
@media (max-width: 1024px) {
  .grid.grid-cols-1.lg\:grid-cols-2 {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .grid.grid-cols-1.md\:grid-cols-3 {
    grid-template-columns: 1fr;
  }
}
</style>
