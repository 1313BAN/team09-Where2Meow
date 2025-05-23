<template>
  <section class="py-12 px-4">
    <div class="container max-w-7xl mx-auto">
      <!-- 섹션 제목 -->
      <h2 class="text-2xl font-bold mb-8 text-gray-900">{{ title }}</h2>
      
      <!-- 인기 아이템 리스트 -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <div
          v-for="(item, index) in popularItems"
          :key="item.id"
          class="flex items-center p-4 bg-white rounded-lg shadow-sm hover:shadow-md transition-shadow cursor-pointer"
          @click="handleItemClick(item)"
        >
          <!-- 순위 번호 -->
          <span class="text-3xl font-bold text-gray-400 mr-4">
            {{ index + 1 }}
          </span>
          
          <!-- 컨텐츠 -->
          <div class="flex items-center flex-1">
            <!-- 이미지 -->
            <div class="w-20 h-14 rounded-lg overflow-hidden mr-4 flex-shrink-0">
              <img
                :src="item.image"
                :alt="item.title"
                class="w-full h-full object-cover"
              />
            </div>
            
            <!-- 정보 -->
            <div class="flex-1 min-w-0">
              <h3 class="text-sm font-bold text-gray-900 mb-1 line-clamp-2">
                {{ item.title }}
              </h3>
              <p class="text-xs text-gray-600">
                {{ item.description }}
              </p>
              
              <!-- 추가 정보 (평점, 리뷰 수 등) -->
              <div v-if="item.rating || item.reviewCount" class="flex items-center mt-2 text-xs text-gray-500">
                <div v-if="item.rating" class="flex items-center mr-3">
                  <i class="pi pi-star-fill text-yellow-400 mr-1"></i>
                  <span>{{ item.rating }}</span>
                </div>
                <div v-if="item.reviewCount">
                  <span>리뷰 {{ item.reviewCount }}개</span>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 화살표 아이콘 -->
          <i class="pi pi-chevron-right text-gray-400 ml-4"></i>
        </div>
      </div>
      
      <!-- 더보기 버튼 -->
      <div v-if="showMoreButton" class="text-center mt-8">
        <CommonButton
          label="더 많은 인기 여행지 보기"
          icon="pi pi-arrow-down"
          severity="secondary"
          outlined
          @click="handleLoadMore"
          :loading="isLoading"
        />
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref } from 'vue'
import CommonButton from '@/components/common/CommonButton.vue'

// Props
const props = defineProps({
  title: {
    type: String,
    default: '인기 음식점'
  },
  items: {
    type: Array,
    default: () => []
  },
  showMoreButton: {
    type: Boolean,
    default: true
  }
})

// 반응형 데이터
const isLoading = ref(false)

// 기본 인기 아이템 데이터 (props로 전달되지 않은 경우)
const defaultItems = [
  {
    id: 1,
    title: '2023년 가을, 제주도 - 성수기 피해',
    description: '제주 4박5일',
    image: 'https://images.unsplash.com/photo-1561453413-4a0e2b3e87bd?w=80&h=56&fit=crop',
    rating: 4.8,
    reviewCount: 245
  },
  {
    id: 2,
    title: '부산 기차여행 - 해운대 호텔',
    description: '부산 2박3일',
    image: 'https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=80&h=56&fit=crop',
    rating: 4.6,
    reviewCount: 189
  },
  {
    id: 3,
    title: 'KTX타고 경북여행 - 안동 - 경주',
    description: '경북 3박4일',
    image: 'https://images.unsplash.com/photo-1578662996442-48f60103fc96?w=80&h=56&fit=crop',
    rating: 4.7,
    reviewCount: 156
  },
  {
    id: 4,
    title: '일본 오사카 - 나라 - 교토 - 고베 7일',
    description: '일본 7박8일',
    image: 'https://images.unsplash.com/photo-1493976040374-85c8e12f0c0e?w=80&h=56&fit=crop',
    rating: 4.9,
    reviewCount: 312
  },
  {
    id: 5,
    title: '유럽항공권+지중해 크루즈 10박11일',
    description: '유럽 10박11일',
    image: 'https://images.unsplash.com/photo-1467269204594-9661b134dd2b?w=80&h=56&fit=crop',
    rating: 4.8,
    reviewCount: 278
  },
  {
    id: 6,
    title: '단풍여행 - 설악산/내장산',
    description: '단풍 2박3일',
    image: 'https://images.unsplash.com/photo-1441974231531-c6227db76b6e?w=80&h=56&fit=crop',
    rating: 4.5,
    reviewCount: 134
  }
]

// 표시할 아이템 (props 우선, 없으면 기본값)
const popularItems = props.items.length > 0 ? props.items : defaultItems

// Events
const emit = defineEmits(['item-click', 'load-more'])

// 아이템 클릭 핸들러
const handleItemClick = (item) => {
  emit('item-click', item)
  console.log('선택된 아이템:', item)
}

// 더보기 핸들러
const handleLoadMore = async () => {
  isLoading.value = true
  
  try {
    emit('load-more')
    // 추가 데이터 로딩 로직
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
/* 텍스트 클램프 */
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 호버 효과 */
.group:hover {
  transform: translateY(-2px);
  transition: transform 0.2s ease;
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .flex {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .text-3xl {
    font-size: 1.5rem;
    margin-bottom: 0.5rem;
  }
  
  .w-20 {
    width: 100%;
    height: 120px;
    margin-bottom: 0.75rem;
  }
}
</style>