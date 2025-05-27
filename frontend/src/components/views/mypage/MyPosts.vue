<template>
  <div class="p-6">
    <div class="mb-6">
      <h2 class="text-xl font-semibold text-gray-900">내가 쓴 글</h2>
      <p class="text-gray-600 mt-1">작성한 게시글을 관리하세요</p>
    </div>

    <!-- 로딩 상태 -->
    <div v-if="isLoading" class="flex justify-center py-12">
      <div class="flex items-center gap-3 text-gray-600">
        <i class="pi pi-spinner pi-spin text-xl"></i>
        <span>게시글을 불러오는 중...</span>
      </div>
    </div>

    <!-- 데이터 없음 -->
    <div v-else-if="!posts || posts.length === 0" class="text-center py-12">
      <div class="w-16 h-16 rounded-full bg-gray-100 flex items-center justify-center mx-auto mb-4">
        <i class="pi pi-file-edit text-gray-400 text-2xl"></i>
      </div>
      <h3 class="text-lg font-medium text-gray-900 mb-2">작성한 게시글이 없습니다</h3>
      <p class="text-gray-600">첫 번째 게시글을 작성해보세요!</p>
    </div>

    <!-- 게시글 리스트 -->
    <div v-else-if="posts && posts.length > 0" class="space-y-4">
      <div
        v-for="post in posts"
        :key="post.boardId"
        class="border border-gray-200 rounded-lg p-4 hover:shadow-md transition-shadow cursor-pointer"
        @click="goToBoardDetail(post.boardId)"
      >
        <div class="flex justify-between items-start">
          <div class="flex-1">
            <!-- 카테고리 -->
            <div class="mb-2">
              <span
                class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-[var(--primary-10)] text-[var(--primary-color)]"
              >
                {{ post.categoryName || '일반' }}
              </span>
            </div>

            <!-- 제목 -->
            <h3 class="text-lg font-semibold text-gray-900 mb-2 line-clamp-2">
              {{ post.title }}
            </h3>

            <!-- 내용 미리보기 -->
            <p class="text-gray-600 text-sm mb-3 line-clamp-2">
              {{ post.content }}
            </p>

            <!-- 메타 정보 -->
            <div class="flex items-center gap-4 text-sm text-gray-500">
              <span class="flex items-center gap-1">
                <i class="pi pi-clock"></i>
                {{ formatDate(post.createdAt) }}
              </span>
              <span class="flex items-center gap-1">
                <i class="pi pi-eye"></i>
                {{ post.viewCount }}
              </span>
              <span class="flex items-center gap-1">
                <i class="pi pi-heart"></i>
                {{ post.likeCount }}
              </span>
              <span class="flex items-center gap-1">
                <i class="pi pi-comment"></i>
                {{ post.commentCount }}
              </span>
            </div>
          </div>

          <!-- 액션 버튼 -->
          <div class="flex items-center gap-2 ml-4">
            <button
              @click.stop="editPost(post)"
              class="p-2 rounded-lg text-gray-400 hover:text-[var(--primary-color)] hover:bg-gray-50 transition-colors cursor-pointer"
              title="수정"
            >
              <i class="pi pi-pencil"></i>
            </button>
            <button
              @click.stop="deletePost(post)"
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
          <h3 class="text-lg font-semibold text-gray-900 mb-2">게시글 삭제</h3>
          <p class="text-gray-600 mb-6">
            이 게시글을 삭제하시겠습니까?<br />
            삭제된 게시글은 복구할 수 없습니다.
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
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getUserBoards, deleteBoard } from '@/api/board'
import { toast } from 'vue-sonner'

const router = useRouter()

// 상태 관리
const posts = ref([])
const isLoading = ref(false)
const isLoadingMore = ref(false)
const hasMore = ref(true)
const currentPage = ref(0)
const pageSize = 10

// 삭제 관련
const showDeleteModal = ref(false)
const selectedPost = ref(null)
const isDeleting = ref(false)

// 데이터 로드
const loadPosts = async (page = 0, append = false) => {
  try {
    if (page === 0) {
      isLoading.value = true
    } else {
      isLoadingMore.value = true
    }

    const response = await getUserBoards({
      page,
      size: pageSize,
      sort: 'createdAt',
      direction: 'desc',
    })

    console.log('User posts response:', response) // 디버깅용

    // 수정된 부분: boards 배열 추출
    const newPosts = response?.boards || []

    if (!Array.isArray(newPosts)) {
      console.error('Posts response is not an array:', newPosts)
      if (!append) {
        posts.value = []
      }
      hasMore.value = false
      return
    }

    if (append) {
      posts.value = [...(posts.value || []), ...newPosts]
    } else {
      posts.value = newPosts
    }

    // 수정된 부분: API 응답의 페이징 정보 사용
    hasMore.value = response?.last === false
    currentPage.value = page
  } catch (error) {
    console.error('게시글 로드 실패:', error)
    toast.error('게시글을 불러오는 데 실패했습니다')
    if (!append) {
      posts.value = []
    }
    hasMore.value = false
  } finally {
    isLoading.value = false
    isLoadingMore.value = false
  }
}

// 더 불러오기
const loadMore = () => {
  if (hasMore.value && !isLoadingMore.value) {
    loadPosts(currentPage.value + 1, true)
  }
}

// 게시글 상세 페이지로 이동
const goToBoardDetail = (boardId) => {
  router.push(`/board/${boardId}`)
}

// 게시글 수정
const editPost = (post) => {
  router.push(`/board/${post.boardId}/edit`)
}

// 게시글 삭제
const deletePost = (post) => {
  selectedPost.value = post
  showDeleteModal.value = true
}

// 삭제 확인
const confirmDelete = async () => {
  if (!selectedPost.value) return

  isDeleting.value = true

  try {
    await deleteBoard(selectedPost.value.boardId)

    // 리스트에서 제거
    posts.value = posts.value.filter((post) => post.boardId !== selectedPost.value.boardId)

    toast.success('게시글이 삭제되었습니다')
    showDeleteModal.value = false
    selectedPost.value = null
  } catch (error) {
    console.error('게시글 삭제 실패:', error)
    toast.error('게시글 삭제에 실패했습니다')
  } finally {
    isDeleting.value = false
  }
}

// 날짜 포맷팅
const formatDate = (dateString) => {
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
  return date.toLocaleDateString()
}

onMounted(() => {
  loadPosts()
})
</script>
