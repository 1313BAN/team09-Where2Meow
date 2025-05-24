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
      <div class="mb-6">
        <h3 class="text-lg font-semibold mb-4 text-gray-900">카테고리</h3>
        <div class="grid grid-cols-3 md:grid-cols-9 gap-4 justify-items-center">
          <div
            v-for="category in categories"
            :key="category.id"
            class="flex flex-col items-center cursor-pointer group"
            :class="{ 'selected-category': selectedCategories.includes(category.id) }"
            @click="handleCategoryClick(category)"
          >
            <div
              class="w-16 h-16 rounded-full bg-gray-100 flex items-center justify-center mb-2 overflow-hidden group-hover:bg-gray-200 transition-all duration-200"
              :class="{
                'bg-blue-100 border-2 border-blue-500': selectedCategories.includes(category.id),
              }"
            >
              <img
                v-if="category.image"
                :src="category.image"
                :alt="category.name"
                class="w-full h-full object-cover"
              />
              <i
                v-else
                :class="[
                  category.icon,
                  'text-2xl',
                  selectedCategories.includes(category.id) ? 'text-blue-600' : 'text-gray-600',
                ]"
              ></i>
            </div>
            <span
              class="text-xs text-center group-hover:text-gray-900 transition-colors"
              :class="
                selectedCategories.includes(category.id)
                  ? 'text-blue-700 font-semibold'
                  : 'text-gray-700'
              "
            >
              {{ category.name }}
            </span>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import Button from 'primevue/button'
import Select from 'primevue/select'
import addressAPI from '@/api/address'

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

// 선택된 카테고리들
const selectedCategories = ref([])

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

const handleSearch = async () => {
  isSearching.value = true
  try {
    const searchData = {
      ...searchForm,
      categories: selectedCategories.value.length > 0 ? selectedCategories.value : null,
    }
    emit('search', searchData)
    console.log('검색 조건:', searchData)
  } finally {
    isSearching.value = false
  }
}

// 하드코딩된 카테고리 목록
const categories = ref([
  {
    id: 1,
    name: '숙박',
    icon: 'pi pi-home',
    image: 'https://images.unsplash.com/photo-1566073771259-6a8506099945?w=60&h=60&fit=crop',
  },
  {
    id: 2,
    name: '관광지',
    icon: 'pi pi-map-marker',
    image: 'https://images.unsplash.com/photo-1539650116574-75c0c6d0c39?w=60&h=60&fit=crop',
  },
  {
    id: 3,
    name: '음식점',
    icon: 'pi pi-shopping-bag',
    image: 'https://images.unsplash.com/photo-1414235077428-338989a2e8c0?w=60&h=60&fit=crop',
  },
  {
    id: 4,
    name: '카페',
    icon: 'pi pi-coffee',
    image: 'https://images.unsplash.com/photo-1501339847302-ac426a4a7cbb?w=60&h=60&fit=crop',
  },
  {
    id: 5,
    name: '문화시설',
    icon: 'pi pi-building',
    image: 'https://images.unsplash.com/photo-1518998053901-5348d3961a04?w=60&h=60&fit=crop',
  },
  {
    id: 6,
    name: '축제',
    icon: 'pi pi-calendar',
    image: 'https://images.unsplash.com/photo-1533174072545-7a4b6ad7a6c3?w=60&h=60&fit=crop',
  },
  {
    id: 7,
    name: '산/계곡',
    icon: 'pi pi-image',
    image: 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=60&h=60&fit=crop',
  },
  {
    id: 8,
    name: '바다/해변',
    icon: 'pi pi-sun',
    image: 'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=60&h=60&fit=crop',
  },
  {
    id: 9,
    name: '쇼핑',
    icon: 'pi pi-shopping-cart',
    image: 'https://images.unsplash.com/photo-1441986300917-64674bd600d8?w=60&h=60&fit=crop',
  },
])

const handleCategoryClick = (category) => {
  const index = selectedCategories.value.indexOf(category.id)
  if (index > -1) {
    // 이미 선택된 카테고리면 제거
    selectedCategories.value.splice(index, 1)
  } else {
    // 선택되지 않은 카테고리면 추가
    selectedCategories.value.push(category.id)
  }

  emit('category-click', {
    category,
    selectedCategories: selectedCategories.value,
  })
  console.log('선택된 카테고리:', category, '전체 선택된 카테고리:', selectedCategories.value)
}
</script>
