<template>
  <section class="bg-white py-8 px-4">
    <div class="container mx-auto px-4 sm:px-6 lg:px-8 max-w-7xl">
      <!-- 검색 제목 -->
      <h2 class="text-2xl font-bold mb-6 text-gray-900">{{ title }}</h2>

      <!-- 검색 폼 -->
      <form @submit.prevent="handleSearch" class="grid grid-cols-1 md:grid-cols-4 gap-4 mb-8 px-2">
        <!-- 국가 선택 -->
        <Select
          v-model="searchForm.country"
          :options="countrySuggestions"
          optionLabel="countryName"
          optionValue="countryId"
          placeholder="국가 선택"
          pt:root="relative cursor-pointer min-h-[48px] border border-gray-300 rounded-xl hover:border-gray-400 focus-within:!border-[var(--secondary-color)] bg-white transition-all duration-200"
          pt:label="absolute left-3 top-1/2 -translate-y-1/2 "
          pt:dropdown="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500"
          pt:dropdownIcon="w-4 h-4"
          pt:overlay="bg-white border border-gray-200 rounded-xl shadow-md mt-1 z-50"
          pt:list="max-h-[220px] overflow-auto box-border py-0"
          pt:option="px-4 py-2 cursor-pointer hover:bg-gray-100 text-sm leading-[1.5rem]"
          pt:optionLabel="text-sm leading-5"
        />

        <!-- 지역 선택 -->
        <Select
          v-model="searchForm.state"
          :options="stateSuggestions"
          optionLabel="stateName"
          optionValue="stateCode"
          placeholder="지역 선택"
          :disabled="!searchForm.country"
          pt:root="relative cursor-pointer min-h-[48px] border border-gray-300 rounded-xl hover:border-gray-400 focus-within:!border-[var(--secondary-color)] bg-white transition-all duration-200"
          pt:label="absolute left-3 top-1/2 -translate-y-1/2 "
          pt:dropdown="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500"
          pt:dropdownIcon="w-4 h-4"
          pt:overlay="bg-white border border-gray-200 rounded-xl shadow-md mt-1 z-50"
          pt:list="max-h-[220px] overflow-auto box-border py-0"
          pt:option="px-4 py-2 cursor-pointer hover:bg-gray-100 text-sm leading-[1.5rem]"
          pt:optionLabel="text-sm leading-5"
        />

        <!-- 도시 선택 -->
        <Select
          v-model="searchForm.city"
          :options="citySuggestions"
          optionLabel="cityName"
          optionValue="cityId"
          placeholder="도시 선택"
          :disabled="!searchForm.state"
          pt:root="relative cursor-pointer min-h-[48px] border border-gray-300 rounded-xl hover:border-gray-400 focus-within:!border-[var(--secondary-color)] bg-white transition-all duration-200"
          pt:label="absolute left-3 top-1/2 -translate-y-1/2 "
          pt:dropdown="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500"
          pt:dropdownIcon="w-4 h-4"
          pt:overlay="bg-white border border-gray-200 rounded-xl shadow-md mt-1 z-50"
          pt:list="max-h-[220px] overflow-auto box-border py-0"
          pt:option="px-4 py-2 cursor-pointer hover:bg-gray-100 text-sm leading-[1.5rem]"
          pt:optionLabel="text-sm leading-5"
        />

        <!-- 검색 버튼 -->
        <Button
          label="검색하기"
          icon="pi pi-search"
          :loading="isSearching"
          @click="handleSearch"
          class="w-full search-button"
          pt:root="bg-gradient-to-r from-[var(--primary-color)] to-[var(--secondary-color)] active:scale-95 transition-all duration-200 ease-in-out rounded-xl px-6 py-3 shadow-md text-white border-none flex items-center justify-center gap-2 cursor-pointer min-h-[48px]"
          pt:label="text-white font-semibold text-base tracking-wide"
          pt:icon="text-white"
        />
      </form>

      <!-- 카테고리 선택 -->
      <div class="mb-6 px-2">
        <h3 class="text-lg font-semibold mb-4 text-gray-900">카테고리</h3>
        <div class="grid grid-cols-4 md:grid-cols-8 gap-4 justify-items-center max-w-4xl mx-auto">
          <div
            v-for="category in categories"
            :key="category.id"
            class="flex flex-col items-center cursor-pointer group"
            :class="{ 'selected-category': selectedCategory === category.id }"
            @click="handleCategoryClick(category)"
          >
            <div
              class="w-16 h-16 rounded-full bg-gray-100 flex items-center justify-center mb-2 overflow-hidden group-hover:bg-gray-200 transition-all duration-200 group-hover:scale-105"
              :class="{
                'bg-gradient-to-r from-[var(--primary-color)] to-[var(--secondary-color)] border-2 border-[var(--secondary-color)] scale-105 shadow-[0_4px_12px_rgba(0,237,179,0.3)]':
                  selectedCategory === category.id,
                'group-hover:shadow-md': selectedCategory !== category.id,
              }"
            >
              <img
                v-if="category.image"
                :src="category.image"
                :alt="category.name"
                class="w-full h-full object-cover"
                :class="{ 'opacity-90': selectedCategory === category.id }"
              />
              <i
                v-else
                :class="[
                  category.icon,
                  'text-2xl',
                  selectedCategory === category.id ? 'text-white' : 'text-gray-600',
                ]"
              ></i>
            </div>
            <span
              class="text-xs text-center group-hover:text-gray-900 transition-colors"
              :class="
                selectedCategory === category.id
                  ? 'text-[var(--secondary-color)] font-semibold'
                  : 'text-gray-700'
              "
            >
              {{ category.name }}
            </span>
          </div>
        </div>
      </div>
    </div>
    <!-- 인기 장소 섹션 -->
    <PopularAttractions
      :attractions="attractions"
      :is-loading="isSearching"
      :has-searched="hasSearched"
      :search-params="searchParams"
      @attraction-click="handleAttractionClick"
      @retry-search="handleRetrySearch"
    />
  </section>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import Button from 'primevue/button'
import Select from 'primevue/select'
import addressAPI from '@/api/address'
import attractionAPI from '@/api/attraction'
import PopularAttractions from './PopularAttractions.vue'

const props = defineProps({
  title: {
    type: String,
    default: '인기 장소 TOP 6 검색',
  },
})

const isSearching = ref(false)
const searchForm = reactive({
  country: null,
  state: null,
  city: null,
})

// 인기 장소 관련 상태
const attractions = ref([])
const hasSearched = ref(false)
const searchParams = ref({})

// 선택된 카테고리 (단일 선택)
const selectedCategory = ref(null)

// 전체 데이터
const countrySuggestions = ref([])
const stateSuggestions = ref([])
const citySuggestions = ref([])

// 컴포넌트 마운트 시 국가 목록 로드
onMounted(async () => {
  try {
    addressAPI.getCountries(
      (res) => {
        countrySuggestions.value = res.data
      },
      (err) => {
        console.error('국가 목록 불러오기 실패', err)
      },
    )
  } catch (error) {
    console.error('국가 목록 로드 중 오류:', error)
  }
})

// 국가 선택 시 지역 목록 로드
watch(
  () => searchForm.country,
  async (newCountry) => {
    if (newCountry) {
      // 이전 선택 초기화
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
      // 국가 선택 해제 시 모든 하위 선택 초기화
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
      // 이전 도시 선택 초기화
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
      // 지역 선택 해제 시 도시 선택 초기화
      searchForm.city = null
      citySuggestions.value = []
    }
  },
)

const emit = defineEmits(['search', 'category-click'])

// 인기 장소 검색 함수
const searchPopularAttractions = async () => {
  isSearching.value = true
  hasSearched.value = true

  try {
    // 검색 파라미터 구성
    const params = {
      countryId: searchForm.country,
      stateId: getStateIdFromCode(searchForm.state),
      cityId: searchForm.city,
      categoryId: selectedCategory.value,
      page: 0,
      size: 6,
      sort: 'reviewCount,desc;attractionName,asc', // 리뷰 수 많은 순, 같으면 이름 순
    }

    // 검색 파라미터 저장 (제목 생성용)
    searchParams.value = {
      stateName: getSelectedStateName(),
      cityName: getSelectedCityName(),
      categoryName: getSelectedCategoryName(),
    }

    // API 호출
    attractionAPI.getAttractionListPaging(
      params,
      (response) => {
        attractions.value = response.data.content || []
        console.log('검색 결과:', attractions.value)
      },
      (error) => {
        console.error('인기 장소 검색 실패:', error)
        attractions.value = []
      },
    )
  } catch (error) {
    console.error('검색 중 오류:', error)
    attractions.value = []
  } finally {
    isSearching.value = false
  }
}

// State 코드로부터 ID 가져오기
const getStateIdFromCode = (stateCode) => {
  if (!stateCode) return null
  const state = stateSuggestions.value.find((s) => s.stateCode === stateCode)
  return state ? state.stateId : null
}

// 선택된 지역명 가져오기
const getSelectedStateName = () => {
  if (!searchForm.state) return null
  const state = stateSuggestions.value.find((s) => s.stateCode === searchForm.state)
  return state ? state.stateName : null
}

// 선택된 도시명 가져오기
const getSelectedCityName = () => {
  if (!searchForm.city) return null
  const city = citySuggestions.value.find((c) => c.cityId === searchForm.city)
  return city ? city.cityName : null
}

// 선택된 카테고리명 가져오기
const getSelectedCategoryName = () => {
  if (!selectedCategory.value) return null
  const category = categories.value.find((c) => c.id === selectedCategory.value)
  return category ? category.name : null
}

const handleSearch = async () => {
  await searchPopularAttractions()
  const searchData = {
    ...searchForm,
    category: selectedCategory.value,
  }
  emit('search', searchData)
  console.log('검색 조건:', searchData)
}

const handleCategoryClick = async (category) => {
  if (selectedCategory.value === category.id) {
    // 이미 선택된 카테고리면 선택 해제
    selectedCategory.value = null
  } else {
    // 새로운 카테고리 선택
    selectedCategory.value = category.id
  }

  // 카테고리 선택/해제 시 자동으로 검색 실행
  await searchPopularAttractions()

  emit('category-click', {
    category,
    selectedCategory: selectedCategory.value,
  })
  console.log('선택된 카테고리:', category, '현재 선택:', selectedCategory.value)
}

// 장소 클릭 핸들러
const handleAttractionClick = (attraction) => {
  console.log('장소 클릭:', attraction)
  // 상세 페이지로 이동하거나 모달 표시 등의 로직 추가 가능
}

// 재검색 핸들러
const handleRetrySearch = () => {
  searchPopularAttractions()
}

// 하드코딩된 카테고리 목록 (8개)
const categories = ref([
  {
    id: 12,
    name: '관광지',
    icon: 'pi pi-map-marker',
    image:
      'https://images.unsplash.com/photo-1469474968028-56623f02e42e?w=64&h=64&fit=crop&crop=center',
  },
  {
    id: 14,
    name: '문화시설',
    icon: 'pi pi-building',
    image:
      'https://images.unsplash.com/photo-1518998053901-5348d3961a04?w=64&h=64&fit=crop&crop=center',
  },
  {
    id: 15,
    name: '축제공연행사',
    icon: 'pi pi-calendar',
    image:
      'https://images.unsplash.com/photo-1533174072545-7a4b6ad7a6c3?w=64&h=64&fit=crop&crop=center',
  },
  {
    id: 25,
    name: '여행코스',
    icon: 'pi pi-coffee',
    image:
      'https://images.unsplash.com/photo-1501339847302-ac426a4a7cbb?w=64&h=64&fit=crop&crop=center',
  },
  {
    id: 28,
    name: '레저/스포츠',
    icon: 'pi pi-bolt',
    image:
      'https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=64&h=64&fit=crop&crop=center',
  },
  {
    id: 32,
    name: '숙박',
    icon: 'pi pi-home',
    image:
      'https://images.unsplash.com/photo-1566073771259-6a8506099945?w=64&h=64&fit=crop&crop=center',
  },
  {
    id: 38,
    name: '쇼핑',
    icon: 'pi pi-shopping-cart',
    image:
      'https://images.unsplash.com/photo-1441986300917-64674bd600d8?w=64&h=64&fit=crop&crop=center',
  },
  {
    id: 39,
    name: '음식점',
    icon: 'pi pi-utensils',
    image:
      'https://images.unsplash.com/photo-1414235077428-338989a2e8c0?w=64&h=64&fit=crop&crop=center',
  },
])
</script>

<style scoped>
/* 검색 버튼 호버 효과 */
.search-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .grid.grid-cols-1.md\:grid-cols-4 {
    grid-template-columns: 1fr;
    gap: 1rem;
  }
}
</style>
