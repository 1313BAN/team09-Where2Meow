<template>
  <div class="flex">
    <!-- 왼쪽 사이드바 -->
    <aside class="w-64 bg-white border-r border-gray-200 flex-shrink-0">
      <div class="p-6">
        <!-- 사이드바 메뉴 -->
        <nav class="space-y-2">
          <button
            @click="switchTab('myPlans')"
            :class="[
              'w-full flex items-center px-4 py-3 text-left rounded-lg transition-colors text-sm font-medium',
              activeTab === 'myPlans' 
                ? 'bg-[#00EDB3] text-white' 
                : 'text-gray-700 hover:bg-gray-100'
            ]"
          >
            <svg class="w-5 h-5 mr-3" fill="currentColor" viewBox="0 0 20 20">
              <path d="M9 2a1 1 0 000 2h2a1 1 0 100-2H9z"/>
              <path fill-rule="evenodd" d="M4 5a2 2 0 012-2v1a1 1 0 001 1h6a1 1 0 001-1V3a2 2 0 012 2v6.5a1.5 1.5 0 01-1.5 1.5h-7A1.5 1.5 0 014 11.5V5z" clip-rule="evenodd"/>
            </svg>
            내가 만든 일정
          </button>

          <button
            @click="switchTab('bookmarks')"
            :class="[
              'w-full flex items-center px-4 py-3 text-left rounded-lg transition-colors text-sm font-medium',
              activeTab === 'bookmarks' 
                ? 'bg-[#00EDB3] text-white' 
                : 'text-gray-700 hover:bg-gray-100'
            ]"
          >
            <svg class="w-5 h-5 mr-3" fill="currentColor" viewBox="0 0 20 20">
              <path d="M5 4a2 2 0 012-2h6a2 2 0 012 2v14l-5-2.5L5 18V4z"/>
            </svg>
            북마크
          </button>
        </nav>
      </div>
    </aside>

    <!-- 오른쪽 컨텐츠 영역 -->
    <main class="flex-1 p-6 bg-white">
      <!-- 페이지 제목 -->
      <div class="mb-6">
        <h1 class="text-2xl font-bold text-gray-900">{{ getPageTitle() }}</h1>
      </div>

      <!-- 로딩 상태 -->
      <div v-if="loading" class="flex flex-col items-center justify-center py-20 gap-4">
        <div class="w-8 h-8 border-4 border-gray-200 border-t-[#00EDB3] rounded-full animate-spin"></div>
        <span class="text-gray-600">데이터를 불러오는 중...</span>
      </div>

      <!-- 에러 상태 -->
      <div v-else-if="error" class="flex flex-col items-center justify-center py-20 gap-4 text-red-500">
        <span>{{ error }}</span>
        <button 
          @click="fetchData" 
          class="px-4 py-2 bg-[#00EDB3] text-white rounded-lg hover:bg-[#00c49a] transition-colors"
        >
          다시 시도
        </button>
      </div>

      <!-- 빈 상태 (북마크가 없을 때) -->
      <div v-else-if="activeTab === 'bookmarks' && plans.length === 0" class="text-center py-20">
        <div class="text-6xl mb-4">📌</div>
        <h3 class="text-xl font-semibold text-gray-900 mb-2">북마크된 여행 계획이 없습니다</h3>
        <p class="text-gray-600 mb-6">마음에 드는 여행 계획을 북마크해보세요</p>
        <button 
          @click="switchTab('myPlans')"
          class="px-6 py-3 bg-[#00EDB3] text-white rounded-lg hover:bg-[#00c49a] transition-colors"
        >
          내 일정 보기
        </button>
      </div>

      <!-- 여행 계획 그리드 -->
      <div v-else class="grid gap-6" style="grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));">
        <TravelPlanCard
          v-for="plan in plans"
          :key="plan.planId"
          :plan="plan"
          :show-delete-button="activeTab === 'myPlans'"
          @click="openPlan"
          @update="updatePlan"
          @delete="deletePlan"
        />
        
        <!-- 내가 만든 일정 탭에서만 추가 카드 표시 -->
        <AddPlanCard v-if="activeTab === 'myPlans'" @click="addNewPlan" />
      </div>
    </main>
  </div>
</template>

<script>
import { planService } from '@/api/plan'
import { attractionApi } from '@/api/attraction'
import TravelPlanCard from '@/components/views/plan/PlanCard.vue'
import AddPlanCard from '@/components/views/plan/AddPlanCard.vue'

export default {
  name: 'PlanDashboardView',
  components: {
    TravelPlanCard,
    AddPlanCard
  },
  data() {
    return {
      activeTab: 'myPlans', // 'myPlans' 또는 'bookmarks'
      plans: [], // 현재 표시되는 계획들
      loading: false,
      error: null
    }
  },
  mounted() {
    this.fetchData();
  },
  methods: {
    // 탭 전환
    switchTab(tab) {
      if (this.activeTab !== tab) {
        this.activeTab = tab;
        this.fetchData();
      }
    },
    
    // 페이지 제목 가져오기
    getPageTitle() {
      return this.activeTab === 'myPlans' ? '내가 만든 일정' : '북마크';
    },
    
    // 데이터 가져오기 (탭에 따라 다른 데이터)
    async fetchData() {
      this.loading = true;
      this.error = null;
      
      try {
        if (this.activeTab === 'myPlans') {
          // 내가 만든 일정 가져오기
          const response = await planService.getUserPlans();
          this.plans = response.data;
        } else if (this.activeTab === 'bookmarks') {
          // 북마크된 일정 가져오기
          const response = await planService.getBookmarkedPlans();
          this.plans = response.data;
        }
      } catch (error) {
        console.error('데이터를 불러오는데 실패했습니다:', error);
        
        if (error.response?.status === 401) {
          this.error = '로그인이 필요합니다.';
        } else if (error.response?.status === 403) {
          this.error = '접근 권한이 없습니다.';
        } else if (error.response?.status >= 500) {
          this.error = '서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.';
        } else {
          this.error = '데이터를 불러오는데 실패했습니다. 다시 시도해주세요.';
        }
      } finally {
        this.loading = false;
      }
    },
    
    // 여행 계획 열기
    openPlan(plan) {
      console.log('Opening plan:', plan);
      
      if (this.activeTab === 'myPlans') {
        // 내가 만든 일정은 바로 편집 모드로 이동
        this.$router.push(`/plan/edit/${plan.planId}`);
      } else if (this.activeTab === 'bookmarks') {
        // 북마크한 일정은 복사하여 새로운 계획으로 편집
        this.copyAndEditPlan(plan);
      }
    },
    
    // 북마크한 계획을 복사하여 편집
    async copyAndEditPlan(plan) {
      try {
        this.loading = true;
        
        // 계획 상세 정보 가져오기
        const detailResponse = await planService.getPlanDetail(plan.planId);
        const planDetail = detailResponse.data;
        
        // 계획 상세 정보에서 관광지 상세 데이터 가져오기
        const attractionsWithDetails = [];
        
        if (planDetail.attractions && planDetail.attractions.length > 0) {
          for (const attraction of planDetail.attractions) {
            try {
              // 각 관광지에 대해 상세 정보 가져오기
              const attractionDetailResponse = await attractionApi.getAttractionDetail(attraction.attractionId);
              const attractionInfo = attractionDetailResponse.data;
              
              attractionsWithDetails.push({
                attractionId: attraction.attractionId,
                attractionName: attractionInfo.attractionName,
                stateName: attractionInfo.stateName,
                cityName: attractionInfo.cityName,
                content: attraction.content,
                date: attraction.date,
                attractionOrder: attraction.attractionOrder,
                // 추가 정보
                first_image1: attractionInfo.firstImage1,
                first_image2: attractionInfo.firstImage2,
                latitude: attractionInfo.latitude,
                longitude: attractionInfo.longitude,
                categoryName: attractionInfo.categoryName,
                tel: attractionInfo.tel,
                addr1: attractionInfo.addr1,
                addr2: attractionInfo.addr2,
                homepage: attractionInfo.homepage,
                overview: attractionInfo.overview
              });
              
            } catch (attractionError) {
              console.error(`관광지 ${attraction.attractionId} 상세 정보 로드 실패:`, attractionError);
              // 관광지 상세 정보를 가져오지 못한 경우에도 기본 정보로 추가
              attractionsWithDetails.push({
                attractionId: attraction.attractionId,
                attractionName: `관광지 ${attraction.attractionId}`,
                stateName: '',
                cityName: '',
                content: attraction.content || `관광지 ${attraction.attractionId}`,
                date: attraction.date,
                attractionOrder: attraction.attractionOrder
              });
            }
          }
        }
        
        // 새로운 계획으로 복사
        const copyData = {
          title: `${planDetail.title} (복사본)`,
          content: planDetail.content,
          startDate: planDetail.startDate,
          endDate: planDetail.endDate,
          isPublic: false, // 복사본은 기본적으로 비공개
          attractions: attractionsWithDetails
        };
        
        // 새 계획 생성
        const createResponse = await planService.createPlan(copyData);
        const newPlanId = createResponse.data.planId;
        
        // 새로 생성된 계획 편집 페이지로 이동
        this.$router.push(`/plan/edit/${newPlanId}`);
        
      } catch (error) {
        console.error('계획 복사에 실패했습니다:', error);
        alert('계획을 복사하는데 실패했습니다. 다시 시도해주세요.');
      } finally {
        this.loading = false;
      }
    },
    
    // 새 계획 추가
    addNewPlan() {
      console.log('Adding new plan');
      this.$router.push('/plan/create');
    },
    
    // 계획 업데이트 (북마크/좋아요 변경 시)
    updatePlan(updatedPlan) {
      const index = this.plans.findIndex(plan => plan.planId === updatedPlan.planId);
      if (index !== -1) {
        // 북마크 탭에서 북마크가 해제되면 목록에서 제거
        if (this.activeTab === 'bookmarks' && !updatedPlan.bookmarked) {
          this.plans.splice(index, 1);
        } else {
          this.plans.splice(index, 1, updatedPlan);
        }
      }
    },
    
    // 계획 삭제 (목록에서 제거)
    deletePlan(deletedPlan) {
      const index = this.plans.findIndex(plan => plan.planId === deletedPlan.planId);
      if (index !== -1) {
        this.plans.splice(index, 1);
      }
    }
  }
}
</script>

<style scoped>
/* 추가 스타일이 필요한 경우 */
.h-full {
  height: calc(100vh - 120px); /* 헤더/푸터 높이 제외 */
}
</style>
