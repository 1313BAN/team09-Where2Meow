<template>
  <section class="py-8">
    <div class="container mx-auto px-4 sm:px-6 lg:px-8 max-w-7xl">
      <!-- 검색 결과 헤더 -->
      <div class="flex flex-col lg:flex-row lg:items-center justify-between mb-6 gap-4">
        <div class="flex items-center gap-4">
          <h2 class="text-xl font-semibold text-gray-900">
            {{ getResultTitle() }}
          </h2>
          <span v-if="totalElements > 0" class="text-sm text-gray-500">
            총 {{ totalElements.toLocaleString() }}개의 여행지
          </span>
        </div>

        <!-- 정렬 및 보기 옵션 -->
        <div class="flex items-center gap-3">
          <!-- 정렬 선택 -->
          <div class="relative">
            <select
              v-model="sortOption"
              @change="handleSortChange"
              class="relative cursor-pointer min-h-[40px] border border-gray-300 rounded-xl hover:border-gray-400 focus:border-[var(--secondary-color)] focus:outline-none bg-white transition-all duration-200 px-3 py-2 pr-8 text-sm appearance-none"
            >
              <option value="attractionName-asc">이름순</option>
              <option value="attractionName-desc">이름 역순</option>
              <option value="readcount-desc">인기순</option>
            </select>
            <i
              class="pi pi-chevron-down absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-500 pointer-events-none"
            ></i>
          </div>
        </div>
      </div>

      <!-- 로딩 상태 -->
      <div v-if="isLoading" class="flex justify-center py-12">
        <div class="flex items-center gap-3 text-gray-600">
          <i class="pi pi-spinner pi-spin text-xl"></i>
          <span>여행지를 불러오는 중...</span>
        </div>
      </div>

      <!-- 여행지 그리드 -->
      <div v-else>
        <!-- 검색 결과 없음 -->
        <div v-if="attractions.length === 0" class="text-center py-12">
          <div
            class="w-16 h-16 rounded-full bg-gray-100 flex items-center justify-center mx-auto mb-4"
          >
            <i class="pi pi-map-marker text-gray-400 text-2xl"></i>
          </div>
          <h3 class="text-lg font-medium text-gray-900 mb-2">검색 결과가 없습니다</h3>
          <p class="text-gray-600 mb-4">다른 검색 조건을 시도해보세요.</p>
          <button
            @click="clearFilters"
            class="px-4 py-2 bg-gradient-to-r from-[var(--primary-color)] to-[var(--secondary-color)] text-white rounded-xl shadow-md hover:shadow-lg active:scale-95 transition-all duration-200 ease-in-out cursor-pointer"
          >
            전체 여행지 보기
          </button>
        </div>

        <!-- 여행지 카드 그리드 -->
        <div
          v-else
          :class="[
            'grid gap-6 mb-8',
            gridColumns === 3
              ? 'grid-cols-1 md:grid-cols-2 lg:grid-cols-3'
              : 'grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4',
          ]"
        >
          <AttractionCard
            v-for="attraction in attractions"
            :key="attraction.attractionId"
            :attraction="attraction"
            @click="handleAttractionClick"
          />
        </div>

        <!-- 페이지네이션 -->
        <div v-if="totalPages > 1" class="flex justify-center mt-8">
          <div class="flex items-center gap-2">
            <button
              @click="changePage(currentPage - 1)"
              :disabled="currentPage === 0"
              class="cursor-pointer p-2 rounded-lg border border-gray-300 disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50 transition-colors"
            >
              <i class="pi pi-angle-left"></i>
            </button>

            <button
              v-for="page in visiblePages"
              :key="page"
              @click="changePage(page)"
              :class="[
                'cursor-pointer px-3 py-2 rounded-lg border transition-colors',
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
              class="cursor-pointer p-2 rounded-lg border border-gray-300 disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50 transition-colors"
            >
              <i class="pi pi-angle-right"></i>
            </button>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import AttractionCard from './AttractionCard.vue'
import attractionAPI from '@/api/attraction'
import { toast } from 'vue-sonner'

const router = useRouter()

const props = defineProps({
  searchParams: {
    type: Object,
    default: () => ({}),
  },
})

// 상태 관리
const attractions = ref([])
const isLoading = ref(false)
const sortOption = ref('attractionName-asc')
const gridColumns = ref(4)
const categories = ref([])

// 반응형 pageSize
const windowWidth = ref(window.innerWidth)
const pageSize = computed(() => {
  if (windowWidth.value < 768) {
    // 모바일: 1열 × 6행 = 6개
    return 6
  } else if (windowWidth.value < 1024) {
    // 태블릿: 2열 × 4행 = 8개
    return 8
  } else if (windowWidth.value < 1280) {
    // 데스크톱 소: 3열 × 4행 = 12개
    return 12
  } else {
    // 데스크톱 대: 4열 × 4행 = 16개
    return 16
  }
})

// window resize 이벤트 처리
const handleResize = () => {
  windowWidth.value = window.innerWidth
}

// 페이지네이션
const currentPage = ref(0)
const totalPages = ref(0)
const totalElements = ref(0)

// 검색 결과 제목 생성
const getResultTitle = () => {
  if (!props.searchParams || Object.keys(props.searchParams).length === 0) {
    return '전체 여행지'
  }

  let title = '검색 결과'
  const { keyword, countryId, stateId, cityId, categoryId } = props.searchParams

  if (keyword) {
    title = `"${keyword}" 검색 결과`
  } else if (categoryId) {
    const category = categories.value.find((cat) => cat.categoryId === categoryId)
    title = category ? `${category.categoryName} 여행지` : '여행지'
  }

  return title
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

// 여행지 목록 로드
const loadAttractions = async (page = 0) => {
  isLoading.value = true

  try {
    const [sortField, sortDirection] = sortOption.value.split('-')

    const params = {
      page,
      size: pageSize.value, // computed 값 사용
      sort: `${sortField},${sortDirection}`,
      ...props.searchParams,
    }

    const hasCondition = ['keyword', 'countryId', 'stateId', 'cityId', 'categoryId'].some(
      (key) => !!params[key],
    )
    if (!hasCondition) {
      params.countryId = 1
    }

    // searchAttractions 사용
    const response = await attractionAPI.attractionApi.searchAttractions(params)

    if (response.data) {
      attractions.value = response.data.content || []
      // API 응답 구조에 맞게 수정
      if (response.data.page) {
        totalPages.value = response.data.page.totalPages || 0
        totalElements.value = response.data.page.totalElements || 0
        // currentPage는 여전히 파라미터 page 사용
      } else {
        // fallback: 기존 방식
        totalPages.value = response.data.totalPages || 0
        totalElements.value = response.data.totalElements || 0
      }
      currentPage.value = page
    }
  } catch (error) {
    console.error('여행지 목록 로드 실패:', error)
    toast.error('여행지 목록을 불러오는데 실패했습니다')
    attractions.value = []
    totalPages.value = 0
    totalElements.value = 0
  } finally {
    isLoading.value = false
  }
}

// 정렬 변경 핸들러
const handleSortChange = () => {
  currentPage.value = 0
  loadAttractions(0)
}

// 페이지 변경
const changePage = (page) => {
  if (page >= 0 && page < totalPages.value) {
    currentPage.value = page
    loadAttractions(page)
    // 페이지 상단으로 스크롤
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }
}

// 필터 초기화
const clearFilters = () => {
  // 부모 컴포넌트에 필터 초기화 요청
  loadAttractions(0)
}

// 이벤트 핸들러들
const handleAttractionClick = (attraction) => {
  router.push({
    name: 'attractionDetail',
    params: { attractionId: attraction.attractionId },
  })
}

// 검색 파라미터 변경 감지
watch(
  () => props.searchParams,
  () => {
    currentPage.value = 0
    loadAttractions(0)
  },
  { deep: true },
)

// pageSize 변경 감지 (viewport 크기 변경 시)
watch(
  () => pageSize.value,
  (newSize, oldSize) => {
    if (newSize !== oldSize) {
      currentPage.value = 0
      loadAttractions(0)
    }
  },
)

// 컴포넌트 마운트 시 데이터 로드
onMounted(async () => {
  // window resize 이벤트 리스너 등록
  window.addEventListener('resize', handleResize)

  // 카테고리 데이해 로드
  try {
    attractionAPI.attractionApi
      .getAllCategories()
      .then((response) => {
        categories.value = response.data
      })
      .catch((error) => {
        console.error('카테고리 목록 불러오기 실패', error)
      })
  } catch (error) {
    console.error('카테고리 데이터 로드 중 오류:', error)
  }

  loadAttractions(0)
})

// 컴포넌트 언마운트 시 이벤트 리스너 제거
onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
/* 그리드 반응형 조정 */
@media (max-width: 768px) {
  .grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 1024px) {
  .grid.xl\:grid-cols-4 {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
