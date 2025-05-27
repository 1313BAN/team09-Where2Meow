<template>
  <div class="p-6">
    <div class="mb-6">
      <h2 class="text-xl font-semibold text-gray-900">내가 쓴 댓글</h2>
      <p class="text-gray-600 mt-1">작성한 댓글을 관리하세요</p>
    </div>

    <!-- 로딩 상태 -->
    <div v-if="isLoading" class="flex justify-center py-12">
      <div class="flex items-center gap-3 text-gray-600">
        <i class="pi pi-spinner pi-spin text-xl"></i>
        <span>댓글을 불러오는 중...</span>
      </div>
    </div>

    <!-- 데이터 없음 -->
    <div v-else-if="!comments || comments.length === 0" class="text-center py-12">
      <div class="w-16 h-16 rounded-full bg-gray-100 flex items-center justify-center mx-auto mb-4">
        <i class="pi pi-comment text-gray-400 text-2xl"></i>
      </div>
      <h3 class="text-lg font-medium text-gray-900 mb-2">작성한 댓글이 없습니다</h3>
      <p class="text-gray-600">게시글에 댓글을 달아보세요!</p>
    </div>

    <!-- 댓글 리스트 -->
    <div v-else-if="comments && comments.length > 0" class="space-y-4">
      <div
        v-for="comment in comments"
        :key="comment.commentId"
        class="border border-gray-200 rounded-lg p-4 hover:shadow-md transition-shadow"
      >
        <!-- 게시글 정보 -->
        <div class="mb-3 pb-3 border-b border-gray-100">
          <div class="flex items-center justify-between">
            <div>
              <button
                @click="goToBoardDetail(comment.boardId)"
                class="text-[var(--primary-color)] hover:text-[var(--primary-dark)] font-medium cursor-pointer"
              >
                {{ comment.boardTitle }}
              </button>
              <span class="text-gray-400 mx-2">•</span>
              <span class="text-sm text-gray-500">{{ comment.boardAuthor }}</span>
            </div>
            <span class="text-sm text-gray-500">
              {{ formatRelativeTime(comment.createdAt) }}
            </span>
          </div>
        </div>

        <!-- 댓글 내용 -->
        <div class="flex justify-between items-start">
          <div class="flex-1">
            <p class="text-gray-800 mb-3">{{ comment.content }}</p>

            <!-- 댓글 메타 정보 -->
            <div class="flex items-center gap-4 text-sm text-gray-500">
              <span class="flex items-center gap-1">
                <i class="pi pi-heart"></i>
                {{ comment.likeCount }}
              </span>

            </div>
          </div>

          <!-- 액션 버튼 -->
          <div class="flex items-center gap-2 ml-4">
            <button
              @click="editComment(comment)"
              class="p-2 rounded-lg text-gray-400 hover:text-[var(--primary-color)] hover:bg-gray-50 transition-colors cursor-pointer"
              title="수정"
            >
              <i class="pi pi-pencil"></i>
            </button>
            <button
              @click="deleteComment(comment)"
              class="p-2 rounded-lg text-gray-400 hover:text-red-500 hover:bg-red-50 transition-colors cursor-pointer"
              title="삭제"
            >
              <i class="pi pi-trash"></i>
            </button>
          </div>
        </div>
      </div>

      <!-- 더 불러오기 버튼 -->
      <div v-if="hasMore" class="text-center pt-4">
        <button
          @click="loadMore"
          :disabled="isLoadingMore"
          class="px-6 py-2 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed transition-colors cursor-pointer"
        >
          <span v-if="!isLoadingMore">더 보기</span>
          <span v-else class="flex items-center gap-2">
            <i class="pi pi-spinner pi-spin"></i>
            불러오는 중...
          </span>
        </button>
      </div>
    </div>

    <!-- 댓글 수정 모달 -->
    <div
      v-if="showEditModal"
      class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50"
      @click="showEditModal = false"
    >
      <div class="bg-white rounded-lg p-6 max-w-md w-full mx-4" @click.stop>
        <h3 class="text-lg font-semibold text-gray-900 mb-4">댓글 수정</h3>
        <textarea
          v-model="editingComment.content"
          rows="4"
          class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[var(--primary-color)] focus:border-transparent resize-none"
          placeholder="댓글을 입력하세요"
        ></textarea>
        <div class="flex gap-3 mt-4">
          <button
            @click="showEditModal = false"
            class="flex-1 px-4 py-2 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50 transition-colors cursor-pointer"
          >
            취소
          </button>
          <button
            @click="confirmEdit"
            :disabled="isUpdating || !editingComment.content.trim()"
            class="flex-1 px-4 py-2 text-white rounded-lg disabled:opacity-50 disabled:cursor-not-allowed transition-colors cursor-pointer"
            style="background: var(--gradient-primary)"
          >
            <span v-if="!isUpdating">수정</span>
            <span v-else class="flex items-center justify-center gap-2">
              <i class="pi pi-spinner pi-spin"></i>
              수정 중...
            </span>
          </button>
        </div>
      </div>
    </div>

    <!-- 삭제 확인 모달 -->
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
          <h3 class="text-lg font-semibold text-gray-900 mb-2">댓글 삭제</h3>
          <p class="text-gray-600 mb-6">
            이 댓글을 삭제하시겠습니까?<br />
            삭제된 댓글은 복구할 수 없습니다.
          </p>
          <div class="flex gap-3">
            <button
              @click="showDeleteModal = false"
              class="flex-1 px-4 py-2 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50 transition-colors cursor-pointer"
            >
              취소
            </button>
            <button
              @click="confirmDelete"
              :disabled="isDeleting"
              class="flex-1 px-4 py-2 bg-red-500 text-white rounded-lg hover:bg-red-600 disabled:opacity-50 disabled:cursor-not-allowed transition-colors cursor-pointer"
            >
              <span v-if="!isDeleting">삭제</span>
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
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getUserComments, updateComment, deleteComment as deleteCommentApi } from '@/api/comment'
import { toast } from 'vue-sonner'
import { formatRelativeTime } from '@/utils/formatters'

const router = useRouter()

// 상태 관리
const comments = ref([])
const isLoading = ref(false)
const isLoadingMore = ref(false)
const hasMore = ref(true)
const currentPage = ref(0)
const pageSize = 10

// 수정 관련
const showEditModal = ref(false)
const editingComment = reactive({
  commentId: null,
  content: '',
})
const isUpdating = ref(false)

// 삭제 관련
const showDeleteModal = ref(false)
const selectedComment = ref(null)
const isDeleting = ref(false)

// 데이터 로드
const loadComments = async (page = 0, append = false) => {
  try {
    if (page === 0) {
      isLoading.value = true
    } else {
      isLoadingMore.value = true
    }

    const response = await getUserComments({
      page,
      size: pageSize,
    })

    if (append) {
      comments.value = [...comments.value, ...response]
    } else {
      comments.value = response
    }

    // 페이지네이션 처리 (백엔드 응답 구조에 따라 조정 필요)
    hasMore.value = response.length === pageSize
    currentPage.value = page
  } catch (error) {
    console.error('댓글 로드 실패:', error)
    toast.error('댓글을 불러오는데 실패했습니다')
  } finally {
    isLoading.value = false
    isLoadingMore.value = false
  }
}

// 더 불러오기
const loadMore = () => {
  if (hasMore.value && !isLoadingMore.value) {
    loadComments(currentPage.value + 1, true)
  }
}

// 게시글 상세 페이지로 이동
const goToBoardDetail = (boardId) => {
  router.push(`/board/${boardId}`)
}

// 댓글 수정
const editComment = (comment) => {
  editingComment.commentId = comment.commentId
  editingComment.content = comment.content
  showEditModal.value = true
}

// 수정 확인
const confirmEdit = async () => {
  if (!editingComment.content.trim()) return

  isUpdating.value = true

  try {
    await updateComment(editingComment.commentId, {
      content: editingComment.content.trim(),
    })

    // 리스트에서 업데이트
    const index = comments.value.findIndex((c) => c.commentId === editingComment.commentId)
    if (index !== -1) {
      comments.value[index].content = editingComment.content.trim()
      comments.value[index].updatedAt = new Date().toISOString()
    }

    toast.success('댓글이 수정되었습니다')
    showEditModal.value = false
  } catch (error) {
    console.error('댓글 수정 실패:', error)
    toast.error('댓글 수정에 실패했습니다')
  } finally {
    isUpdating.value = false
  }
}

// 댓글 삭제
const deleteComment = (comment) => {
  selectedComment.value = comment
  showDeleteModal.value = true
}

// 삭제 확인
const confirmDelete = async () => {
  if (!selectedComment.value) return

  isDeleting.value = true

  try {
    await deleteCommentApi(selectedComment.value.commentId)

    // 리스트에서 제거
    comments.value = comments.value.filter((c) => c.commentId !== selectedComment.value.commentId)

    toast.success('댓글이 삭제되었습니다')
    showDeleteModal.value = false
    selectedComment.value = null
  } catch (error) {
    console.error('댓글 삭제 실패:', error)
    toast.error('댓글 삭제에 실패했습니다')
  } finally {
    isDeleting.value = false
  }
}

onMounted(() => {
  loadComments()
})
</script>
