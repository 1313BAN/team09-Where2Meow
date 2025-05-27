<template>
  <div class="bg-white rounded-lg p-6 border border-gray-200">
    <div class="flex items-start justify-between mb-4">
      <!-- 사용자 정보 -->
      <div class="flex items-center gap-3">
        <div class="w-10 h-10 rounded-full bg-gradient-to-r from-[var(--primary-color)] to-[var(--secondary-color)] flex items-center justify-center text-white font-semibold">
          {{ getUserInitial(review.userName) }}
        </div>
        <div>
          <div class="font-medium text-gray-900">{{ review.userName || '익명' }}</div>
          <div class="text-sm text-gray-500">{{ formatDate(review.createdAt) }}</div>
        </div>
      </div>

      <!-- 내 리뷰인 경우 수정/삭제 버튼 -->
      <div v-if="isMyReview" class="flex items-center gap-2">
        <button
          @click="handleEdit"
          class="p-2 text-gray-400 hover:text-blue-500 transition-colors"
          title="수정"
        >
          <i class="pi pi-pencil"></i>
        </button>
        <button
          @click="handleDelete"
          class="p-2 text-gray-400 hover:text-red-500 transition-colors"
          title="삭제"
        >
          <i class="pi pi-trash"></i>
        </button>
      </div>
    </div>

    <!-- 평점 -->
    <div class="flex items-center gap-2 mb-3">
      <div class="flex">
        <i v-for="star in 5" :key="star" 
           :class="['pi text-sm', star <= review.rating ? 'pi-star-fill text-yellow-400' : 'pi-star text-gray-300']">
        </i>
      </div>
      <span class="text-sm text-gray-600">{{ review.rating }}.0</span>
      <span v-if="review.updatedAt !== review.createdAt" class="text-xs text-gray-400 ml-2">
        (수정됨)
      </span>
    </div>

    <!-- 리뷰 내용 -->
    <div class="mb-4">
      <p class="text-gray-700 leading-relaxed">{{ review.content }}</p>
    </div>

    <!-- 리뷰 이미지 (있는 경우) -->
    <div v-if="review.images && review.images.length > 0" class="mb-4">
      <div class="grid grid-cols-2 md:grid-cols-3 gap-2">
        <img
          v-for="(image, index) in review.images"
          :key="index"
          :src="image.url"
          :alt="`리뷰 이미지 ${index + 1}`"
          class="w-full h-24 object-cover rounded-lg cursor-pointer hover:opacity-80 transition-opacity"
          @click="openImageModal(image.url)"
        />
      </div>
    </div>

    <!-- 리뷰 액션 -->
    <div class="flex items-center justify-between pt-4 border-t border-gray-100">
      <div class="flex items-center gap-4">
        <!-- 좋아요 버튼 -->
        <button
          @click="handleLike"
          class="flex items-center gap-1 text-sm transition-colors"
          :class="review.isLiked ? 'text-red-500' : 'text-gray-500 hover:text-red-500'"
        >
          <i :class="['pi', review.isLiked ? 'pi-heart-fill' : 'pi-heart']"></i>
          <span>{{ review.likeCount || 0 }}</span>
        </button>

        <!-- 답글 버튼 (추후 구현) -->
        <button class="flex items-center gap-1 text-sm text-gray-500 hover:text-gray-700 transition-colors">
          <i class="pi pi-comment"></i>
          <span>답글</span>
        </button>
      </div>

      <!-- 신고 버튼 (내 리뷰가 아닌 경우) -->
      <button
        v-if="!isMyReview"
        @click="reportReview"
        class="text-sm text-gray-400 hover:text-gray-600 transition-colors"
      >
        신고
      </button>
    </div>
  </div>

  <!-- 이미지 확대 모달 -->
  <div
    v-if="showImageModal"
    class="fixed inset-0 bg-black bg-opacity-75 flex items-center justify-center z-50"
    @click="closeImageModal"
  >
    <div class="relative max-w-4xl max-h-full p-4">
      <img
        :src="selectedImage"
        alt="확대된 이미지"
        class="max-w-full max-h-full object-contain"
      />
      <button
        @click="closeImageModal"
        class="absolute top-4 right-4 text-white text-2xl hover:text-gray-300 transition-colors"
      >
        <i class="pi pi-times"></i>
      </button>
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { storeToRefs } from 'pinia'
import { toast } from 'vue-sonner'

const authStore = useAuthStore()
const { userId } = storeToRefs(authStore)

const props = defineProps({
  review: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['like', 'edit', 'delete'])

// 상태 관리
const showImageModal = ref(false)
const selectedImage = ref('')

// 내 리뷰인지 확인
const isMyReview = computed(() => {
  return userId.value && userId.value === props.review.userId
})

// 사용자 이름 첫 글자 추출
const getUserInitial = (userName) => {
  if (!userName) return '?'
  return userName.charAt(0).toUpperCase()
}

// 날짜 포맷팅
const formatDate = (dateString) => {
  if (!dateString) return ''

  try {
    const date = new Date(dateString)
    const now = new Date()
    const diffMs = now - date
    const diffMins = Math.floor(diffMs / 60000)
    const diffHours = Math.floor(diffMs / 3600000)
    const diffDays = Math.floor(diffMs / 86400000)

    if (diffMins < 1) return '방금 전'
    if (diffMins < 60) return `${diffMins}분 전`
    if (diffHours < 24) return `${diffHours}시간 전`
    if (diffDays < 7) return `${diffDays}일 전`
    return date.toLocaleDateString('ko-KR', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    })
  } catch {
    return '날짜 오류'
  }
}

// 이벤트 핸들러들
const handleLike = () => {
  emit('like', props.review)
}

const handleEdit = () => {
  emit('edit', props.review)
}

const handleDelete = () => {
  emit('delete', props.review)
}

const reportReview = () => {
  toast.info('신고 기능은 준비 중입니다')
}

const openImageModal = (imageUrl) => {
  selectedImage.value = imageUrl
  showImageModal.value = true
}

const closeImageModal = () => {
  showImageModal.value = false
  selectedImage.value = ''
}
</script>

<style scoped>
/* 이미지 모달 스타일 */
.fixed.inset-0 {
  backdrop-filter: blur(4px);
}
</style>
