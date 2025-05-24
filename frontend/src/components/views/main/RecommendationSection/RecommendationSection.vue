<template>
  <section class="py-8" :class="backgroundColor">
    <div class="container mx-auto px-4 sm:px-6 lg:px-8 max-w-7xl">
      <!-- 섹션 제목 -->
      <div class="flex items-center justify-between mb-8">
        <h2 class="text-2xl font-bold text-gray-900 flex items-center gap-2">
          <i :class="['pi', sectionIcon, 'text-[var(--primary-color)]']"></i>
          <span>{{ title }}</span>
          <span v-if="displayItems.length" class="text-sm text-gray-500 font-normal ml-2"
            >({{ displayItems.length }})</span
          >
        </h2>

        <!-- 캐러셀 네비게이션 버튼 (충분한 데이터가 있을 때만 표시) -->
        <div v-if="shouldShowNavigation" class="flex items-center gap-2">
          <button
            @click="goToPrevPage()"
            class="w-8 h-8 rounded-full bg-white border border-gray-200 shadow-sm flex items-center justify-center hover:shadow-md transition-all duration-200 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            <i class="pi pi-chevron-left text-gray-600 text-sm"></i>
          </button>
          <button
            @click="goToNextPage()"
            class="w-8 h-8 rounded-full bg-white border border-gray-200 shadow-sm flex items-center justify-center hover:shadow-md transition-all duration-200 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            <i class="pi pi-chevron-right text-gray-600 text-sm"></i>
          </button>
        </div>
      </div>

      <!-- 수동 구현 캐러셀 (가로 배치) -->
      <div v-if="displayItems.length > 0" class="relative overflow-hidden">
        <div
          class="flex transition-transform duration-300 ease-in-out gap-4"
          :style="{ transform: `translateX(-${currentPage * 100}%)` }"
        >
          <!-- 각 페이지 -->
          <div
            v-for="(page, pageIndex) in paginatedItems"
            :key="pageIndex"
            class="flex-none w-full flex gap-4"
          >
            <!-- 각 페이지 내 아이템들 -->
            <div
              v-for="item in page"
              :key="item.id || item.planId || item.boardId"
              class="flex-1 cursor-pointer"
              @click="handleItemClick(item)"
            >
              <!-- Plan 카드 (정사각형) -->
              <div
                v-if="cardType === 'plan'"
                class="bg-white rounded-xl shadow-lg overflow-hidden h-full"
              >
                <!-- 이미지 -->
                <div class="relative h-48 overflow-hidden">
                  <img
                    :src="item.image || getDefaultPlanImage()"
                    :alt="item.title"
                    class="w-full h-full object-cover"
                  />
                  <div class="absolute inset-0 bg-gradient-to-t from-black/50 to-transparent"></div>

                  <!-- 좋아요 수 오버레이 -->
                  <div
                    class="absolute bottom-3 right-3 bg-white/90 backdrop-blur-sm rounded-full px-2 py-1 flex items-center gap-1"
                  >
                    <i class="pi pi-heart-fill text-red-500 text-xs"></i>
                    <span class="text-xs font-medium text-gray-800">{{ item.likeCount || 0 }}</span>
                  </div>
                </div>

                <!-- 내용 -->
                <div class="p-4">
                  <h3
                    class="text-lg font-bold text-gray-900 mb-2 overflow-hidden text-ellipsis whitespace-nowrap"
                  >
                    {{ item.title }}
                  </h3>
                  <p class="text-sm text-gray-600 mb-3 line-clamp-2">
                    {{ item.content || item.description }}
                  </p>

                  <!-- 날짜 정보 -->
                  <div
                    v-if="item.startDate || item.endDate"
                    class="text-xs text-gray-500 flex items-center gap-1"
                  >
                    <i class="pi pi-calendar text-[var(--primary-color)]"></i>
                    <span>{{ formatDateRange(item.startDate, item.endDate) }}</span>
                  </div>
                </div>
              </div>

              <!-- Board 카드 (리스트형) -->
              <div v-else class="bg-white rounded-xl shadow-lg overflow-hidden h-full">
                <div class="p-4">
                  <div class="flex items-start gap-4">
                    <!-- 썸네일 (옵션) -->
                    <div v-if="item.image" class="flex-shrink-0">
                      <img
                        :src="item.image"
                        :alt="item.title"
                        class="w-16 h-16 rounded-lg object-cover"
                      />
                    </div>

                    <!-- 내용 -->
                    <div class="flex-1 min-w-0">
                      <div class="flex items-start justify-between mb-2">
                        <h3
                          class="text-lg font-bold text-gray-900 overflow-hidden text-ellipsis whitespace-nowrap flex-1"
                        >
                          {{ item.title }}
                        </h3>
                        <div class="flex items-center gap-1 ml-2 flex-shrink-0">
                          <i class="pi pi-heart-fill text-red-500 text-xs"></i>
                          <span class="text-xs font-medium text-gray-600">{{
                            item.likeCount || 0
                          }}</span>
                        </div>
                      </div>

                      <!-- 카테고리 -->
                      <div v-if="item.categoryName" class="mb-2">
                        <span
                          class="inline-block px-2 py-1 bg-[var(--light-color)]/30 text-[var(--primary-color)] text-xs rounded-full"
                        >
                          {{ item.categoryName }}
                        </span>
                      </div>

                      <!-- 내용 미리보기 -->
                      <p class="text-sm text-gray-600 line-clamp-2 mb-3">
                        {{ getContentPreview(item.content) }}
                      </p>

                      <!-- 메타 정보 -->
                      <div class="flex items-center justify-between text-xs text-gray-500">
                        <div class="flex items-center gap-3">
                          <span class="flex items-center gap-1">
                            <i class="pi pi-eye"></i>
                            {{ item.viewCount || 0 }}
                          </span>
                          <span class="flex items-center gap-1">
                            <i class="pi pi-clock"></i>
                            {{ formatDate(item.createdAt) }}
                          </span>
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

      <!-- 로딩 상태 -->
      <div v-else-if="isLoading" class="flex justify-center items-center py-12">
        <i class="pi pi-spin pi-spinner text-2xl text-[var(--primary-color)]"></i>
        <span class="ml-2 text-gray-600">데이터를 불러오는 중...</span>
      </div>

      <!-- 데이터 없음 -->
      <div v-else class="text-center py-12">
        <i class="pi pi-inbox text-4xl text-gray-400 mb-4"></i>
        <p class="text-gray-500">표시할 데이터가 없습니다.</p>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import planAPI from '@/api/plan'
import boardAPI from '@/api/board'

// Props
const props = defineProps({
  title: {
    type: String,
    default: '인기 콘텐츠',
  },
  cardType: {
    type: String,
    default: 'plan', // 'plan' or 'board'
    validator: (value) => ['plan', 'board'].includes(value),
  },
  backgroundColor: {
    type: String,
    default: 'bg-gray-50',
  },
  numVisible: {
    type: Number,
    default: 4,
  },
  showNavigators: {
    type: Boolean,
    default: true,
  },
  showIndicators: {
    type: Boolean,
    default: false,
  },
  circular: {
    type: Boolean,
    default: true,
  },
  autoplayInterval: {
    type: Number,
    default: 0, // 0이면 자동재생 안함
  },
  showCatSilhouette: {
    type: Boolean,
    default: false, // 고양이 제거
  },
  sectionIcon: {
    type: String,
    default: 'pi-star',
  },
})

// 반응형 데이터
const items = ref([])
const isLoading = ref(false)
const currentPage = ref(0)

// 최대 12개 아이템만 표시
const displayItems = computed(() => items.value.slice(0, 12))

// 현재 화면 크기에 따른 표시 개수
const currentNumVisible = computed(() => {
  // 실제 구현에서는 window size에 따라 조정하거나 props 사용
  return props.numVisible
})

// 페이지네이션된 아이템들
const paginatedItems = computed(() => {
  const itemsPerPage = currentNumVisible.value
  const pages = []

  for (let i = 0; i < displayItems.value.length; i += itemsPerPage) {
    pages.push(displayItems.value.slice(i, i + itemsPerPage))
  }

  return pages
})

// 네비게이션 버튼 표시 여부
const shouldShowNavigation = computed(() => displayItems.value.length > currentNumVisible.value)

// 총 페이지 수
const totalPages = computed(() => paginatedItems.value.length)

// Events
const emit = defineEmits(['item-click'])

// 컴포넌트 마운트 시 데이터 로드
onMounted(() => {
  loadData()
})

// 이전 페이지로
const goToPrevPage = () => {
  if (currentPage.value > 0) {
    currentPage.value--
  }
}

// 다음 페이지로
const goToNextPage = () => {
  if (currentPage.value < totalPages.value - 1) {
    currentPage.value++
  }
}

// 데이터 로드 함수
const loadData = async () => {
  isLoading.value = true

  try {
    if (props.cardType === 'plan') {
      await loadPlans()
    } else if (props.cardType === 'board') {
      await loadBoards()
    }
  } catch (error) {
    console.error('데이터 로드 실패:', error)
    items.value = []
  } finally {
    isLoading.value = false
  }
}

// Plan 데이터 로드
const loadPlans = async () => {
  return new Promise((resolve, reject) => {
    planAPI.getPopularPlans(
      (response) => {
        const plans = response.data || []
        // Plan 데이터를 카드 형식에 맞게 변환
        items.value = plans.map((plan) => ({
          ...plan,
          id: plan.planId,
          image: getPlanImage(plan),
          description: plan.content,
        }))
        resolve()
      },
      (error) => {
        console.error('Plan 데이터 로드 실패:', error)
        reject(error)
      },
    )
  })
}

// Board 데이터 로드
const loadBoards = async () => {
  return new Promise((resolve, reject) => {
    boardAPI.getPopularBoards(
      (response) => {
        const boards = response.data?.boards || []
        // Board 데이터를 카드 형식에 맞게 변환
        items.value = boards.map((board) => ({
          ...board,
          id: board.boardId,
          description: board.content,
        }))
        resolve()
      },
      (error) => {
        console.error('Board 데이터 로드 실패:', error)
        reject(error)
      },
    )
  })
}

// Plan 이미지 추출 (첫 번째 attraction의 이미지 사용)
const getPlanImage = (plan) => {
  if (plan.attractions && plan.attractions.length > 0) {
    // attractions에서 첫 번째 이미지 추출 (실제 구조에 따라 조정 필요)
    return plan.attractions[0].image || getDefaultPlanImage()
  }
  return getDefaultPlanImage()
}

// 기본 Plan 이미지
const getDefaultPlanImage = () => {
  return 'https://images.unsplash.com/photo-1488646953014-85cb44e25828?w=300&h=200&fit=crop'
}

// 내용 미리보기 (HTML 태그 제거)
const getContentPreview = (content) => {
  if (!content) return ''
  // HTML 태그 제거
  const textContent = content.replace(/<[^>]*>/g, '')
  // 150자로 제한
  return textContent.length > 150 ? textContent.substring(0, 150) + '...' : textContent
}

// 날짜 범위 포맷
const formatDateRange = (startDate, endDate) => {
  if (!startDate && !endDate) return ''

  const formatDate = (date) => {
    if (!date) return ''
    return new Date(date).toLocaleDateString('ko-KR', {
      month: 'short',
      day: 'numeric',
    })
  }

  if (startDate && endDate) {
    return `${formatDate(startDate)} - ${formatDate(endDate)}`
  } else if (startDate) {
    return formatDate(startDate)
  } else {
    return formatDate(endDate)
  }
}

// 날짜 포맷
const formatDate = (date) => {
  if (!date) return ''
  const now = new Date()
  const target = new Date(date)
  const diffMs = now - target
  const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24))

  if (diffDays === 0) {
    return '오늘'
  } else if (diffDays === 1) {
    return '어제'
  } else if (diffDays < 7) {
    return `${diffDays}일 전`
  } else {
    return target.toLocaleDateString('ko-KR', {
      month: 'short',
      day: 'numeric',
    })
  }
}

// 아이템 클릭 핸들러
const handleItemClick = (item) => {
  emit('item-click', item)
  console.log('선택된 아이템:', item)
}
</script>
