<template>
  <section class="py-12 px-4" :class="[backgroundColor, 'relative overflow-hidden']">
    <!-- 고양이 이미지 배경 -->
    <div v-if="showCatSilhouette" class="absolute -right-20 -bottom-10 opacity-5 pointer-events-none z-0">
      <svg width="250" height="150" viewBox="0 0 250 150" fill="currentColor" xmlns="http://www.w3.org/2000/svg">
        <path d="M100,40 C85,30 65,30 50,40 C45,25 30,15 15,20 C0,25 -5,45 5,60 C0,70 0,85 10,95 C25,110 50,110 65,95 C80,110 105,110 120,95 C130,110 155,110 170,95 C185,110 210,110 225,95 C240,85 245,65 235,50 C245,35 240,15 225,10 C210,5 190,15 185,30 C175,20 155,15 140,20 C125,25 115,35 115,50 C110,45 105,40 100,40 Z M50,60 C45,60 40,55 40,50 C40,45 45,40 50,40 C55,40 60,45 60,50 C60,55 55,60 50,60 Z M190,60 C185,60 180,55 180,50 C180,45 185,40 190,40 C195,40 200,45 200,50 C200,55 195,60 190,60 Z"></path>
      </svg>
    </div>

    <div class="container max-w-7xl mx-auto relative z-10">
      <!-- 섹션 제목 -->
      <div class="flex items-center justify-between mb-8">
        <h2 class="text-2xl font-bold text-gray-900 flex items-center gap-2">
          <i :class="['pi', sectionIcon, 'text-[var(--primary-color)]']"></i>
          <span>{{ title }}</span>
          <span v-if="items.length" class="text-sm text-gray-500 font-normal ml-2">({{ items.length }})</span>
        </h2>
        
        <!-- 더보기 버튼 -->
        <button v-if="showViewMore" @click="$emit('view-more')" class="text-sm text-[var(--primary-color)] flex items-center gap-1 hover:underline paw-cursor">
          <span>더보기</span>
          <i class="pi pi-arrow-right"></i>
        </button>
      </div>
      
      <!-- 추천 아이템 캐러셀 -->
      <div class="relative">
        <Carousel 
          :value="recommendationItems" 
          :numVisible="numVisible"
          :numScroll="1" 
          :responsiveOptions="responsiveOptions"
          :showNavigators="showNavigators"
          :showIndicators="showIndicators"
          :circular="circular"
          :autoplayInterval="autoplayInterval"
          class="cat-carousel"
        >
          <template #item="slotProps">
            <CommonCard
              :image="slotProps.data.image"
              :image-alt="slotProps.data.title"
              :clickable="true"
              card-class="mx-2 h-full"
              image-class="h-48 sm:h-52 md:h-56"
              @click="handleItemClick(slotProps.data)"
            >
              <template #overlay>
                <div class="p-4 text-white">
                  <h3 class="text-lg font-bold mb-2">{{ slotProps.data.title }}</h3>
                  <p v-if="slotProps.data.description" class="text-sm opacity-90">
                    {{ slotProps.data.description }}
                  </p>
                  
                  <!-- 추가 정보 (지역, 평점 등) -->
                  <div v-if="slotProps.data.location || slotProps.data.rating" class="mt-3 flex items-center justify-between text-xs">
                    <span v-if="slotProps.data.location" class="opacity-80">
                      <i class="pi pi-map-marker mr-1"></i>
                      {{ slotProps.data.location }}
                    </span>
                    <div v-if="slotProps.data.rating" class="flex items-center">
                      <i class="pi pi-star-fill text-[var(--accent-color)] mr-1"></i>
                      <span>{{ slotProps.data.rating }}</span>
                    </div>
                  </div>
                </div>
              </template>
            </CommonCard>
          </template>
        </Carousel>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, computed } from 'vue'
import Carousel from 'primevue/carousel'
import CommonCard from '@/components/common/CommonCard.vue'

// Props
const props = defineProps({
  title: {
    type: String,
    default: '인기 여행지 정보'
  },
  items: {
    type: Array,
    default: () => []
  },
  backgroundColor: {
    type: String,
    default: 'bg-[var(--light-color)]/20'
  },
  numVisible: {
    type: Number,
    default: 4
  },
  showNavigators: {
    type: Boolean,
    default: true
  },
  showIndicators: {
    type: Boolean,
    default: false
  },
  circular: {
    type: Boolean,
    default: true
  },
  autoplayInterval: {
    type: Number,
    default: 0 // 0이면 자동재생 안함
  },
  showCatSilhouette: {
    type: Boolean,
    default: true
  },
  showViewMore: {
    type: Boolean,
    default: false
  },
  sectionIcon: {
    type: String,
    default: 'pi-map'
  }
})

// 기본 추천 아이템 데이터
const defaultItems = [
  {
    id: 1,
    title: '종로 북촌 한옥마을',
    description: '전통과 현대가 어우러진 아름다운 한옥마을',
    image: 'https://images.unsplash.com/photo-1578662996442-48f60103fc96?w=300&h=200&fit=crop',
    location: '서울 종로구',
    rating: 4.7
  },
  {
    id: 2,
    title: '서울 명동거리와 쇼핑',
    description: '쇼핑과 맛집이 가득한 서울의 중심가',
    image: 'https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=300&h=200&fit=crop',
    location: '서울 중구',
    rating: 4.5
  },
  {
    id: 3,
    title: '푸른 바다와 제주 올레길 둘러보기',
    description: '제주도의 아름다운 자연을 만끽할 수 있는 올레길',
    image: 'https://images.unsplash.com/photo-1561453413-4a0e2b3e87bd?w=300&h=200&fit=crop',
    location: '제주도',
    rating: 4.9
  },
  {
    id: 4,
    title: '힐링 숲속 온천 스파',
    description: '자연 속에서 즐기는 온천과 힐링',
    image: 'https://images.unsplash.com/photo-1544161515-4ab6ce6db874?w=300&h=200&fit=crop',
    location: '강원도',
    rating: 4.6
  },
  {
    id: 5,
    title: '부산 해운대 해변',
    description: '아름다운 해변과 다양한 액티비티',
    image: 'https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=300&h=200&fit=crop',
    location: '부산 해운대',
    rating: 4.8
  },
  {
    id: 6,
    title: '경주 불국사',
    description: '천년 고도 경주의 대표적인 문화유산',
    image: 'https://images.unsplash.com/photo-1578662996442-48f60103fc96?w=300&h=200&fit=crop',
    location: '경북 경주',
    rating: 4.7
  }
]

// 표시할 아이템 (props 우선, 없으면 기본값)
const recommendationItems = computed(() => 
  props.items.length > 0 ? props.items : defaultItems
)

// 반응형 옵션
const responsiveOptions = ref([
  {
    breakpoint: '1024px',
    numVisible: 3,
    numScroll: 1
  },
  {
    breakpoint: '768px',
    numVisible: 2,
    numScroll: 1
  },
  {
    breakpoint: '560px',
    numVisible: 1,
    numScroll: 1
  }
])

// Events
const emit = defineEmits(['item-click', 'view-more'])

// 아이템 클릭 핸들러
const handleItemClick = (item) => {
  emit('item-click', item)
  console.log('선택된 추천 아이템:', item)
}
</script>

<style scoped>
/* 캐러셀 커스터마이징 */
:deep(.p-carousel .p-carousel-content .p-carousel-container .p-carousel-items-content .p-carousel-items-container) {
  display: flex;
  align-items: stretch;
}

:deep(.p-carousel .p-carousel-item) {
  display: flex;
}

/* 네비게이션 버튼 스타일 */
:deep(.p-carousel .p-carousel-prev),
:deep(.p-carousel .p-carousel-next) {
  background: rgba(255, 255, 255, 0.9);
  border: none;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  color: var(--primary-color) !important;
}

:deep(.p-carousel .p-carousel-prev:hover),
:deep(.p-carousel .p-carousel-next:hover) {
  background: white;
  transform: scale(1.1);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.15);
  color: var(--secondary-color) !important;
}

/* 고양이 발바닥 커서 효과 */
.cat-carousel:deep(.p-carousel-prev .p-icon),
.cat-carousel:deep(.p-carousel-next .p-icon) {
  transform: scale(1.2);
}

.cat-carousel:deep(.p-carousel .p-carousel-indicators .p-carousel-indicator.p-highlight button) {
  background-color: var(--primary-color);
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  :deep(.p-carousel .p-carousel-prev),
  :deep(.p-carousel .p-carousel-next) {
    width: 35px;
    height: 35px;
  }
}
</style>