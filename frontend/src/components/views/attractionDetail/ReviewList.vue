<template>
  <div class="space-y-4">
    <!-- 리뷰 정렬 옵션 -->
    <div class="flex items-center justify-between">
      <div class="flex items-center gap-4">
        <span class="text-sm text-gray-600">정렬:</span>
        <select
          v-model="sortOption"
          @change="loadReviews"
          class="text-sm border border-gray-300 rounded-lg px-3 py-1 focus:outline-none focus:border-[var(--secondary-color)]"
        >
          <option value="createdAt-desc">최신순</option>
          <option value="createdAt-asc">오래된순</option>
          <option value="rating-desc">평점 높은순</option>
          <option value="rating-asc">평점 낮은순</option>
        </select>
      </div>
    </div>

    <!-- 로딩 상태 -->
    <div v-if="isLoading" class="flex justify-center py-8">
      <div class="flex items-center gap-2 text-gray-600">
        <i class="pi pi-spinner pi-spin"></i>
        <span>리뷰를 불러오는 중...</span>
      </div>
    </div>

    <!-- 리뷰 목록 -->
    <div v-else-if="reviews.length > 0" class="space-y-4">
      <ReviewItem
        v-for="review in reviews"
        :key="review.reviewId"
        :review="review"
        @like="handleReviewLike"
        @edit="handleReviewEdit"
        @delete="handleReviewDelete"
      />

      <!-- 더 보기 버튼 -->
      <div v-if="hasMore" class="text-center">
        <button
          @click="loadMoreReviews"
          :disabled="isLoadingMore"
          class="px-6 py-2 border border-gray-300 rounded-xl text-gray-700 hover:bg-gray-50 transition-colors disabled:opacity-50"
        >
          <span v-if="!isLoadingMore">더 보기</span>
          <span v-else class="flex items-center gap-2">
            <i class="pi pi-spinner pi-spin"></i>
            불러오는 중...
          </span>
        </button>
      </div>
    </div>

    <!-- 리뷰 없음 -->
    <div v-else class="text-center py-8">
      <div class="w-12 h-12 rounded-full bg-gray-100 flex items-center justify-center mx-auto mb-3">
        <i class="pi pi-comment text-gray-400 text-xl"></i>
      </div>
      <h3 class="text-lg font-medium text-gray-900 mb-2">아직 리뷰가 없습니다</h3>
      <p class="text-gray-600">첫 번째 리뷰를 작성해보세요!</p>
    </div>

    <!-- 리뷰 수정 모달 -->
    <ReviewEditModal
      v-if="showEditModal"
      :review="editingReview"
      @close="closeEditModal"
      @updated="handleReviewUpdated"
    />

    <!-- 리뷰 삭제 확인 모달 -->
    <div
      v-if="showDeleteModal"
      class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50"
      @click="showDeleteModal = false"
    >
      <div class="bg-white rounded-lg p-6 max-w-md w-full mx-4" @click.stop>
        <div class="text-center">
          <div class="w-16 h-16 rounded-full bg-red-100 flex items-center justify-center mx-auto mb-4">
            <i class="pi pi-exclamation-triangle text-red-500 text-2xl"></i>
          </div>
          <h3 class="text-lg font-semibold text-gray-900 mb-2">리뷰 삭제</h3>
          <p class="text-gray-600 mb-6">
            정말로 이 리뷰를 삭제하시겠습니까?<br />
            삭제된 리뷰는 복구할 수 없습니다.
          </p>
          <div class="flex gap-3">
            <button
              @click="showDeleteModal = false"
              class="flex-1 px-4 py-2 border border-gray-300 rounded-xl text-gray-700 hover:bg-gray-50 transition-colors cursor-pointer"
            >
              취소
            </button>
            <button
              @click="confirmDelete"
              :disabled="isDeleting"
              class="flex-1 px-4 py-2 bg-red-500 text-white rounded-xl hover:bg-red-600 disabled:opacity-50 disabled:cursor-not-allowed transition-colors cursor-pointer"
            >
              <span v-if="!isDeleting">삭제하기</span>
              <span v-else class="flex items-center justify-center gap-2">
                <i class="pi pi-spinner pi-spin"></i>
                삭제 중...
              </span>
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { storeToRefs } from 'pinia'
import reviewAPI from '@/api/review'
import ReviewItem from './ReviewItem.vue'
import ReviewEditModal from './ReviewEditModal.vue'
import { toast } from 'vue-sonner'

const authStore = useAuthStore()
const { userId } = storeToRefs(authStore)

const props = defineProps({
  attractionId: {
    type: Number,
    required: true
  }
})

const emit = defineEmits(['review-updated'])

// 상태 관리
const reviews = ref([])
const isLoading = ref(false)
const isLoadingMore = ref(false)
const sortOption = ref('createdAt-desc')
const currentPage = ref(0)
const hasMore = ref(true)
const pageSize = 10

// 모달 상태
const showEditModal = ref(false)
const showDeleteModal = ref(false)
const editingReview = ref(null)
const deletingReviewId = ref(null)
const isDeleting = ref(false)

// 리뷰 목록 로드
const loadReviews = async (reset = true) => {
  if (reset) {
    isLoading.value = true
    currentPage.value = 0
    reviews.value = []
  } else {
    isLoadingMore.value = true
  }

  try {
    const page = reset ? 0 : currentPage.value + 1
    
    reviewAPI.getReviewsByAttraction(
      props.attractionId,
      userId.value, // UUID 대신 사용자 ID 사용
      page,
      pageSize,
      (response) => {
        const newReviews = response.data?.content || []
        
        if (reset) {
          reviews.value = newReviews
        } else {
          reviews.value.push(...newReviews)
        }
        
        currentPage.value = page
        hasMore.value = newReviews.length === pageSize
      },
      (error) => {
        console.error('리뷰 목록 로드 실패:', error)
        toast.error('리뷰를 불러오는데 실패했습니다')
      }
    )
  } catch (error) {
    console.error('리뷰 로드 중 오류:', error)
  } finally {
    isLoading.value = false
    isLoadingMore.value = false
  }
}

// 더 많은 리뷰 로드
const loadMoreReviews = () => {
  if (!isLoadingMore.value && hasMore.value) {
    loadReviews(false)
  }
}

// 리뷰 좋아요 토글
const handleReviewLike = (review) => {
  reviewAPI.toggleReviewLike(
    review.reviewId,
    userId.value,
    (response) => {
      // 리뷰 목록에서 해당 리뷰의 좋아요 상태 업데이트
      const reviewIndex = reviews.value.findIndex(r => r.reviewId === review.reviewId)
      if (reviewIndex !== -1) {
        reviews.value[reviewIndex] = { ...reviews.value[reviewIndex], ...response.data }
      }
    },
    (error) => {
      console.error('리뷰 좋아요 토글 실패:', error)
      toast.error('좋아요 처리에 실패했습니다')
    }
  )
}

// 리뷰 수정
const handleReviewEdit = (review) => {
  editingReview.value = review
  showEditModal.value = true
}

// 리뷰 삭제
const handleReviewDelete = (review) => {
  deletingReviewId.value = review.reviewId
  showDeleteModal.value = true
}

// 수정 모달 닫기
const closeEditModal = () => {
  showEditModal.value = false
  editingReview.value = null
}

// 리뷰 수정 완료
const handleReviewUpdated = () => {
  closeEditModal()
  loadReviews()
  emit('review-updated')
  toast.success('리뷰가 수정되었습니다')
}

// 리뷰 삭제 확인
const confirmDelete = async () => {
  if (!deletingReviewId.value) return

  isDeleting.value = true

  try {
    reviewAPI.deleteReview(
      deletingReviewId.value,
      userId.value,
      () => {
        loadReviews()
        emit('review-updated')
        toast.success('리뷰가 삭제되었습니다')
      },
      (error) => {
        console.error('리뷰 삭제 실패:', error)
        toast.error('리뷰 삭제에 실패했습니다')
      }
    )
  } catch (error) {
    console.error('리뷰 삭제 중 오류:', error)
  } finally {
    isDeleting.value = false
    showDeleteModal.value = false
    deletingReviewId.value = null
  }
}

// 컴포넌트 마운트 시 리뷰 로드
onMounted(() => {
  loadReviews()
})
</script>
