<template>
  <div class="bg-white rounded-lg p-4 border border-gray-200 hover:shadow-sm transition-shadow">
    <div class="flex items-start gap-3">
      <!-- 사용자 아바타 -->
      <div
        class="w-10 h-10 rounded-full bg-gradient-to-r from-[var(--primary-color)] to-[var(--secondary-color)] flex items-center justify-center text-white font-semibold flex-shrink-0"
      >
        {{ getUserInitial(review.userNickname) }}
      </div>

      <!-- 리뷰 내용 -->
      <div class="flex-1 min-w-0">
        <!-- 헤더: 사용자명, 평점, 날짜 -->
        <div class="flex items-center justify-between mb-2">
          <div class="flex items-center gap-3">
            <span class="font-medium text-gray-900">{{ review.userNickname || '익명' }}</span>
            
            <!-- 평점 -->
            <div class="flex items-center gap-1">
              <div class="flex">
                <i
                  v-for="star in 5"
                  :key="star"
                  :class="[
                    'pi text-sm',
                    star <= review.score ? 'pi-star-fill text-yellow-400' : 'pi-star text-gray-300',
                  ]"
                >
                </i>
              </div>
              <span class="text-sm text-gray-500 ml-1">{{ review.score }}.0</span>
            </div>
          </div>

          <!-- 내 리뷰인 경우 수정/삭제 버튼 -->
          <div v-if="isMyReview" class="flex items-center gap-1">
            <button
              @click="handleEdit"
              class="p-1 text-gray-400 hover:text-blue-500 transition-colors"
              title="수정"
            >
              <i class="pi pi-pencil"></i>
            </button>
            <button
              @click="handleDelete"
              class="p-1 text-gray-400 hover:text-red-500 transition-colors"
              title="삭제"
            >
              <i class="pi pi-trash"></i>
            </button>
          </div>
        </div>

        <!-- 리뷰 내용 -->
        <div class="mb-3">
          <p class="text-gray-700 leading-relaxed">{{ review.content }}</p>
        </div>

        <!-- 하단: 날짜, 좋아요 -->
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-3 text-sm text-gray-500">
            <span>{{ formatDate(review.createdAt) }}</span>

          </div>

          <!-- 좋아요 버튼 -->
          <button
            @click="handleLike"
            class="flex items-center gap-1 text-sm transition-colors py-1 px-2 rounded hover:bg-gray-50"
                        :class="review.isLiked ? 'text-red-500' : 'text-gray-500 hover:text-red-500'"
          >
                        <i :class="['pi', review.isLiked ? 'pi-heart-fill' : 'pi-heart']"></i>
            <span>{{ review.likeCount || 0 }}</span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { storeToRefs } from 'pinia'

const authStore = useAuthStore()
const { userUuid } = storeToRefs(authStore)

const props = defineProps({
  review: {
    type: Object,
    required: true,
  },
})

const emit = defineEmits(['like', 'edit', 'delete'])

// 내 리뷰인지 확인 (UUID 기준)
const isMyReview = computed(() => {
  return userUuid.value && userUuid.value === props.review.userUuid
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
      month: 'short',
      day: 'numeric',
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
</script>

<style scoped>
/* 호버 효과를 위한 부드러운 전환 */
.transition-shadow {
  transition: box-shadow 0.2s ease-in-out;
}
</style>
