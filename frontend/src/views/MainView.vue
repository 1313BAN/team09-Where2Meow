<template>
  <div class="main-view">
    <!-- 메인 배너 -->
    <MainBanner @search="handleBannerSearch" />

    <!-- 검색 섹션 -->
    <SearchSection @search="handleDetailSearch" @category-click="handleCategoryClick" />

    <!-- 인기 음식점 -->
    <PopularSection
      title="인기 음식점"
      :items="popularRestaurants"
      @item-click="handlePopularItemClick"
      @load-more="handleLoadMorePopular"
    />

    <!-- 인기 여행지 정보 -->
    <RecommendationSection
      title="인기 여행지 정보"
      backgroundColor="bg-[var(--light-color)]/20"
      :items="recommendationPlaces"
      sectionIcon="pi-compass"
      :showViewMore="true"
      @item-click="handleRecommendationClick"
      @view-more="handleViewMoreRecommendations"
    />

    <!-- 유저가 뽑은 지금 핫한 여행지 -->
    <RecommendationSection
      title="유저가 뽑은 지금 핫한 여행지"
      background-color="bg-white"
      :items="hotPlaces"
      @item-click="handleHotPlaceClick"
    />

    <!-- 가냥이의 추천 일정 -->
    <RecommendationSection
      title="가냥이의 추천 일정"
      background-color="bg-gray-50"
      :items="aiRecommendations"
      @item-click="handleAiRecommendationClick"
    />

    <!-- 트래블러 초이스 배너 -->
    <PromoBanner @button-click="handlePromoClick" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import MainBanner from '@/components/views/main/MainBanner.vue'
import SearchSection from '@/components/views/main/SearchSection.vue'
import PopularSection from '@/components/views/main/PopularSection.vue'
import RecommendationSection from '@/components/views/main/RecommendationSection.vue'
import PromoBanner from '@/components/views/main/PromoBanner.vue'

const router = useRouter()

// 반응형 데이터
const popularRestaurants = ref([])
const recommendationPlaces = ref([])
const hotPlaces = ref([])
const aiRecommendations = ref([])

// 인기 음식점 데이터
const defaultPopularRestaurants = [
  {
    id: 1,
    title: '제주도 흑돼지 맛집 - 돈사돈',
    description: '제주 4박5일',
    image: 'https://images.unsplash.com/photo-1414235077428-338989a2e8c0?w=80&h=56&fit=crop',
    rating: 4.8,
    reviewCount: 245,
  },
  {
    id: 2,
    title: '부산 자갈치 시장 회센터',
    description: '부산 2박3일',
    image: 'https://images.unsplash.com/photo-1559339352-11d035aa65de?w=80&h=56&fit=crop',
    rating: 4.6,
    reviewCount: 189,
  },
  {
    id: 3,
    title: '서울 강남 한정식 맛집',
    description: '서울 1박2일',
    image: 'https://images.unsplash.com/photo-1579027989536-b7b1f875659b?w=80&h=56&fit=crop',
    rating: 4.7,
    reviewCount: 156,
  },
  {
    id: 4,
    title: '오사카 타코야키 거리',
    description: '일본 7박8일',
    image: 'https://images.unsplash.com/photo-1582878826629-29b7ad1cdc43?w=80&h=56&fit=crop',
    rating: 4.9,
    reviewCount: 312,
  },
  {
    id: 5,
    title: '파리 미슐랭 레스토랑',
    description: '유럽 10박11일',
    image: 'https://images.unsplash.com/photo-1544124499-58912cbddaad?w=80&h=56&fit=crop',
    rating: 4.8,
    reviewCount: 278,
  },
  {
    id: 6,
    title: '강원도 산채정식 맛집',
    description: '강원도 2박3일',
    image: 'https://images.unsplash.com/photo-1512058564366-18510be2db19?w=80&h=56&fit=crop',
    rating: 4.5,
    reviewCount: 134,
  },
]

// 추천 여행지 데이터
const defaultRecommendationPlaces = [
  {
    id: 1,
    title: '종로 북촌 한옥마을',
    description: '전통과 현대가 어우러진 아름다운 한옥마을',
    image: 'https://images.unsplash.com/photo-1578662996442-48f60103fc96?w=300&h=200&fit=crop',
    location: '서울 종로구',
    rating: 4.7,
  },
  {
    id: 2,
    title: '서울 명동거리와 쇼핑',
    description: '쇼핑과 맛집이 가득한 서울의 중심가',
    image: 'https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=300&h=200&fit=crop',
    location: '서울 중구',
    rating: 4.5,
  },
  {
    id: 3,
    title: '푸른 바다와 제주 올레길',
    description: '제주도의 아름다운 자연을 만끽할 수 있는 올레길',
    image: 'https://images.unsplash.com/photo-1561453413-4a0e2b3e87bd?w=300&h=200&fit=crop',
    location: '제주도',
    rating: 4.9,
  },
  {
    id: 4,
    title: '힐링 숲속 온천 스파',
    description: '자연 속에서 즐기는 온천과 힐링',
    image: 'https://images.unsplash.com/photo-1544161515-4ab6ce6db874?w=300&h=200&fit=crop',
    location: '강원도',
    rating: 4.6,
  },
]

// 핫한 여행지 데이터
const defaultHotPlaces = [
  {
    id: 1,
    title: '로마, 이탈리아',
    description: '역사와 문화가 살아 숨쉬는 영원한 도시',
    image: 'https://images.unsplash.com/photo-1552832230-c0197dd311b5?w=300&h=200&fit=crop',
    location: '이탈리아',
    rating: 4.8,
  },
  {
    id: 2,
    title: '파리, 프랑스',
    description: '낭만과 예술의 도시',
    image: 'https://images.unsplash.com/photo-1502602898536-47ad22581b52?w=300&h=200&fit=crop',
    location: '프랑스',
    rating: 4.7,
  },
  {
    id: 3,
    title: '라스베가스, 미국',
    description: '24시간 잠들지 않는 엔터테인먼트의 도시',
    image: 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=300&h=200&fit=crop',
    location: '미국',
    rating: 4.6,
  },
  {
    id: 4,
    title: '레이캬비크, 아이슬란드',
    description: '신비로운 북극광과 자연의 경이로움',
    image: 'https://images.unsplash.com/photo-1539650116574-75c0c6d0c39?w=300&h=200&fit=crop',
    location: '아이슬란드',
    rating: 4.9,
  },
]

// AI 추천 일정 데이터
const defaultAiRecommendations = [
  {
    id: 1,
    title: '서울, 대한민국',
    description: '가냥이 추천 서울 3박4일 완벽 코스',
    image: 'https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=300&h=200&fit=crop',
    location: '대한민국',
    rating: 4.8,
  },
  {
    id: 2,
    title: '베이징, 중국',
    description: '역사와 현대가 공존하는 베이징 여행',
    image: 'https://images.unsplash.com/photo-1508804185872-d7badad00f7d?w=300&h=200&fit=crop',
    location: '중국',
    rating: 4.6,
  },
  {
    id: 3,
    title: '오사카, 일본',
    description: '맛의 도시 오사카 미식 여행',
    image: 'https://images.unsplash.com/photo-1493976040374-85c8e12f0c0e?w=300&h=200&fit=crop',
    location: '일본',
    rating: 4.7,
  },
  {
    id: 4,
    title: '타이페이, 대만',
    description: '야시장과 문화가 어우러진 타이페이',
    image: 'https://images.unsplash.com/photo-1560492284-15ac5ed8c1dd?w=300&h=200&fit=crop',
    location: '대만',
    rating: 4.5,
  },
]

// 컴포넌트 마운트 시 데이터 로드
onMounted(() => {
  loadInitialData()
})

// 초기 데이터 로드
const loadInitialData = () => {
  popularRestaurants.value = defaultPopularRestaurants
  recommendationPlaces.value = defaultRecommendationPlaces
  hotPlaces.value = defaultHotPlaces
  aiRecommendations.value = defaultAiRecommendations
}

// 이벤트 핸들러들
const handleBannerSearch = (query) => {
  console.log('배너 검색:', query)
  // AI 검색 페이지로 이동하거나 검색 결과 표시
  router.push({ name: 'search', query: { q: query, type: 'ai' } })
}

const handleDetailSearch = (searchForm) => {
  console.log('상세 검색:', searchForm)
  // 상세 검색 결과 페이지로 이동
  router.push({ name: 'search', query: { ...searchForm, type: 'detail' } })
}

const handleCategoryClick = (category) => {
  console.log('카테고리 클릭:', category)
  // 카테고리별 목록 페이지로 이동
  router.push({ name: 'category', params: { id: category.id } })
}

const handlePopularItemClick = (item) => {
  console.log('인기 아이템 클릭:', item)
  // 상세 페이지로 이동
  router.push({ name: 'place-detail', params: { id: item.id } })
}

const handleLoadMorePopular = () => {
  console.log('더보기 클릭')
  // 더 많은 인기 아이템 로드
}

const handleRecommendationClick = (item) => {
  console.log('추천 여행지 클릭:', item)
  router.push({ name: 'place-detail', params: { id: item.id } })
}

const handleHotPlaceClick = (item) => {
  console.log('핫한 여행지 클릭:', item)
  router.push({ name: 'place-detail', params: { id: item.id } })
}

const handleAiRecommendationClick = (item) => {
  console.log('AI 추천 일정 클릭:', item)
  router.push({ name: 'itinerary-detail', params: { id: item.id } })
}

const handlePromoClick = () => {
  console.log('프로모 배너 클릭')
  router.push({ name: 'awards' })
}
</script>

<style scoped>
.main-view {
  min-height: 100vh;
}

/* 섹션 간 간격 조정 */
.main-view > * + * {
  margin-top: 0;
}

/* 스크롤 시 부드러운 효과 */
.main-view {
  scroll-behavior: smooth;
}
</style>
