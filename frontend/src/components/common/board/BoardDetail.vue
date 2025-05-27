<template>
  <div class="min-h-screen bg-gray-50">
    <div class="container mx-auto px-4 py-8">
      <div class="max-w-4xl mx-auto">
        <!-- 로딩 상태 -->
        <div v-if="isLoading" class="flex justify-center py-12">
          <div class="flex items-center gap-3 text-gray-600">
            <i class="pi pi-spinner pi-spin text-xl"></i>
            <span>게시글을 불러오는 중...</span>
          </div>
        </div>

        <!-- 에러 상태 -->
        <div v-else-if="error" class="text-center py-12">
          <div
            class="w-16 h-16 rounded-full bg-red-100 flex items-center justify-center mx-auto mb-4"
          >
            <i class="pi pi-exclamation-triangle text-red-500 text-2xl"></i>
          </div>
          <h3 class="text-lg font-medium text-gray-900 mb-2">게시글을 불러올 수 없습니다</h3>
          <p class="text-gray-600 mb-4">{{ error }}</p>
          <button
            @click="$router.go(-1)"
            class="px-4 py-2 bg-gray-500 text-white rounded-lg hover:bg-gray-600 transition-colors cursor-pointer"
          >
            돌아가기
          </button>
        </div>

        <!-- 게시글 내용 -->
        <div v-else-if="board" class="bg-white rounded-lg shadow-sm border border-gray-200">
          <!-- 게시글 헤더 -->
          <div class="p-6 border-b border-gray-200">
            <div class="flex items-center justify-between mb-4">
              <div class="flex items-center gap-3">
                <span
                  class="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-[var(--primary-10)] text-[var(--primary-color)]"
                >
                  {{ board.categoryName || '일반' }}
                </span>

              </div>
              <button
                @click="$router.go(-1)"
                class="p-2 rounded-lg text-gray-400 hover:text-gray-600 hover:bg-gray-50 transition-colors cursor-pointer"
                title="돌아가기"
              >
                <i class="pi pi-times text-xl"></i>
              </button>
            </div>

            <h1 class="text-2xl font-bold text-gray-900 mb-4">{{ board.title || '제목 없음' }}</h1>

            <div class="flex items-center justify-between">
              <div class="flex items-center gap-4">
                <div class="flex items-center gap-3">
                  <div
                    class="w-10 h-10 rounded-full flex items-center justify-center"
                    style="background: var(--gradient-primary)"
                  >
                    <span class="text-white text-sm font-bold">{{
                      (board.username || '').charAt(0) || '?'
                    }}</span>
                  </div>
                  <div>
                    <p class="font-medium text-gray-900">{{ board.username || '익명' }}</p>
                    <p class="text-sm text-gray-500">
                      {{ board.createdAt ? formatRelativeTime(board.createdAt) : '날짜 정보 없음' }}
                    </p>
                  </div>
                </div>
              </div>

              <div class="flex items-center justify-between">
                <div class="flex items-center gap-4 text-sm text-gray-500">
                  <span class="flex items-center gap-1">
                    <i class="pi pi-eye"></i>
                    {{ board.viewCount || 0 }}
                  </span>
                  <span class="flex items-center gap-1">
                    <i class="pi pi-heart" :class="{ 'text-red-500': board.isLiked }"></i>
                    {{ board.likeCount || 0 }}
                  </span>
                  <span class="flex items-center gap-1">
                    <i class="pi pi-comment"></i>
                    {{ comments.length }}
                  </span>
                </div>

                <!-- 내 글일 경우 수정/삭제 버튼 -->
                <div v-if="isMyPost" class="flex items-center gap-2">
                  <button
                    @click="editPost"
                    class="p-2 text-gray-400 hover:text-blue-500 transition-colors"
                    title="수정"
                  >
                    <i class="pi pi-pencil"></i>
                  </button>
                  <button
                    @click="showDeleteModal = true"
                    class="p-2 text-gray-400 hover:text-red-500 transition-colors"
                    title="삭제"
                  >
                    <i class="pi pi-trash"></i>
                  </button>
                </div>
              </div>
            </div>
          </div>

          <!-- 게시글 본문 -->
          <div class="p-6 border-b border-gray-200">
            <div class="prose max-w-none">
              <p class="text-gray-800 whitespace-pre-wrap">
                {{ board.content || '내용이 없습니다.' }}
              </p>
            </div>
          </div>

          <!-- 액션 버튼 -->
          <div class="p-6 border-b border-gray-200">
            <div class="flex items-center gap-3">
              <button
                @click="toggleLike"
                :disabled="isLikeProcessing"
                class="flex items-center gap-2 px-4 py-2 rounded-lg border transition-colors cursor-pointer disabled:opacity-50"
                :class="
                  board.isLiked
                    ? 'border-red-200 bg-red-50 text-red-600 hover:bg-red-100'
                    : 'border-gray-200 text-gray-600 hover:bg-gray-50'
                "
              >
                <i class="pi pi-heart" :class="{ 'pi-heart-fill': board.isLiked }"></i>
                <span>좋아요 {{ board.likeCount || 0 }}</span>
              </button>

              <button
                @click="toggleBookmark"
                :disabled="isBookmarkProcessing"
                class="flex items-center gap-2 px-4 py-2 rounded-lg border transition-colors cursor-pointer disabled:opacity-50"
                :class="
                  board.isBookmarked
                    ? 'border-yellow-200 bg-yellow-50 text-yellow-600 hover:bg-yellow-100'
                    : 'border-gray-200 text-gray-600 hover:bg-gray-50'
                "
              >
                <i class="pi pi-bookmark" :class="{ 'pi-bookmark-fill': board.isBookmarked }"></i>
                <span>북마크</span>
              </button>
            </div>
          </div>

          <!-- 댓글 섹션 -->
          <div class="p-6">
            <h3 class="text-lg font-semibold text-gray-900 mb-4">댓글 {{ comments.length }}개</h3>

            <!-- 댓글 작성 -->
            <div class="mb-6">
              <textarea
                v-model="newComment"
                rows="3"
                class="w-full border border-gray-300 rounded-xl hover:border-gray-400 focus:border-[var(--secondary-color)] focus:outline-none bg-white transition-all duration-200 px-4 py-3 resize-none"
                placeholder="댓글을 입력하세요..."
              ></textarea>
              <div class="flex justify-end mt-2">
                <button
                  @click="submitComment"
                  :disabled="!newComment.trim() || isCommentSubmitting"
                  class="px-4 py-2 bg-gradient-to-r from-[var(--primary-color)] to-[var(--secondary-color)] text-white rounded-xl shadow-md hover:shadow-lg disabled:opacity-50 disabled:cursor-not-allowed active:scale-95 transition-all duration-200 ease-in-out cursor-pointer"
                >
                  <span v-if="!isCommentSubmitting">댓글 작성</span>
                  <span v-else class="flex items-center gap-2">
                    <i class="pi pi-spinner pi-spin"></i>
                    작성 중...
                  </span>
                </button>
              </div>
            </div>

            <!-- 댓글 리스트 -->
            <div v-if="comments.length === 0" class="text-center py-8 text-gray-500">
              첫 번째 댓글을 작성해보세요!
            </div>

            <div v-else class="space-y-4">
              <div
                v-for="comment in comments"
                :key="comment.commentId"
                class="border border-gray-200 rounded-lg p-4"
              >
                <div class="flex justify-between items-start">
                  <div class="flex items-start gap-3 flex-1">
                    <div
                      class="w-8 h-8 rounded-full flex items-center justify-center"
                      style="background: var(--gradient-primary)"
                    >
                      <span class="text-white text-xs font-bold">{{
                        (comment.username || '').charAt(0) || '?'
                      }}</span>
                    </div>
                    <div class="flex-1">
                      <div class="flex items-center gap-2 mb-1">
                        <span class="font-medium text-gray-900">{{
                          comment.username || '익명'
                        }}</span>
                        <span class="text-sm text-gray-500">{{
                          comment.createdAt
                            ? formatRelativeTime(comment.createdAt)
                            : '날짜 정보 없음'
                        }}</span>

                      </div>
                      <p class="text-gray-800">{{ comment.content || '내용이 없습니다.' }}</p>
                      <div class="flex items-center gap-4 mt-2">
                        <button
                          @click="toggleCommentLike(comment)"
                          class="flex items-center gap-1 text-sm text-gray-500 hover:text-red-500 transition-colors cursor-pointer"
                        >
                          <i class="pi pi-heart" :class="{ 'text-red-500': comment.isLiked }"></i>
                          {{ comment.likeCount || 0 }}
                        </button>

                        <!-- 내 댄글일 경우 삭제 버튼 -->
                        <button
                          v-if="isMyComment(comment.userId)"
                          @click="deleteCommentConfirm(comment.commentId)"
                          class="text-sm text-gray-400 hover:text-red-500 transition-colors cursor-pointer"
                        >
                          삭제
                        </button>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- 게시글 삭제 확인 모달 -->
  <div
    v-if="showDeleteModal"
    class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50"
    @click="showDeleteModal = false"
  >
    <div class="bg-white rounded-lg p-6 max-w-md w-full mx-4" @click.stop>
      <div class="text-center">
        <div
          class="w-16 h-16 rounded-full bg-red-100 flex items-center justify-center mx-auto mb-4"
        >
          <i class="pi pi-exclamation-triangle text-red-500 text-2xl"></i>
        </div>
        <h3 class="text-lg font-semibold text-gray-900 mb-2">게시글 삭제</h3>
        <p class="text-gray-600 mb-6">
          정말로 이 게시글을 삭제하시겠습니까?<br />
          삭제된 게시글은 복구할 수 없습니다.
        </p>
        <div class="flex gap-3">
          <button
            @click="showDeleteModal = false"
            class="flex-1 px-4 py-2 border border-gray-300 rounded-xl text-gray-700 hover:bg-gray-50 transition-colors cursor-pointer"
          >
            취소
          </button>
          <button
            @click="deletePost"
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

  <!-- 댄글 삭제 확인 모달 -->
  <div
    v-if="showCommentDeleteModal"
    class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50"
    @click="showCommentDeleteModal = false"
  >
    <div class="bg-white rounded-lg p-6 max-w-md w-full mx-4" @click.stop>
      <div class="text-center">
        <div
          class="w-16 h-16 rounded-full bg-red-100 flex items-center justify-center mx-auto mb-4"
        >
          <i class="pi pi-exclamation-triangle text-red-500 text-2xl"></i>
        </div>
        <h3 class="text-lg font-semibold text-gray-900 mb-2">댄글 삭제</h3>
        <p class="text-gray-600 mb-6">정말로 이 댄글을 삭제하시겠습니까?</p>
        <div class="flex gap-3">
          <button
            @click="showCommentDeleteModal = false"
            class="flex-1 px-4 py-2 border border-gray-300 rounded-xl text-gray-700 hover:bg-gray-50 transition-colors cursor-pointer"
          >
            취소
          </button>
          <button
            @click="deleteCommentExecute"
            :disabled="isCommentDeleting"
            class="flex-1 px-4 py-2 bg-red-500 text-white rounded-xl hover:bg-red-600 disabled:opacity-50 disabled:cursor-not-allowed transition-colors cursor-pointer"
          >
            <span v-if="!isCommentDeleting">삭제하기</span>
            <span v-else class="flex items-center justify-center gap-2">
              <i class="pi pi-spinner pi-spin"></i>
              삭제 중...
            </span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { storeToRefs } from 'pinia'
import {
  getBoardDetail,
  getBoardComments,
  addBoardLike,
  removeBoardLike,
  addBoardBookmark,
  removeBoardBookmark,
  deleteBoard,
} from '@/api/board'
import { createComment, addCommentLike, removeCommentLike, deleteComment } from '@/api/comment'
import { toast } from 'vue-sonner'
import { formatRelativeTime } from '@/utils/formatters'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const { userId } = storeToRefs(authStore)

// 상태 관리
const board = ref(null)
const comments = ref([])
const isLoading = ref(false)
const error = ref('')

// 액션 상태
const isLikeProcessing = ref(false)
const isBookmarkProcessing = ref(false)

// 댓글 관련
const newComment = ref('')
const isCommentSubmitting = ref(false)

// 모달 및 삭제 상태
const showDeleteModal = ref(false)
const showCommentDeleteModal = ref(false)
const isDeleting = ref(false)
const isCommentDeleting = ref(false)
const deleteTargetCommentId = ref(null)

// 내 게시글/댓글 확인
const isMyPost = computed(() => {
  return userId.value && board.value && userId.value === board.value.userId
})

const isMyComment = (commentUserId) => {
  return userId.value && userId.value === commentUserId
}

// 게시글 데이터 로드
const loadBoardDetail = async () => {
  const boardId = route.params.boardId
  if (!boardId) {
    error.value = '잘못된 게시글 ID입니다'
    return
  }

  isLoading.value = true
  error.value = ''

  try {
    const boardData = await getBoardDetail(boardId)
    const commentsData = await getBoardComments(boardId)

    board.value = boardData
    comments.value = commentsData
  } catch (err) {
    console.error('게시글 로드 실패:', err)
    if (err.response?.status === 404) {
      error.value = '게시글을 찾을 수 없습니다'
    } else {
      error.value = '게시글을 불러오는데 실패했습니다'
    }
  } finally {
    isLoading.value = false
  }
}

// 좋아요 토글
const toggleLike = async () => {
  if (!board.value || isLikeProcessing.value) return

  isLikeProcessing.value = true

  try {
    if (board.value.isLiked) {
      await removeBoardLike(board.value.boardId)
      board.value.likeCount--
    } else {
      await addBoardLike(board.value.boardId)
      board.value.likeCount++
    }
    board.value.isLiked = !board.value.isLiked
  } catch (error) {
    console.error('좋아요 처리 실패:', error)
    toast.error('좋아요 처리에 실패했습니다')
  } finally {
    isLikeProcessing.value = false
  }
}

// 북마크 토글
const toggleBookmark = async () => {
  if (!board.value || isBookmarkProcessing.value) return

  isBookmarkProcessing.value = true

  try {
    if (board.value.isBookmarked) {
      await removeBoardBookmark(board.value.boardId)
      toast.success('북마크가 해제되었습니다')
    } else {
      await addBoardBookmark(board.value.boardId)
      toast.success('북마크에 추가되었습니다')
    }
    board.value.isBookmarked = !board.value.isBookmarked
  } catch (error) {
    console.error('북마크 처리 실패:', error)
    toast.error('북마크 처리에 실패했습니다')
  } finally {
    isBookmarkProcessing.value = false
  }
}

// 댓글 작성
const submitComment = async () => {
  if (!newComment.value.trim() || isCommentSubmitting.value) return

  isCommentSubmitting.value = true

  try {
    const commentData = {
      boardId: board.value.boardId,
      content: newComment.value.trim(),
    }

    const createdComment = await createComment(commentData)

    // 댓글 리스트에 새 댓글 추가 (간단하게 전체 데이터 재로드)
    const updatedComments = await getBoardComments(board.value.boardId)
    comments.value = updatedComments

    newComment.value = ''
    toast.success('댓글이 작성되었습니다')
  } catch (error) {
    console.error('댓글 작성 실패:', error)
    toast.error('댓글 작성에 실패했습니다')
  } finally {
    isCommentSubmitting.value = false
  }
}

// 댓글 좋아요 토글
const toggleCommentLike = async (comment) => {
  try {
    if (comment.isLiked) {
      await removeCommentLike(comment.commentId)
      comment.likeCount--
    } else {
      await addCommentLike(comment.commentId)
      comment.likeCount++
    }
    comment.isLiked = !comment.isLiked
  } catch (error) {
    console.error('댓글 좋아요 처리 실패:', error)
    toast.error('댓글 좋아요 처리에 실패했습니다')
  }
}

// 게시글 수정
const editPost = () => {
  router.push({ name: 'board', query: { edit: board.value.boardId } })
}

// 게시글 삭제
const deletePost = async () => {
  if (!board.value || isDeleting.value) return

  isDeleting.value = true

  try {
    await deleteBoard(board.value.boardId)
    toast.success('게시글이 삭제되었습니다')

    // 게시판 목록으로 이동
    router.push({ name: 'board' })
  } catch (error) {
    console.error('게시글 삭제 실패:', error)
    toast.error('게시글 삭제에 실패했습니다')
  } finally {
    isDeleting.value = false
    showDeleteModal.value = false
  }
}

// 댓글 삭제 확인
const deleteCommentConfirm = (commentId) => {
  deleteTargetCommentId.value = commentId
  showCommentDeleteModal.value = true
}

// 댓글 삭제 실행
const deleteCommentExecute = async () => {
  if (!deleteTargetCommentId.value || isCommentDeleting.value) return

  isCommentDeleting.value = true

  try {
    await deleteComment(deleteTargetCommentId.value)
    toast.success('댓글이 삭제되었습니다')

    // 댓글 목록 새로고침
    const updatedComments = await getBoardComments(board.value.boardId)
    comments.value = updatedComments
  } catch (error) {
    console.error('댓글 삭제 실패:', error)
    toast.error('댓글 삭제에 실패했습니다')
  } finally {
    isCommentDeleting.value = false
    showCommentDeleteModal.value = false
    deleteTargetCommentId.value = null
  }
}

onMounted(() => {
  loadBoardDetail()
})
</script>
