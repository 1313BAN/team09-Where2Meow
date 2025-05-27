<template>
  <section class="relative py-12 px-4 overflow-hidden bg-[#fff6d5]">
    <!-- 배경 도형들 (PromoBanner와 유사한 스타일) -->
    <div
      class="absolute top-0 right-0 w-40 h-40 bg-[#ffc107] rounded-full opacity-50 transform translate-x-8 -translate-y-8"
    ></div>
    <div
      class="absolute bottom-0 right-32 w-28 h-28 bg-[#4caf50] rounded-full opacity-50 transform translate-y-6"
    ></div>
    <div
      class="absolute top-1/2 left-0 w-32 h-32 bg-[var(--primary-color)] rounded-full opacity-30 transform -translate-x-8"
    ></div>

    <div class="container mx-auto px-4 sm:px-6 lg:px-8 max-w-7xl relative z-10">
      <div class="flex flex-col lg:flex-row items-center justify-between">
        <!-- 좌측 콘텐츠 -->
        <div class="flex-1 max-w-2xl mb-8 lg:mb-0 lg:pr-8">
          <!-- 배지 -->
          <div class="flex items-center mb-4">
            <span
              class="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-[var(--primary-color)]/10 text-[var(--primary-color)] mr-3"
            >
              AI 여행 플래너
            </span>
          </div>

          <!-- 메인 제목 -->
          <h2 class="text-2xl lg:text-3xl font-bold mb-3 text-gray-900">
            지금 바로 <span class="text-[var(--primary-color)]">가냥이</span>와 함께<br />
            여행 계획을 시작해보세요!
          </h2>

          <!-- 설명 -->
          <p class="text-gray-600 mb-6 leading-relaxed">
            AI가 추천하는 맞춤형 여행 코스부터 사용자 커뮤니티까지,<br />
            당신만의 완벽한 여행을 만들어보세요.
          </p>

          <!-- 액션 버튼들 -->
          <div class="flex flex-col sm:flex-row gap-3">
            <button
              @click="handleStartPlanning"
              class="px-6 py-3 bg-[var(--primary-color)] text-white font-semibold rounded-xl shadow-lg hover:bg-[var(--secondary-color)] hover:shadow-xl transform hover:-translate-y-0.5 transition-all duration-200 flex items-center justify-center gap-2"
            >
              <i class="pi pi-plus-circle"></i>
              여행 계획 시작하기
            </button>

            <button
              @click="handleExplorePlans"
              class="px-6 py-3 bg-white text-gray-700 font-semibold rounded-xl border-2 border-gray-200 hover:border-[var(--primary-color)] hover:text-[var(--primary-color)] transition-all duration-200 flex items-center justify-center gap-2"
            >
              <i class="pi pi-compass"></i>
              인기 일정 둘러보기
            </button>
          </div>
        </div>

        <!-- 우측 통계 정보 (간결하게) -->
        <div class="flex-shrink-0">
          <div class="grid grid-cols-3 gap-6 text-center">
            <div class="bg-white/70 backdrop-blur-sm rounded-xl p-4 shadow-sm">
              <div class="text-xl font-bold text-[var(--primary-color)]">
                {{ formatNumber(stats.totalPlans) }}+
              </div>
              <div class="text-xs text-gray-600 mt-1">여행 계획</div>
            </div>
            <div class="bg-white/70 backdrop-blur-sm rounded-xl p-4 shadow-sm">
              <div class="text-xl font-bold text-[var(--primary-color)]">
                {{ formatNumber(stats.totalUsers) }}+
              </div>
              <div class="text-xs text-gray-600 mt-1">활성 사용자</div>
            </div>
            <div class="bg-white/70 backdrop-blur-sm rounded-xl p-4 shadow-sm">
              <div class="text-xl font-bold text-[var(--primary-color)]">
                {{ formatNumber(stats.totalReviews) }}+
              </div>
              <div class="text-xs text-gray-600 mt-1">여행 후기</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref } from 'vue'

// 통계 데이터 (간소화)
const stats = ref({
  totalPlans: 15420,
  totalUsers: 8230,
  totalReviews: 24680,
})

// Events
const emit = defineEmits(['start-planning', 'explore-plans'])

// 여행 계획 시작하기 클릭
const handleStartPlanning = () => {
  emit('start-planning')
  console.log('여행 계획 시작하기 클릭')
}

// 인기 일정 둘러보기 클릭
const handleExplorePlans = () => {
  emit('explore-plans')
  console.log('인기 일정 둘러보기 클릭')
}

// 숫자 포맷팅
const formatNumber = (num) => {
  return num.toLocaleString()
}
</script>
