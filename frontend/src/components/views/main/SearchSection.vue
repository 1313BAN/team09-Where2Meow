<template>
  <section class="bg-white py-8 px-4">
    <div class="container max-w-7xl mx-auto">
      <!-- 검색 제목 -->
      <h2 class="text-2xl font-bold mb-6 text-gray-900">{{ title }}</h2>
      
      <!-- 검색 폼 -->
      <form @submit.prevent="handleSearch" class="grid grid-cols-1 md:grid-cols-4 gap-4 mb-8">
        <InputText
          v-model="searchForm.country"
          placeholder="나라"
          class="w-full"
        />
        <InputText
          v-model="searchForm.city"
          placeholder="도시"
          class="w-full"
        />
        <InputText
          v-model="searchForm.region"
          placeholder="지역"
          class="w-full"
        />
        <CommonButton
          label="검색하기"
          icon="pi pi-search"
          severity="primary"
          :loading="isSearching"
          @click="handleSearch"
          custom-class="w-full"
        />
      </form>

      <!-- 카테고리 아이콘 -->
      <div class="grid grid-cols-3 md:grid-cols-9 gap-4 justify-items-center">
        <div
          v-for="category in categories"
          :key="category.id"
          class="flex flex-col items-center cursor-pointer group"
          @click="handleCategoryClick(category)"
        >
          <div class="w-16 h-16 rounded-full bg-gray-100 flex items-center justify-center mb-2 overflow-hidden group-hover:bg-gray-200 transition-colors">
            <img
              v-if="category.image"
              :src="category.image"
              :alt="category.name"
              class="w-full h-full object-cover"
            />
            <i
              v-else
              :class="category.icon"
              class="text-2xl text-gray-600"
            ></i>
          </div>
          <span class="text-xs text-center text-gray-700 group-hover:text-gray-900">
            {{ category.name }}
          </span>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, reactive } from 'vue'
import InputText from 'primevue/inputtext'
import CommonButton from '@/components/common/CommonButton.vue'

// Props
const props = defineProps({
  title: {
    type: String,
    default: '여행 정보 검색'
  }
})

// 반응형 데이터
const isSearching = ref(false)
const searchForm = reactive({
  country: '',
  city: '',
  region: ''
})

// 카테고리 데이터
const categories = ref([
  {
    id: 1,
    name: '숙박',
    icon: 'pi pi-home',
    image: 'https://images.unsplash.com/photo-1566073771259-6a8506099945?w=60&h=60&fit=crop'
  },
  {
    id: 2,
    name: '관광지',
    icon: 'pi pi-map-marker',
    image: 'https://images.unsplash.com/photo-1539650116574-75c0c6d0c39?w=60&h=60&fit=crop'
  },
  {
    id: 3,
    name: '음식점',
    icon: 'pi pi-shopping-bag',
    image: 'https://images.unsplash.com/photo-1414235077428-338989a2e8c0?w=60&h=60&fit=crop'
  },
  {
    id: 4,
    name: '카페',
    icon: 'pi pi-coffee',
    image: 'https://images.unsplash.com/photo-1501339847302-ac426a4a7cbb?w=60&h=60&fit=crop'
  },
  {
    id: 5,
    name: '문화시설',
    icon: 'pi pi-building',
    image: 'https://images.unsplash.com/photo-1518998053901-5348d3961a04?w=60&h=60&fit=crop'
  },
  {
    id: 6,
    name: '축제',
    icon: 'pi pi-calendar',
    image: 'https://images.unsplash.com/photo-1533174072545-7a4b6ad7a6c3?w=60&h=60&fit=crop'
  },
  {
    id: 7,
    name: '산/계곡',
    icon: 'pi pi-image',
    image: 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=60&h=60&fit=crop'
  },
  {
    id: 8,
    name: '바다/해변',
    icon: 'pi pi-sun',
    image: 'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=60&h=60&fit=crop'
  },
  {
    id: 9,
    name: '쇼핑',
    icon: 'pi pi-shopping-cart',
    image: 'https://images.unsplash.com/photo-1441986300917-64674bd600d8?w=60&h=60&fit=crop'
  }
])

// Events
const emit = defineEmits(['search', 'category-click'])

// 검색 핸들러
const handleSearch = async () => {
  const hasSearchTerm = searchForm.country || searchForm.city || searchForm.region
  if (!hasSearchTerm) return

  isSearching.value = true

  try {
    emit('search', { ...searchForm })
    console.log('검색 조건:', searchForm)
  } finally {
    isSearching.value = false
  }
}

// 카테고리 클릭 핸들러
const handleCategoryClick = (category) => {
  emit('category-click', category)
  console.log('선택된 카테고리:', category)
}
</script>

<style scoped>
/* 카테고리 아이템 호버 효과 */
.group:hover .w-16 {
  transform: scale(1.05);
  transition: transform 0.2s ease;
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .grid-cols-9 {
    grid-template-columns: repeat(3, 1fr);
  }
}
</style>