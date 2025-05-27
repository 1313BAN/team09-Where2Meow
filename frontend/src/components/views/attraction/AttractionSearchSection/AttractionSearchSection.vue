<template>
  <section class="bg-white py-8 px-4 border-b border-gray-200">
    <div class="container mx-auto px-4 sm:px-6 lg:px-8 max-w-7xl">
      <!-- 페이지 헤더 -->
      <div class="mb-8">
        <h1 class="text-3xl font-bold text-gray-900">여행지 둘러보기</h1>
        <p class="text-gray-600 mt-2">전 세계의 아름다운 여행지를 검색하고 탐색해보세요</p>
      </div>

      <!-- 검색 폼 -->
      <div class="bg-gray-50 rounded-xl p-6 mb-6">
        <!-- 기본 텍스트 검색 -->
        <div class="flex gap-4 mb-4">
          <div class="flex-1 relative">
            <input
              v-model="searchKeyword"
              @keypress.enter="handleSearch"
              type="text"
              placeholder="여행지명, 지역명으로 검색해보세요..."
              class="w-full min-h-[48px] border border-gray-300 rounded-xl hover:border-gray-400 focus:border-[var(--secondary-color)] focus:outline-none bg-white transition-all duration-200 pl-12 pr-4 py-3 text-base"
            />
            <i
              class="pi pi-search absolute left-4 top-1/2 transform -translate-y-1/2 text-gray-400"
            ></i>
          </div>

          <Button
            label="검색"
            icon="pi pi-search"
            :loading="isSearching"
            @click="handleSearch"
            class="search-button"
            pt:root="bg-gradient-to-r from-[var(--primary-color)] to-[var(--secondary-color)] active:scale-95 transition-all duration-200 ease-in-out rounded-xl px-6 py-3 shadow-md text-white border-none flex items-center justify-center gap-2 cursor-pointer min-h-[48px]"
            pt:label="text-white font-semibold text-base tracking-wide"
            pt:icon="text-white"
          />
        </div>

        <!-- 상세 검색 토글 -->
        <div class="flex items-center justify-between">
          <button
            @click="toggleAdvancedSearch"
            class="flex items-center gap-2 text-sm text-gray-600 hover:text-gray-900 transition-colors cursor-pointer"
          >
            <i :class="['pi', showAdvancedSearch ? 'pi-chevron-up' : 'pi-chevron-down']"></i>
            <span>{{ showAdvancedSearch ? '상세 검색 닫기' : '상세 검색 열기' }}</span>
          </button>

          <button
            v-if="hasActiveFilters"
            @click="clearAllFilters"
            class="text-sm text-red-600 hover:text-red-700 transition-colors cursor-pointer"
          >
            <i class="pi pi-times mr-1"></i>
            필터 초기화
          </button>
        </div>

        <!-- 상세 검색 옵션 -->
        <div v-if="showAdvancedSearch" class="mt-4 pt-4 border-t border-gray-200">
          <!-- 지역 선택 -->
          <div class="grid grid-cols-1 md:grid-cols-3 gap-4 mb-4">
            <Select
              v-model="searchForm.country"
              :options="countrySuggestions"
              optionLabel="countryName"
              optionValue="countryId"
              placeholder="국가 선택"
              showClear
              pt:root="relative cursor-pointer min-h-[48px] border border-gray-300 rounded-xl hover:border-gray-400 focus-within:!border-[var(--secondary-color)] bg-white transition-all duration-200"
              pt:label="absolute left-3 top-1/2 -translate-y-1/2"
              pt:dropdown="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500"
              pt:dropdownIcon="w-4 h-4"
              pt:overlay="bg-white border border-gray-200 rounded-xl shadow-md mt-1 z-50"
              pt:list="max-h-[220px] overflow-auto box-border py-0"
              pt:option="px-4 py-2 cursor-pointer hover:bg-gray-100 text-sm leading-[1.5rem]"
              pt:optionLabel="text-sm leading-5"
              pt:clearIcon="absolute right-10 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-500 hover:text-[var(--danger-color)] transition-colors duration-150"
            />

            <Select
              v-model="searchForm.state"
              :options="stateSuggestions"
              optionLabel="stateName"
              optionValue="stateCode"
              placeholder="지역 선택"
              :disabled="!searchForm.country"
              showClear
              pt:root="relative cursor-pointer min-h-[48px] border border-gray-300 rounded-xl hover:border-gray-400 focus-within:!border-[var(--secondary-color)] bg-white transition-all duration-200"
              pt:label="absolute left-3 top-1/2 -translate-y-1/2"
              pt:dropdown="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500"
              pt:dropdownIcon="w-4 h-4"
              pt:overlay="bg-white border border-gray-200 rounded-xl shadow-md mt-1 z-50"
              pt:list="max-h-[220px] overflow-auto box-border py-0"
              pt:option="px-4 py-2 cursor-pointer hover:bg-gray-100 text-sm leading-[1.5rem]"
              pt:optionLabel="text-sm leading-5"
              pt:clearIcon="absolute right-10 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-500 hover:text-[var(--danger-color)] transition-colors duration-150"
            />

            <Select
              v-model="searchForm.city"
              :options="citySuggestions"
              optionLabel="cityName"
              optionValue="cityCode"
              placeholder="도시 선택"
              :disabled="!searchForm.state"
              showClear
              pt:root="relative cursor-pointer min-h-[48px] border border-gray-300 rounded-xl hover:border-gray-400 focus-within:!border-[var(--secondary-color)] bg-white transition-all duration-200"
              pt:label="absolute left-3 top-1/2 -translate-y-1/2"
              pt:dropdown="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500"
              pt:dropdownIcon="w-4 h-4"
              pt:overlay="bg-white border border-gray-200 rounded-xl shadow-md mt-1 z-50"
              pt:list="max-h-[220px] overflow-auto box-border py-0"
              pt:option="px-4 py-2 cursor-pointer hover:bg-gray-100 text-sm leading-[1.5rem]"
              pt:optionLabel="text-sm leading-5"
              pt:clearIcon="absolute right-10 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-500 hover:text-[var(--danger-color)] transition-colors duration-150"
            />
          </div>

          <!-- 카테고리 선택 (드롭다운 방식) -->
          <div class="mb-4">
            <label class="block text-sm font-medium text-gray-700 mb-2">카테고리</label>
            <Select
              v-model="selectedCategory"
              :options="categories"
              optionLabel="name"
              optionValue="id"
              placeholder="카테고리 선택"
              showClear
              pt:root="relative cursor-pointer min-h-[48px] border border-gray-300 rounded-xl hover:border-gray-400 focus-within:!border-[var(--secondary-color)] bg-white transition-all duration-200 w-full max-w-sm"
              pt:label="absolute left-3 top-1/2 -translate-y-1/2"
              pt:dropdown="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500"
              pt:dropdownIcon="w-4 h-4"
              pt:overlay="bg-white border border-gray-200 rounded-xl shadow-md mt-1 z-50"
              pt:list="max-h-[220px] overflow-auto box-border py-0"
              pt:option="px-4 py-2 cursor-pointer hover:bg-gray-100 text-sm leading-[1.5rem]"
              pt:optionLabel="text-sm leading-5"
              pt:clearIcon="absolute right-10 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-500 hover:text-[var(--danger-color)] transition-colors duration-150"
            />
          </div>
        </div>
      </div>

      <!-- 현재 적용된 필터 표시 -->
      <div v-if="activeFilters.length > 0" class="mb-6">
        <div class="flex items-center gap-2 flex-wrap">
          <span class="text-sm text-gray-600">적용된 필터:</span>
          <div
            v-for="filter in activeFilters"
            :key="filter.key"
            class="inline-flex items-center gap-1 px-3 py-1 bg-[var(--primary-10)] text-[var(--primary-color)] rounded-full text-sm"
          >
            <span>{{ filter.label }}</span>
            <button
              @click="removeFilter(filter.key)"
              class="cursor-pointer ml-1 text-[var(--primary-color)] hover:text-[var(--danger-color)] transition-colors"
            >
              <i class="pi pi-times text-xs"></i>
            </button>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import Button from 'primevue/button'
import Select from 'primevue/select'
import addressAPI from '@/api/address'
import attractionAPI from '@/api/attraction'

const emit = defineEmits(['search'])

// 상태 관리
const isSearching = ref(false)
const showAdvancedSearch = ref(false)
const searchKeyword = ref('')

const searchForm = reactive({
  country: null,
  state: null,
  city: null,
})

const selectedCategory = ref(null)

// 주소 데이터
const countrySuggestions = ref([])
const stateSuggestions = ref([])
const citySuggestions = ref([])

// 카테고리 목록
const categories = ref([])

// 활성 필터 계산
const activeFilters = computed(() => {
  const filters = []

  if (searchKeyword.value) {
    filters.push({ key: 'keyword', label: `검색: ${searchKeyword.value}` })
  }

  if (searchForm.country) {
    const country = countrySuggestions.value.find((c) => c.countryId === searchForm.country)
    if (country) {
      filters.push({ key: 'country', label: `국가: ${country.countryName}` })
    }
  }

  if (searchForm.state) {
    const state = stateSuggestions.value.find((s) => s.stateCode === searchForm.state)
    if (state) {
      filters.push({ key: 'state', label: `지역: ${state.stateName}` })
    }
  }

  if (searchForm.city) {
    const city = citySuggestions.value.find((c) => c.cityId === searchForm.city)
    if (city) {
      filters.push({ key: 'city', label: `도시: ${city.cityName}` })
    }
  }

  if (selectedCategory.value) {
    const category = categories.value.find((c) => c.id === selectedCategory.value)
    if (category) {
      filters.push({ key: 'category', label: `카테고리: ${category.name}` })
    }
  }

  return filters
})

const hasActiveFilters = computed(() => activeFilters.value.length > 0)

// 상세 검색 토글
const toggleAdvancedSearch = () => {
  showAdvancedSearch.value = !showAdvancedSearch.value
}

// 필터 제거
const removeFilter = (filterKey) => {
  switch (filterKey) {
    case 'keyword':
      searchKeyword.value = ''
      break
    case 'country':
      searchForm.country = null
      break
    case 'state':
      searchForm.state = null
      break
    case 'city':
      searchForm.city = null
      break
    case 'category':
      selectedCategory.value = null
      break
  }
  handleSearch()
}

// 모든 필터 초기화
const clearAllFilters = () => {
  searchKeyword.value = ''
  searchForm.country = null
  searchForm.state = null
  searchForm.city = null
  selectedCategory.value = null
  handleSearch()
}

// 검색 실행
const handleSearch = () => {
  isSearching.value = true

  const searchData = {
    keyword: searchKeyword.value.trim() || null,
    countryId: searchForm.country,
    stateId: searchForm.state,
    cityId: searchForm.city,
    categoryId: selectedCategory.value,
  }

  emit('search', searchData)

  setTimeout(() => {
    isSearching.value = false
  }, 500)
}

// 컴포넌트 마운트 시 국가 목록 및 카테고리 로드
onMounted(async () => {
  try {
    // 국가 목록 로드
    addressAPI.getCountries(
      (res) => {
        countrySuggestions.value = res.data
      },
      (err) => {
        console.error('국가 목록 불러오기 실패', err)
      },
    )

    // 카테고리 목록 로드
    attractionAPI.attractionApi
      .getAllCategories()
      .then((response) => {
        categories.value = response.data.map((category) => ({
          id: category.categoryId,
          name: category.categoryName,
        }))
      })
      .catch((error) => {
        console.error('카테고리 목록 불러오기 실패', error)
      })
  } catch (error) {
    console.error('초기 데이터 로드 중 오류:', error)
  }
})

// 국가 선택 시 지역 목록 로드
watch(
  () => searchForm.country,
  async (newCountry) => {
    if (newCountry) {
      searchForm.state = null
      searchForm.city = null
      stateSuggestions.value = []
      citySuggestions.value = []

      try {
        addressAPI.getStates(
          newCountry,
          (res) => {
            stateSuggestions.value = res.data
          },
          (err) => {
            console.error('지역 목록 불러오기 실패', err)
          },
        )
      } catch (error) {
        console.error('지역 목록 로드 중 오류:', error)
      }
    } else {
      searchForm.state = null
      searchForm.city = null
      stateSuggestions.value = []
      citySuggestions.value = []
    }
  },
)

// 지역 선택 시 도시 목록 로드
watch(
  () => searchForm.state,
  async (newState) => {
    if (newState) {
      searchForm.city = null
      citySuggestions.value = []

      try {
        addressAPI.getCities(
          newState,
          (res) => {
            citySuggestions.value = res.data
          },
          (err) => {
            console.error('도시 목록 불러오기 실패', err)
          },
        )
      } catch (error) {
        console.error('도시 목록 로드 중 오류:', error)
      }
    } else {
      searchForm.city = null
      citySuggestions.value = []
    }
  },
)
</script>

<style scoped>
.search-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .grid.grid-cols-1.md\:grid-cols-3 {
    grid-template-columns: 1fr;
    gap: 1rem;
  }
}
</style>
