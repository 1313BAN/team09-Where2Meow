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
          <div class="w-16 h-16 rounded-full bg-red-100 flex items-center justify-center mx-auto mb-4">
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
                <span class="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-[var(--primary-10)] text-[var(--primary-color)]">
                  {{ board.categoryName || '일반' }}
                </span>
                <span v-if="board.updatedAt !== board.createdAt" class="text-sm text-gray-500">(수정됨)</span>
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
                  <div class="w-10 h-10 rounded-full flex items-center justify-center" style="background: var(--gradient-primary)">
                    <span class="text-white text-sm font-bold">{{ (board.authorName || '').charAt(0) || '?' }}</span>
                  </div>
                  <div>
                    <p class="font-medium text-gray-900">{{ board.authorName || '익명' }}</p>
                    <p class="text-sm text-gray-500">{{ board.createdAt ? formatDate(board.createdAt) : '날짜 정보 없음' }}</p>
                  </div>
                </div>
              </div>

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
            </div>
          </div>

          <!-- 게시글 본문 -->
          <div class="p-6 border-b border-gray-200">
            <div class="prose max-w-none">
              <p class="text-gray-800 whitespace-pre-wrap">{{ board.content || '내용이 없습니다.' }}</p>
            </div>
          </div>

          <!-- 액션 버튼 -->
          <div class="p-6 border-b border-gray-200">
            <div class="flex items-center gap-3">
              <button
                @click="toggleLike"
                :disabled="isLikeProcessing"
                class="flex items-center gap-2 px-4 py-2 rounded-lg border transition-colors cursor-pointer disabled:opacity-50"
                :class="board.isLiked 
                  ? 'border-red-200 bg-red-50 text-red-600 hover:bg-red-100' 
                  : 'border-gray-200 text-gray-600 hover:bg-gray-50'"
              >
                <i class="pi pi-heart" :class="{ 'pi-heart-fill': board.isLiked }"></i>
                <span>좋아요 {{ board.likeCount || 0 }}</span>
              </button>

              <button
                @click="toggleBookmark"
                :disabled="isBookmarkProcessing"
                class="flex items-center gap-2 px-4 py-2 rounded-lg border transition-colors cursor-pointer disabled:opacity-50"
                :class="board.isBookmarked 
                  ? 'border-yellow-200 bg-yellow-50 text-yellow-600 hover:bg-yellow-100' 
                  : 'border-gray-200 text-gray-600 hover:bg-gray-50'"
              >
                <i class="pi pi-bookmark" :class="{ 'pi-bookmark-fill': board.isBookmarked }"></i>
                <span>북마크</span>
              </button>
            </div>
          </div>

          <!-- 댓글 섹션 -->
          <div class="p-6">
            <h3 class="text-lg font-semibold text-gray-900 mb-4">
              댓글 {{ comments.length }}개
            </h3>

            <!-- 댓글 작성 -->
            <div class="mb-6">
              <textarea
                v-model="newComment"
                rows="3"
                class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[var(--primary-color)] focus:border-transparent resize-none"
                placeholder="댓글을 입력하세요..."
              ></textarea>
              <div class="flex justify-end mt-2">
                <button
                  @click="submitComment"
                  :disabled="!newComment.trim() || isCommentSubmitting"
                  class="px-4 py-2 text-white rounded-lg disabled:opacity-50 disabled:cursor-not-allowed transition-colors cursor-pointer"
                  style="background: var(--gradient-primary)"
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
                    <div class="w-8 h-8 rounded-full flex items-center justify-center" style="background: var(--gradient-primary)">
                      <span class="text-white text-xs font-bold">{{ (comment.authorName || '').charAt(0) || '?' }}</span>
                    </div>
                    <div class="flex-1">
                      <div class="flex items-center gap-2 mb-1">
                        <span class="font-medium text-gray-900">{{ comment.authorName || '익명' }}</span>
                        <span class="text-sm text-gray-500">{{ comment.createdAt ? formatDate(comment.createdAt) : '날짜 정보 없음' }}</span>
                        <span v-if="comment.updatedAt !== comment.createdAt" class="text-xs text-gray-400">(수정됨)</span>
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
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getBoardDetail, getBoardComments, addBoardLike, removeBoardLike, addBoardBookmark, removeBoardBookmark } from '@/api/board'
import { createComment, addCommentLike, removeCommentLike } from '@/api/comment'
import { toast } from 'vue-sonner'

const route = useRoute()

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
      content: newComment.value.trim()
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

// 날짜 포맷팅
const formatDate = (dateString) => {
  if (!dateString) return '날짜 정보 없음'
  
  try {
    const date = new Date(dateString)
    if (isNaN(date.getTime())) return '잘못된 날짜'
    
    const now = new Date()
    const diffMs = now - date
    const diffMins = Math.floor(diffMs / 60000)
    const diffHours = Math.floor(diffMs / 3600000)
    const diffDays = Math.floor(diffMs / 86400000)

    if (diffMins < 1) return '방금 전'
    if (diffMins < 60) return `${diffMins}분 전`
    if (diffHours < 24) return `${diffHours}시간 전`
    if (diffDays < 7) return `${diffDays}일 전`
    return date.toLocaleDateString()
  } catch {
    return '날짜 오류'
  }
}

onMounted(() => {
  loadBoardDetail()
})
</script>
