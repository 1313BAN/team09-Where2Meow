<template>
  <div class="p-6">
    <!-- 헤더 섹션 -->
    <div class="flex items-center justify-between mb-6">
      <div class="flex items-center gap-4">
        <h2 class="text-xl font-semibold text-gray-900">
          {{ currentCategoryName }}
        </h2>
        <span v-if="totalElements > 0" class="text-sm text-gray-500">
          총 {{ totalElements }}개의 게시글
        </span>
      </div>

      <!-- 정렬 및 검색 -->
      <div class="flex items-center gap-3">
        <!-- 정렬 선택 -->
        <div class="relative">
          <select
            v-model="sortOption"
            @change="handleSortChange"
            class="relative cursor-pointer min-h-[40px] border border-gray-300 rounded-xl hover:border-gray-400 focus:border-[var(--secondary-color)] focus:outline-none bg-white transition-all duration-200 px-3 py-2 pr-8 text-sm appearance-none"
          >
            <option value="createdAt-desc">최신순</option>
            <option value="createdAt-asc">오래된순</option>
            <option value="likeCount-desc">좋아요순</option>
            <option value="viewCount-desc">조회수순</option>
          </select>
          <i
            class="pi pi-chevron-down absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-500 pointer-events-none"
          ></i>
        </div>

        <!-- 검색 -->
        <div class="relative">
          <input
            v-model="searchKeyword"
            @keypress.enter="handleSearch"
            type="text"
            placeholder="게시글 검색..."
            class="w-64 min-h-[40px] border border-gray-300 rounded-xl hover:border-gray-400 focus:border-[var(--secondary-color)] focus:outline-none bg-white transition-all duration-200 pl-10 pr-4 py-2 text-sm"
          />
          <i
            class="pi pi-search absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400"
          ></i>
          <button
            v-if="searchKeyword"
            @click="clearSearch"
            class="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-gray-600"
          >
            <i class="pi pi-times"></i>
          </button>
        </div>

        <button
          @click="handleSearch"
          class="min-h-[40px] px-4 py-2 bg-gradient-to-r from-[var(--primary-color)] to-[var(--secondary-color)] text-white rounded-xl shadow-md hover:shadow-lg active:scale-95 transition-all duration-200 ease-in-out cursor-pointer"
        >
          검색
        </button>
      </div>
    </div>

    <!-- 로딩 상태 -->
    <div v-if="isLoading" class="flex justify-center py-12">
      <div class="flex items-center gap-3 text-gray-600">
        <i class="pi pi-spinner pi-spin text-xl"></i>
        <span>게시글을 불러오는 중...</span>
      </div>
    </div>

    <!-- 게시글 리스트 -->
    <div v-else>
      <!-- 공지사항 (상단 고정) -->
      <div v-if="pinnedNotices.length > 0" class="mb-4">
        <div class="space-y-2">
          <div
            v-for="notice in pinnedNotices"
            :key="`notice-${notice.boardId}`"
            @click="goToBoardDetail(notice.boardId)"
            class="p-4 bg-yellow-50 border border-yellow-200 rounded-lg cursor-pointer hover:bg-yellow-100 transition-colors"
          >
            <div class="flex items-center justify-between">
              <div class="flex-1">
                <div class="flex items-center gap-2 mb-2">
                  <span
                    class="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-red-100 text-red-800"
                  >
                    📌 공지사항
                  </span>
                  <span v-if="notice.isLiked" class="text-red-500">
                    <i class="pi pi-heart-fill"></i>
                  </span>
                  <span v-if="notice.isBookmarked" class="text-yellow-500">
                    <i class="pi pi-bookmark-fill"></i>
                  </span>
                </div>
                <h3 class="text-lg font-semibold text-gray-900 mb-1">{{ notice.title }}</h3>
                <p class="text-gray-600 text-sm line-clamp-2">{{ notice.content }}</p>
                <div class="flex items-center justify-between mt-3">
                  <span class="text-sm text-gray-500">
                    {{ formatDate(notice.createdAt) }}
                  </span>
                  <div class="flex items-center gap-4 text-sm text-gray-500">
                    <span class="flex items-center gap-1">
                      <i class="pi pi-eye"></i>
                      {{ notice.viewCount }}
                    </span>
                    <span class="flex items-center gap-1">
                      <i class="pi pi-heart"></i>
                      {{ notice.likeCount }}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="border-t border-gray-200 my-6"></div>
      </div>

      <!-- 일반 게시글 -->
      <div v-if="boards.length === 0 && !isLoading" class="text-center py-12">
        <div
          class="w-16 h-16 rounded-full bg-gray-100 flex items-center justify-center mx-auto mb-4"
        >
          <i class="pi pi-file text-gray-400 text-2xl"></i>
        </div>
        <h3 class="text-lg font-medium text-gray-900 mb-2">게시글이 없습니다</h3>
        <p class="text-gray-600 mb-4">
          {{ searchKeyword ? '검색 결과가 없습니다.' : '첫 번째 게시글을 작성해보세요!' }}
        </p>
        <button
          @click="handleWritePost"
          class="px-4 py-2 bg-gradient-to-r from-[var(--primary-color)] to-[var(--secondary-color)] text-white rounded-xl shadow-md hover:shadow-lg active:scale-95 transition-all duration-200 ease-in-out cursor-pointer"
        >
          글쓰기
        </button>
      </div>

      <div v-else class="space-y-3">
        <div
          v-for="board in boards"
          :key="board.boardId"
          @click="goToBoardDetail(board.boardId)"
          class="p-4 border border-gray-200 rounded-lg cursor-pointer hover:bg-gray-50 transition-colors"
        >
          <div class="flex items-start justify-between">
            <div class="flex-1">
              <div class="flex items-center gap-2 mb-2">
                <span
                  class="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-[var(--primary-10)] text-[var(--primary-color)]"
                >
                  {{ getCategoryName(board.categoryId) }}
                </span>
                <span v-if="board.isLiked" class="text-red-500">
                  <i class="pi pi-heart-fill"></i>
                </span>
                <span v-if="board.isBookmarked" class="text-yellow-500">
                  <i class="pi pi-bookmark-fill"></i>
                </span>
                <span v-if="isMyPost(board.userId)" class="text-blue-500 text-xs font-medium">
                  내 글
                </span>
                <span v-if="board.updatedAt !== board.createdAt" class="text-xs text-gray-400">
                  (수정됨)
                </span>
              </div>

              <h3 class="text-lg font-semibold text-gray-900 mb-1">{{ board.title }}</h3>
              <p class="text-gray-600 text-sm line-clamp-2 mb-3">{{ board.content }}</p>

              <div class="flex items-center justify-between">
                <span class="text-sm text-gray-500">
                  {{ formatDate(board.createdAt) }}
                </span>
                <div class="flex items-center gap-4 text-sm text-gray-500">
                  <span class="flex items-center gap-1">
                    <i class="pi pi-eye"></i>
                    {{ board.viewCount }}
                  </span>
                  <span class="flex items-center gap-1">
                    <i class="pi pi-heart"></i>
                    {{ board.likeCount }}
                  </span>
                </div>
              </div>
            </div>

            <!-- 내 글일 경우 수정/삭제 버튼 -->
            <div v-if="isMyPost(board.userId)" class="flex items-center gap-2 ml-4">
              <button
                @click.stop="editPost(board)"
                class="p-2 text-gray-400 hover:text-blue-500 transition-colors"
                title="수정"
              >
                <i class="pi pi-pencil"></i>
              </button>
              <button
                @click.stop="deletePost(board.boardId)"
                class="p-2 text-gray-400 hover:text-red-500 transition-colors"
                title="삭제"
              >
                <i class="pi pi-trash"></i>
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- 페이지네이션 -->
      <div v-if="totalPages > 1" class="flex justify-center mt-8">
        <div class="flex items-center gap-2">
          <button
            @click="changePage(currentPage - 1)"
            :disabled="currentPage === 0"
            class="p-2 rounded-lg border border-gray-300 disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50 transition-colors"
          >
            <i class="pi pi-angle-left"></i>
          </button>

          <button
            v-for="page in visiblePages"
            :key="page"
            @click="changePage(page)"
            :class="[
              'px-3 py-2 rounded-lg border transition-colors',
              currentPage === page
                ? 'border-[var(--primary-color)] bg-[var(--primary-color)] text-white'
                : 'border-gray-300 hover:bg-gray-50',
            ]"
          >
            {{ page + 1 }}
          </button>

          <button
            @click="changePage(currentPage + 1)"
            :disabled="currentPage === totalPages - 1"
            class="p-2 rounded-lg border border-gray-300 disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50 transition-colors"
          >
            <i class="pi pi-angle-right"></i>
          </button>
        </div>
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
</template>

<script setup>
import { ref, computed, onMounted, watch, inject } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { storeToRefs } from 'pinia'
import { getBoards, searchBoards, deleteBoard } from '@/api/board'
import { toast } from 'vue-sonner'

const router = useRouter()
const authStore = useAuthStore()
const { userId } = storeToRefs(authStore)

const props = defineProps({
  categoryId: {
    type: Number,
    default: 0,
  },
})

// 게시판 네비게이션 inject
const boardNavigation = inject('boardNavigation')

// 상태 관리
const boards = ref([])
const pinnedNotices = ref([])
const isLoading = ref(false)
const searchKeyword = ref('')
const sortOption = ref('createdAt-desc')

// 페이지네이션
const currentPage = ref(0)
const totalPages = ref(0)
const totalElements = ref(0)
const pageSize = 10

// 모달 상태
const showDeleteModal = ref(false)
const deleteTargetId = ref(null)
const isDeleting = ref(false)

// 카테고리 이름 매핑
const categoryNames = {
  0: '전체 게시글',
  1: '공지사항',
  2: '자유게시판',
  3: '여행후기',
  4: '질문게시판',
}

// 현재 카테고리 이름
const currentCategoryName = computed(() => {
  return categoryNames[props.categoryId] || '게시판'
})

// 카테고리 이름 반환
const getCategoryName = (categoryId) => {
  return categoryNames[categoryId] || '기타'
}

// 내 게시글 확인
const isMyPost = (postUserId) => {
  return userId.value && userId.value === postUserId
}

// 페이지네이션 표시할 페이지 번호들
const visiblePages = computed(() => {
  const pages = []
  const start = Math.max(0, currentPage.value - 2)
  const end = Math.min(totalPages.value - 1, currentPage.value + 2)

  for (let i = start; i <= end; i++) {
    pages.push(i)
  }
  return pages
})

// 게시글 목록 로드
const loadBoards = async (page = 0) => {
  isLoading.value = true

  try {
    const [sort, direction] = sortOption.value.split('-')

    const params = {
      page,
      size: pageSize,
      sort,
      direction,
    }

    // 카테고리 필터 (0이면 전체이므로 categoryId 추가하지 않음)
    if (props.categoryId !== 0) {
      params.categoryId = props.categoryId
    }

    let response
    if (searchKeyword.value.trim()) {
      // 검색 모드
      response = await searchBoards(searchKeyword.value.trim(), params)
    } else {
      // 일반 목록
      response = await getBoards(params)
    }

    // 응답 구조에 따라 데이터 추출
    if (response.boards) {
      boards.value = response.boards
      totalPages.value = response.totalPages || 0
      totalElements.value = response.totalElements || 0
      currentPage.value = response.currentPage || 0
    } else if (Array.isArray(response)) {
      boards.value = response
      totalPages.value = 1
      totalElements.value = response.length
      currentPage.value = 0
    }

    // 공지사항 상단 고정 (전체 게시글이거나 공지사항 카테고리일 때)
    if (props.categoryId === 0 || props.categoryId === 1) {
      await loadPinnedNotices()
    } else {
      pinnedNotices.value = []
    }
  } catch (error) {
    console.error('게시글 로드 실패:', error)
    toast.error('게시글을 불러오는데 실패했습니다')
    boards.value = []
  } finally {
    isLoading.value = false
  }
}

// 상단 고정 공지사항 로드 (최신 1개만)
const loadPinnedNotices = async () => {
  try {
    const response = await getBoards({
      categoryId: 1, // 공지사항 카테고리
      sort: 'createdAt',
      direction: 'desc',
      page: 0,
      size: 1, // 최신 1개만 상단 고정
    })

    if (response.boards) {
      pinnedNotices.value = response.boards
    }
  } catch (error) {
    console.error('공지사항 로드 실패:', error)
    pinnedNotices.value = []
  }
}

// 정렬 변경 핸들러
const handleSortChange = () => {
  currentPage.value = 0
  loadBoards(0)
}

// 검색 핸들러
const handleSearch = () => {
  currentPage.value = 0
  loadBoards(0)
}

// 검색 초기화
const clearSearch = () => {
  searchKeyword.value = ''
  currentPage.value = 0
  loadBoards(0)
}

// 페이지 변경
const changePage = (page) => {
  if (page >= 0 && page < totalPages.value) {
    currentPage.value = page
    loadBoards(page)
  }
}

// 게시글 상세로 이동
const goToBoardDetail = (boardId) => {
  router.push({ name: 'boardDetail', params: { boardId } })
}

// 글쓰기 모드로 변경
const handleWritePost = () => {
  if (boardNavigation) {
    boardNavigation.changeToWriteMode()
  }
}

// 게시글 수정
const editPost = (board) => {
  if (boardNavigation) {
    // 수정할 게시글 정보를 전달하면서 글쓰기 모드로 변경
    boardNavigation.editBoard = board
    boardNavigation.changeToWriteMode()
  }
}

// 게시글 삭제
const deletePost = (boardId) => {
  deleteTargetId.value = boardId
  showDeleteModal.value = true
}

// 삭제 확인
const confirmDelete = async () => {
  if (!deleteTargetId.value) return

  isDeleting.value = true

  try {
    await deleteBoard(deleteTargetId.value)
    toast.success('게시글이 삭제되었습니다')

    // 목록 새로고침
    await loadBoards(currentPage.value)
  } catch (error) {
    console.error('게시글 삭제 실패:', error)
    toast.error('게시글 삭제에 실패했습니다')
  } finally {
    isDeleting.value = false
    showDeleteModal.value = false
    deleteTargetId.value = null
  }
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
    return date.toLocaleDateString()
  } catch {
    return '날짜 오류'
  }
}

// 카테고리 변경 감지
watch(
  () => props.categoryId,
  () => {
    currentPage.value = 0
    loadBoards(0)
  },
)

// 컴포넌트 마운트 시 데이터 로드
onMounted(() => {
  loadBoards(0)
})
</script>
