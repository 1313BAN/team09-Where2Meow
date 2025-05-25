<template>
  <div class="p-6">
    <div class="mb-6">
      <h2 class="text-xl font-semibold text-gray-900">북마크한 게시글</h2>
      <p class="text-gray-600 mt-1">북마크한 게시글을 관리하세요</p>
    </div>

    <!-- 로딩 상태 -->
    <div v-if="isLoading" class="flex justify-center py-12">
      <div class="flex items-center gap-3 text-gray-600">
        <i class="pi pi-spinner pi-spin text-xl"></i>
        <span>북마크한 게시글을 불러오는 중...</span>
      </div>
    </div>

    <!-- 데이터 없음 -->
    <div v-else-if="!bookmarkedPosts || bookmarkedPosts.length === 0" class="text-center py-12">
      <div class="w-16 h-16 rounded-full bg-gray-100 flex items-center justify-center mx-auto mb-4">
        <i class="pi pi-bookmark text-gray-400 text-2xl"></i>
      </div>
      <h3 class="text-lg font-medium text-gray-900 mb-2">북마크한 게시글이 없습니다</h3>
      <p class="text-gray-600">관심있는 게시글을 북마크해보세요!</p>
    </div>

    <!-- 게시글 리스트 -->
    <div v-else-if="bookmarkedPosts && bookmarkedPosts.length > 0" class="space-y-4">
      <div
        v-for="post in bookmarkedPosts"
        :key="post.boardId"
        class="border border-gray-200 rounded-lg p-4 hover:shadow-md transition-shadow cursor-pointer"
        @click="goToBoardDetail(post.boardId)"
      >
        <div class="flex justify-between items-start">
          <div class="flex-1">
            <!-- 카테고리와 작성자 -->
            <div class="mb-2 flex items-center gap-2">
              <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-[var(--primary-10)] text-[var(--primary-color)]">
                {{ post.categoryName || '일반' }}
              </span>
              <span class="text-sm text-gray-500">
                by {{ post.authorName }}
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

          <!-- 북마크 해제 버튼 -->
          <div class="flex items-center gap-2 ml-4">
            <button
              @click.stop="removeBookmark(post)"
              :disabled="post.isRemoving"
              class="p-2 rounded-lg text-yellow-500 hover:text-red-500 hover:bg-red-50 transition-colors cursor-pointer disabled:opacity-50"
              title="북마크 해제"
            >
              <i v-if="!post.isRemoving" class="pi pi-bookmark-fill"></i>
              <i v-else class="pi pi-spinner pi-spin"></i>
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getBookmarkedBoards, removeBoardBookmark } from '@/api/board'
import { toast } from 'vue-sonner'

const router = useRouter()

// 상태 관리
const bookmarkedPosts = ref([])
const isLoading = ref(false)
const isLoadingMore = ref(false)
const hasMore = ref(true)
const currentPage = ref(0)
const pageSize = 10

// 데이터 로드
const loadBookmarkedPosts = async (page = 0, append = false) => {
  try {
    if (page === 0) {
      isLoading.value = true
    } else {
      isLoadingMore.value = true
    }

    const response = await getBookmarkedBoards({
      page,
      size: pageSize,
      sort: 'createdAt',
      direction: 'desc'
    })

    const postsWithState = response.content.map(post => ({
      ...post,
      isRemoving: false
    }))

    if (append) {
      bookmarkedPosts.value = [...bookmarkedPosts.value, ...postsWithState]
    } else {
      bookmarkedPosts.value = postsWithState
    }

    hasMore.value = !response.last
    currentPage.value = page

  } catch (error) {
    console.error('북마크한 게시글 로드 실패:', error)
    toast.error('북마크한 게시글을 불러오는데 실패했습니다')
  } finally {
    isLoading.value = false
    isLoadingMore.value = false
  }
}

// 더 불러오기
const loadMore = () => {
  if (hasMore.value && !isLoadingMore.value) {
    loadBookmarkedPosts(currentPage.value + 1, true)
  }
}

// 게시글 상세 페이지로 이동
const goToBoardDetail = (boardId) => {
  router.push(`/board/${boardId}`)
}

// 북마크 해제
const removeBookmark = async (post) => {
  const index = bookmarkedPosts.value.findIndex(p => p.boardId === post.boardId)
  if (index === -1) return

  // 로딩 상태 표시
  bookmarkedPosts.value[index].isRemoving = true

  try {
    await removeBoardBookmark(post.boardId)
    
    // 리스트에서 제거
    bookmarkedPosts.value = bookmarkedPosts.value.filter(p => p.boardId !== post.boardId)
    
    toast.success('북마크가 해제되었습니다')
    
  } catch (error) {
    console.error('북마크 해제 실패:', error)
    toast.error('북마크 해제에 실패했습니다')
    
    // 실패 시 로딩 상태 해제
    if (bookmarkedPosts.value[index]) {
      bookmarkedPosts.value[index].isRemoving = false
    }
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
  loadBookmarkedPosts()
})
</script>
