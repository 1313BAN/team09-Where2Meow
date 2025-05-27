<template>
  <div class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
    <div class="bg-white rounded-xl max-w-2xl w-full max-h-[90vh] overflow-y-auto" @click.stop>
      <!-- 모달 헤더 -->
      <div class="flex items-center justify-between p-6 border-b border-gray-200">
        <h2 class="text-xl font-semibold text-gray-900">리뷰 수정</h2>
        <button
          @click="handleClose"
          class="p-2 text-gray-400 hover:text-gray-600 transition-colors"
        >
          <i class="pi pi-times text-xl"></i>
        </button>
      </div>

      <!-- 모달 내용 -->
      <div class="p-6">
        <!-- 리뷰 수정 폼 -->
        <form @submit.prevent="handleSubmit" class="space-y-6">
          <!-- 평점 선택 -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">평점</label>
            <div class="flex items-center gap-1">
              <button
                v-for="star in 5"
                :key="star"
                type="button"
                @click="setRating(star)"
                @mouseover="hoverRating = star"
                @mouseleave="hoverRating = 0"
                class="text-2xl transition-colors focus:outline-none"
                :class="star <= (hoverRating || rating) ? 'text-yellow-400' : 'text-gray-300'"
              >
                <i :class="['pi', star <= (hoverRating || rating) ? 'pi-star-fill' : 'pi-star']"></i>
              </button>
              <span class="ml-3 text-sm text-gray-600">
                {{ getRatingText(rating) }}
              </span>
            </div>
            <p v-if="errors.rating" class="mt-1 text-sm text-red-600">{{ errors.rating }}</p>
          </div>

          <!-- 리뷰 내용 -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">리뷰 내용</label>
            <textarea
              v-model="content"
              rows="6"
              placeholder="여행지에 대한 솔직한 후기를 작성해주세요."
              class="w-full border border-gray-300 rounded-xl px-4 py-3 focus:outline-none focus:border-[var(--secondary-color)] resize-none"
              :class="{ 'border-red-300': errors.content }"
            ></textarea>
            <div class="flex justify-between mt-2">
              <p v-if="errors.content" class="text-sm text-red-600">{{ errors.content }}</p>
              <p class="text-sm text-gray-500">{{ content.length }}/1000</p>
            </div>
          </div>

          <!-- 버튼 -->
          <div class="flex gap-3 pt-4">
            <button
              type="button"
              @click="handleClose"
              class="flex-1 px-4 py-3 border border-gray-300 rounded-xl text-gray-700 hover:bg-gray-50 transition-colors cursor-pointer"
            >
              취소
            </button>
            <button
              type="submit"
              :disabled="isSubmitting || !isValid || !hasChanges"
              class="flex-1 px-4 py-3 bg-gradient-to-r from-[var(--primary-color)] to-[var(--secondary-color)] text-white rounded-xl shadow-md hover:shadow-lg active:scale-95 transition-all duration-200 ease-in-out cursor-pointer disabled:opacity-50 disabled:cursor-not-allowed"
            >
              <span v-if="!isSubmitting">수정 완료</span>
              <span v-else class="flex items-center justify-center gap-2">
                <i class="pi pi-spinner pi-spin"></i>
                수정 중...
              </span>
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import reviewAPI from '@/api/review'
import { toast } from 'vue-sonner'

const props = defineProps({
  review: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['close', 'updated'])

// 폼 상태
const rating = ref(0)
const hoverRating = ref(0)
const content = ref('')
const isSubmitting = ref(false)
const errors = ref({})

// 원본 데이터
const originalRating = ref(0)
const originalContent = ref('')

// 평점 텍스트 매핑
const ratingTexts = {
  1: '별로예요',
  2: '그저 그래요',
  3: '괜찮아요',
  4: '좋아요',
  5: '최고예요'
}

// 폼 유효성 검사
const isValid = computed(() => {
  return rating.value > 0 && content.value.trim().length >= 10
})

// 변경 사항 확인
const hasChanges = computed(() => {
  return rating.value !== originalRating.value || content.value.trim() !== originalContent.value
})

// 평점 설정
const setRating = (star) => {
  rating.value = star
  if (errors.value.rating) {
    delete errors.value.rating
  }
}

// 평점 텍스트 반환
const getRatingText = (rating) => {
  return rating > 0 ? ratingTexts[rating] : '평점을 선택해주세요'
}

// 폼 유효성 검사
const validateForm = () => {
  errors.value = {}

  if (rating.value === 0) {
    errors.value.rating = '평점을 선택해주세요'
  }

  if (content.value.trim().length < 10) {
    errors.value.content = '리뷰는 10자 이상 작성해주세요'
  }

  if (content.value.length > 1000) {
    errors.value.content = '리뷰는 1000자 이하로 작성해주세요'
  }

  return Object.keys(errors.value).length === 0
}

// 폼 제출
const handleSubmit = async () => {
  if (!validateForm() || !hasChanges.value) return

  isSubmitting.value = true

  try {
    const reviewData = {
      rating: rating.value,
      content: content.value.trim()
    }

    reviewAPI.updateReview(
      props.review.reviewId,
      reviewData,
      (response) => {
        emit('updated', response.data)
        toast.success('리뷰가 수정되었습니다')
      },
      (error) => {
        console.error('리뷰 수정 실패:', error)
        toast.error('리뷰 수정에 실패했습니다')
      }
    )
  } catch (error) {
    console.error('리뷰 수정 중 오류:', error)
    toast.error('리뷰 수정 중 오류가 발생했습니다')
  } finally {
    isSubmitting.value = false
  }
}

// 모달 닫기
const handleClose = () => {
  if (hasChanges.value) {
    if (confirm('수정 중인 리뷰가 있습니다. 정말 닫으시겠습니까?')) {
      emit('close')
    }
  } else {
    emit('close')
  }
}

// 컴포넌트 마운트 시 기존 데이터 설정
onMounted(() => {
  if (props.review) {
    rating.value = props.review.rating
    content.value = props.review.content
    originalRating.value = props.review.rating
    originalContent.value = props.review.content
  }
})
</script>

<style scoped>
/* 모달 애니메이션 */
.fixed.inset-0 {
  animation: fadeIn 0.2s ease-out;
}

.bg-white.rounded-xl {
  animation: slideUp 0.3s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes slideUp {
  from {
    transform: translateY(20px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}
</style>
