<template>
  <div class="main-view">
    <!-- 히어로 섹션 -->
    <HeroSection @search="handleBannerSearch" />

    <!-- 검색 섹션 -->
    <SearchSection @search="handleDetailSearch" @category-click="handleCategoryClick" />

    <!-- 가냥이의 추천 일정 -->
    <RecommendationSection
      title="가냥이의 추천 일정"
      card-type="plan"
      background-color="bg-gray-50"
      section-icon="pi-sparkles"
      @item-click="handlePlanClick"
    />

    <!-- 현재 HOT한 게시글 -->
    <RecommendationSection
      title="현재 HOT한 게시글"
      card-type="board"
      background-color="bg-white"
      section-icon="pi-bolt"
      @item-click="handleBoardClick"
    />

    <!-- CTA 섹션 (여행 계획 시작 유도) -->
    <CTASection @start-planning="handleStartPlanning" @explore-plans="handleExplorePlans" />
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import HeroSection from '@/components/views/main/HeroSection/HeroSection.vue'
import SearchSection from '@/components/views/main/SearchSection/SearchSection.vue'
import RecommendationSection from '@/components/views/main/RecommendationSection/RecommendationSection.vue'
import CTASection from '@/components/views/main/CTASection/CTASection.vue'

const router = useRouter()

// 이벤트 핸들러들
const handleBannerSearch = (query) => {
  console.log('배너 검색:', query)
  // PlanView로 이동하면서 검색어를 query로 전달
  router.push({ name: 'plan-create', query: { message: query } })
}

const handleDetailSearch = (searchForm) => {
  console.log('상세 검색:', searchForm)
  // 상세 검색 결과 페이지로 이동
  router.push({ name: 'search', query: { ...searchForm, type: 'detail' } })
}

const handleCategoryClick = (category) => {
  console.log('카테고리 클릭:', category)
  // 카테고리별 목록 페이지로 이동
  router.push({
    name: 'category',
    params: { id: category.category?.id || category.selectedCategory },
  })
}

const handlePlanClick = (plan) => {
  console.log('여행 계획 클릭:', plan)
  // 여행 계획 상세 페이지로 이동
  router.push({ name: 'plan-detail', params: { id: plan.id || plan.planId } })
}

const handleBoardClick = (board) => {
  console.log('게시글 클릭:', board)
  // 게시글 상세 페이지로 이동
  router.push({ name: 'board-detail', params: { id: board.id || board.boardId } })
}

// CTA 섹션 이벤트 핸들러들
const handleStartPlanning = () => {
  console.log('여행 계획 시작하기')
  // 여행 계획 생성 페이지로 이동
  router.push({ name: 'plan-create' })
}

const handleExplorePlans = () => {
  console.log('인기 일정 둘러보기')
  // 인기 여행 계획 목록 페이지로 이동
  router.push({ name: 'plans', query: { sort: 'popular' } })
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
