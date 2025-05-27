<template>
  <div class="plan-container">
    <!-- 왼쪽 패널 -->
    <LeftPanel
      :activeTab="activeTab"
      :tripTitle="tripTitle"
      :content="content"
      :startDate="startDate"
      :endDate="endDate"
      :isPublic="isPublic"
      :selectedDay="selectedDay"
      :totalDays="totalDays"
      :currentDaySchedule="currentDaySchedule"
      :searchQuery="searchQuery"
      :selectedCategoryIds="selectedCategoryIds"
      :availableCategories="availableCategories"
      :searchResults="searchResults"
      :isSearching="isSearching"
      :hasMoreResults="hasMoreResults"
      :isSaving="isSaving"
      :selectedPlace="selectedPlace"
      @update:activeTab="activeTab = $event"
      @update:tripTitle="tripTitle = $event"
      @update:content="content = $event"
      @update:startDate="startDate = $event"
      @update:endDate="endDate = $event"
      @update:isPublic="isPublic = $event"
      @update:selectedDay="selectedDay = $event"
      @update:searchQuery="handleSearchQuery"
      @update:selectedCategoryIds="handleCategoryFilter"
      @selectScheduleItem="selectScheduleItem"
      @addScheduleItem="addScheduleItem"
      @selectPlace="selectPlace"
      @addToSchedule="addToSchedule"
      @savePlan="savePlan"
      @loadMoreResults="loadMoreResults"
    />

    <!-- 중앙 지도 영역 -->
    <div class="center-panel">
      <CenterPanel
        :searchResults="searchResults"
        :selectedPlace="selectedPlace"
        :mapCenter="mapCenter"
        :mapZoom="mapZoom"
        :planStartDate="startDate"
        :planEndDate="endDate"
        :currentDaySchedule="currentDaySchedule"
        :isInSchedule="isPlaceInCurrentSchedule"
        @selectPlace="selectPlace"
        @closePlace="closePlace"
        @addToSchedule="addToSchedule"
        @removeFromSchedule="removeFromSchedule"
        @updateMemo="updateMemo"
        @selectScheduleItem="selectScheduleItem"
      />
    </div>

    <!-- 오른쪽 채팅 패널 -->
    <RightPanel
      :chatMessages="chatMessages"
      :newMessage="newMessage"
      @update:newMessage="newMessage = $event"
      @sendMessage="sendMessage"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import LeftPanel from '@/components/views/plan/LeftPanel.vue'
import CenterPanel from '@/components/views/plan/CenterPanel.vue'
import RightPanel from '@/components/views/plan/RightPanel.vue'
import { planService } from '@/api/plan'
import { attractionApi } from '@/api/attraction'
import { transformPlanDataForAPI } from '@/utils/planDataTransformer'

// 반응형 데이터
const activeTab = ref('schedule')
const tripTitle = ref('')
const content = ref('')
const startDate = ref(new Date().toISOString().split('T')[0])
const endDate = ref(new Date(Date.now() + 3 * 24 * 60 * 60 * 1000).toISOString().split('T')[0])
const isPublic = ref(false)
const selectedDay = ref(1)
const searchQuery = ref('')
const selectedCategoryIds = ref([]) // 선택된 카테고리 ID 목록
const availableCategories = ref([]) // 백엔드에서 받아온 카테고리 목록
const newMessage = ref('')
const isSaving = ref(false)
const isSearching = ref(false)
const searchResults = ref([])
const currentPage = ref(0)
const hasMoreResults = ref(true)

// ✅ 지도 관련 상태 추가
const selectedPlace = ref(null)
const selectedPlaceIndex = ref(null) 
const mapCenter = ref({ lat: 37.5665, lng: 126.978 })
const mapZoom = ref(10)

// 일정에 따라 지도 재조정
const adjustMapToSchedule = () => {
  if (currentDaySchedule.value && currentDaySchedule.value.length > 0) {
    // 현재 일정에 좌표가 있는지 확인
    const hasValidCoordinates = currentDaySchedule.value.some(
      item => item.latitude && item.longitude
    );

    if (hasValidCoordinates) {
      // 첫번째 유효한 좌표가 있는 일정 아이템 찾기
      const firstValidItem = currentDaySchedule.value.find(
        item => item.latitude && item.longitude
      );

      if (firstValidItem) {
        mapCenter.value = {
          lat: parseFloat(firstValidItem.latitude),
          lng: parseFloat(firstValidItem.longitude)
        };
        mapZoom.value = 14; // 적절한 줌 레벨로 설정
      }
    }
  }
};

// activeTab 변경 감지
watch(
  () => activeTab.value,
  (newTab) => {
    // 일정 탭으로 변경될 때
    if (newTab === 'schedule') {
      // 검색 결과 마커를 지우기 위해 검색 결과 초기화
      searchResults.value = [];
      // 일정에 따라 지도 중심 재조정
      adjustMapToSchedule();
    }
  }
);

// selectedDay 변경 시 지도 재조정
watch(
  () => selectedDay.value,
  () => {
    // 일정 탭일 때만 적용
    if (activeTab.value === 'schedule') {
      adjustMapToSchedule();
    }
  }
);

// ✅ 장소 선택 함수
const selectPlace = (place, index) => {
  console.log('selectPlace 호출됨:', place, index) // 디버깅용
  
  selectedPlace.value = place
  selectedPlaceIndex.value = index
  
  if (place.latitude && place.longitude) {
    mapCenter.value = {
      lat: parseFloat(place.latitude),
      lng: parseFloat(place.longitude)
    }
    mapZoom.value = 15
    console.log('지도 중심 변경:', mapCenter.value, '줌:', mapZoom.value) // 디버깅용
  }
}

// ✅ 장소 상세정보 닫기
const closePlace = () => {
  selectedPlace.value = null
  selectedPlaceIndex.value = null  // ✅ 추가
}

// 계산된 속성들
const totalDays = computed(() => {
  if (!startDate.value || !endDate.value) return 1
  const start = new Date(startDate.value)
  const end = new Date(endDate.value)
  const diffTime = Math.abs(end - start)
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24)) + 1
  return Math.max(1, diffDays)
})

const currentDaySchedule = computed(() => {
  return scheduleData.value[selectedDay.value] || []
})

// 데이터
const scheduleData = ref({
  1: [
    {
      attractionId: 1,
      name: '경복궁',
      location: '서울 종로구',
      duration: '09:00 - 11:00',
      content: '경복궁 관광',
      latitude: 37.579617,
      longitude: 126.977041
    },
    {
      attractionId: 2,
      name: '경복궁 별주안',
      location: '서울 종로구',
      duration: '11:30 - 12:30',
      content: '별주안 관광',
      latitude: 37.582664,
      longitude: 126.975330
    },
    {
      attractionId: 3,
      name: '팀모이 골복',
      location: '서울 종로구',
      duration: '13:00 - 14:30',
      content: '골복에서 점심 먹기',
      latitude: 37.574547,
      longitude: 126.973106
    }
  ],
  2: [
    {
      attractionId: 4,
      name: '경복궁 밸워하우스',
      location: '서울 종로구',
      duration: '09:00 - 11:00',
      content: '블로그 컨텐츠 제작',
      latitude: 37.576723,
      longitude: 126.986298
    }
  ]
})

const chatMessages = ref([
  {
    id: 1,
    sender: 'ai',
    text: '안녕하세요! 제주도 여행 계획을 함께 만들어 볼까요?',
    time: '09:30',
  },
])

// 컴포넌트 마운트 시 카테고리 목록 로드
onMounted(async () => {
  try {
    // 백엔드에서 카테고리 목록을 받아올 API가 있다면 사용
    const response = await attractionApi.getAllCategories()
    availableCategories.value = response.data
  } catch (error) {
    console.error('카테고리 로드 실패:', error)
    alert('카테고리 정보를 불러오는데 실패했습니다.')
  }
})

// 검색 관련 메서드
const handleSearchQuery = async (query) => {
  searchQuery.value = query
  await performSearch()
}

const handleCategoryFilter = async (categoryIds) => {
  selectedCategoryIds.value = categoryIds
  await performSearch()
}

// 현재 선택된 장소가 일정에 있는지 확인하는 계산된 속성
const isPlaceInCurrentSchedule = computed(() => {
  if (!selectedPlace.value || !currentDaySchedule.value) return false;
  
  return currentDaySchedule.value.some(item => 
    item.attractionId === selectedPlace.value.attractionId
  );
});

// 일정에서 장소 삭제
const removeFromSchedule = (place) => {
  if (!place || !currentDaySchedule.value) return;
  
  scheduleData.value[selectedDay.value] = currentDaySchedule.value.filter(
    item => item.attractionId !== place.attractionId
  );
  
  console.log(`${selectedDay.value}일차에서 일정 삭제:`, place);
  // 상세 카드 닫기
  closePlace();
};

// 메모 업데이트
const updateMemo = (data) => {
  const { place, memo } = data;
  
  if (!place || !currentDaySchedule.value) return;
  
  const itemIndex = currentDaySchedule.value.findIndex(
    item => item.attractionId === place.attractionId
  );
  
  if (itemIndex === -1) return;
  
  // 메모 업데이트
  scheduleData.value[selectedDay.value][itemIndex].content = memo;
  
  console.log(`${selectedDay.value}일차 일정 메모 업데이트:`, place, memo);
};

// 검색 관련 메서드 수정
const performSearch = async () => {
  const hasKeyword = searchQuery.value && searchQuery.value.trim()
  const hasCategory = selectedCategoryIds.value.length > 0

  if (!hasKeyword && !hasCategory) {
    searchResults.value = []
    hasMoreResults.value = true
    currentPage.value = 0
    return
  }

  try {
    isSearching.value = true
    currentPage.value = 0

    const searchParams = {
      page: 0,
      size: 10,
    }

    if (hasKeyword) {
      searchParams.keyword = searchQuery.value.trim()
    }

    if (hasCategory) {
      // 단일 카테고리 ID 전송 (첫 번째 요소만 사용)
      searchParams.categoryId = selectedCategoryIds.value[0]
    }

    console.log('검색 요청 파라미터:', searchParams)
    const response = await attractionApi.searchAttractions(searchParams)
    console.log('검색 응답:', response.data)
    console.log(
      'page:',
      response.data.number,
      'last:',
      response.data.last,
      'totalPages:',
      response.data.totalPages,
    )

    searchResults.value = response.data.content
    hasMoreResults.value = response.data.page.number + 1 < response.data.page.totalPages

    console.log('초기 hasMoreResults 설정:', hasMoreResults.value)
  } catch (error) {
    console.error('검색 실패:', error)
    searchResults.value = []
    hasMoreResults.value = false
  } finally {
    isSearching.value = false
  }
}

const loadMoreResults = async () => {
  if (!hasMoreResults.value || isSearching.value) return

  const hasKeyword = searchQuery.value && searchQuery.value.trim()
  const hasCategory = selectedCategoryIds.value.length > 0

  if (!hasKeyword && !hasCategory) return

  try {
    isSearching.value = true
    const nextPage = currentPage.value + 1

    const searchParams = {
      page: nextPage,
      size: 10,
    }

    if (hasKeyword) {
      searchParams.keyword = searchQuery.value.trim()
    }

    if (hasCategory) {
      // 단일 카테고리 ID 전송
      searchParams.categoryId = selectedCategoryIds.value[0]
    }

    console.log('더보기 요청 파라미터:', searchParams)
    const response = await attractionApi.searchAttractions(searchParams)
    console.log('더보기 응답:', response.data)
    console.log(
      '현재 page:',
      response.data.number,
      'last:',
      response.data.last,
      'totalPages:',
      response.data.totalPages,
    )

    searchResults.value = [...searchResults.value, ...response.data.content]
    hasMoreResults.value = response.data.page.number + 1 < response.data.page.totalPages
    currentPage.value = nextPage

    console.log('hasMoreResults 업데이트됨:', hasMoreResults.value)
  } catch (error) {
    console.error('추가 로딩 실패:', error)
  } finally {
    isSearching.value = false
  }
}

// 기존 메서드들
const selectScheduleItem = (item) => {
  selectedPlace.value = {
    ...item,
    attractionName: item.name,
    rating: 4.5,
    reviews: 100,
    image: 'https://via.placeholder.com/150',
    description: '선택된 일정 장소입니다.',
  }
}

const addScheduleItem = () => {
  console.log('일정 추가')
}

const addToSchedule = (addData) => {
  const { place, date, memo } = addData
  
  // 날짜를 기반으로 몇일차인지 계산
  const startDateObj = new Date(startDate.value)
  const scheduleDateObj = new Date(date)
  const dayDiff = Math.floor((scheduleDateObj - startDateObj) / (1000 * 60 * 60 * 24)) + 1
  
  if (!scheduleData.value[dayDiff]) {
    scheduleData.value[dayDiff] = []
  }

  const newItem = {
    attractionId: place.attractionId,
    name: place.attractionName || place.name,
    location:
      place.stateName && place.cityName
        ? `${place.stateName} ${place.cityName}`
        : place.location || '위치 정보 없음',
    duration: '시간 미정',
    content: memo || place.attractionName || place.name,
    date: date,
    // 위도, 경도 정보 추가
    latitude: place.latitude,
    longitude: place.longitude
  }

  scheduleData.value[dayDiff].push(newItem)
  selectedPlace.value = null
  
  // 검색 탭에서 일정을 추가하면 일정 탭으로 자동 전환
  if (activeTab.value === 'search') {
    activeTab.value = 'schedule'
    // 선택한 일차로 이동
    selectedDay.value = dayDiff
    // 검색 결과 초기화
    searchResults.value = []
  }
  
  console.log(`${dayDiff}일차(${date})에 일정 추가:`, newItem)
}

const sendMessage = () => {
  if (!newMessage.value.trim()) return

  const userMessage = {
    id: chatMessages.value.length + 1,
    sender: 'user',
    text: newMessage.value,
    time: new Date().toLocaleTimeString('ko-KR', {
      hour: '2-digit',
      minute: '2-digit',
    }),
  }

  chatMessages.value.push(userMessage)

  setTimeout(() => {
    const aiMessage = {
      id: chatMessages.value.length + 1,
      sender: 'ai',
      text: '좋은 선택이네요! 더 도움이 필요하시면 말씀해 주세요.',
      time: new Date().toLocaleTimeString('ko-KR', {
        hour: '2-digit',
        minute: '2-digit',
      }),
    }
    chatMessages.value.push(aiMessage)
  }, 1000)

  newMessage.value = ''
}

const savePlan = async () => {
  if (isSaving.value) return

  try {
    isSaving.value = true

    const planData = transformPlanDataForAPI({
      tripTitle: tripTitle.value,
      content: content.value,
      startDate: startDate.value,
      endDate: endDate.value,
      isPublic: isPublic.value,
      scheduleData: scheduleData.value,
    })

    console.log('저장할 데이터:', planData)

    const response = await planService.createPlan(planData)

    console.log('저장 성공:', response.data)
    alert('여행 계획이 성공적으로 저장되었습니다!')
  } catch (error) {
    console.error('저장 실패:', error)

    if (error.response?.data?.message) {
      alert(`저장 실패: ${error.response.data.message}`)
    } else {
      alert('여행 계획 저장 중 오류가 발생했습니다.')
    }
  } finally {
    isSaving.value = false
  }
}
</script>

<style scoped>
.plan-container {
  display: flex;
  height: calc(100vh - 56px);
  width: 100vw;
  overflow: hidden;
}

.center-panel {
  flex: 1;
  min-width: 0;
}
</style>