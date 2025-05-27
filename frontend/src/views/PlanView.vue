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
      :isLoading="isLoading"
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
      @updateScheduleOrder="updateScheduleOrder"
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
      :isAiProcessing="isAiProcessing"
      @update:newMessage="newMessage = $event"
      @sendMessage="sendAiMessage"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import LeftPanel from '@/components/views/plan/LeftPanel.vue'
import CenterPanel from '@/components/views/plan/CenterPanel.vue'
import RightPanel from '@/components/views/plan/RightPanel.vue'
import { planService } from '@/api/plan'
import { attractionApi } from '@/api/attraction'
import { aiApi } from '@/api/ai'
import { transformPlanDataForAPI } from '@/utils/planDataTransformer'
import { convertScheduleToAiFormat, convertAiResponseToSchedule, detectCommandType } from '@/utils/aiDataConverter'

// 라우트 정보 가져오기
const route = useRoute()

// 편집 모드 여부 확인
const isEditMode = computed(() => {
  return route.name === 'plan-edit' && route.params.planId
})

// 현재 편집 중인 계획 ID
const currentPlanId = computed(() => {
  return route.params.planId || null
})

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
const isLoading = ref(false) // 계획 로드용 로딩 상태 추가
const searchResults = ref([])
const currentPage = ref(0)
const hasMoreResults = ref(true)

// AI 관련 상태
const isAiProcessing = ref(false)

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
        mapZoom.value = 11; // 적절한 줌 레벨로 설정
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
const scheduleData = ref({})

const chatMessages = ref([
  {
    id: 1,
    sender: 'ai',
    text: '안냥! AI 여행 플래너 가냥이다냥. "강릉 1박 2일 여행 계획 짜줘" 같은 명령을 해보라냥!',
    time: new Date().toLocaleTimeString('ko-KR', {
      hour: '2-digit',
      minute: '2-digit',
    }),
  },
])

// 기존 계획 데이터 로드 함수
const loadExistingPlan = async (planId) => {
  try {
    console.log('계획 데이터 로드 시작:', planId)
    
    const response = await planService.getPlanDetail(planId)
    const planData = response.data
    
    console.log('로드된 계획 데이터:', planData)
    
    // 기본 정보 설정
    tripTitle.value = planData.title || ''
    content.value = planData.content || ''
    startDate.value = planData.startDate || ''
    endDate.value = planData.endDate || ''
    isPublic.value = planData.isPublic || false
    
    // 일정 데이터 변환
    if (planData.attractions && planData.attractions.length > 0) {
      const newScheduleData = {}
      
      // 각 관광지에 대해 상세 정보를 가져와서 일정 데이터 구성
      for (const attraction of planData.attractions) {
        try {
          // 관광지 상세 정보 가져오기
          const attractionDetail = await attractionApi.getAttractionDetail(attraction.attractionId)
          const attractionInfo = attractionDetail.data
          
          const planDate = new Date(attraction.date)
          const startDateObj = new Date(startDate.value)
          const dayDiff = Math.floor((planDate - startDateObj) / (1000 * 60 * 60 * 24)) + 1
          
          if (!newScheduleData[dayDiff]) {
            newScheduleData[dayDiff] = []
          }
          
          newScheduleData[dayDiff].push({
            attractionId: attraction.attractionId,
            attractionName: attractionInfo.attractionName || attractionInfo.name,
            name: attractionInfo.attractionName || attractionInfo.name,
            stateName: attractionInfo.stateName,
            cityName: attractionInfo.cityName,
            location: attractionInfo.stateName && attractionInfo.cityName 
              ? `${attractionInfo.stateName} ${attractionInfo.cityName}` 
              : '위치 정보 없음',
            duration: '시간 미정',
            content: attraction.content || attractionInfo.attractionName || attractionInfo.name,
            date: attraction.date,
            first_image1: attractionInfo.first_image1,
            first_image2: attractionInfo.first_image2,
            image: attractionInfo.image || attractionInfo.first_image1,
            categoryName: attractionInfo.categoryName,
            attraction_category_name: attractionInfo.attraction_category_name,
            latitude: attractionInfo.latitude,
            longitude: attractionInfo.longitude,
            attractionOrder: attraction.attractionOrder,
            // 추가 정보
            tel: attractionInfo.tel,
            addr1: attractionInfo.addr1,
            addr2: attractionInfo.addr2,
            homepage: attractionInfo.homepage,
            overview: attractionInfo.overview
          })
        } catch (attractionError) {
          console.error(`관광지 ${attraction.attractionId} 상세 정보 로드 실패:`, attractionError)
          // 관광지 정보를 가져오지 못한 경우에도 기본 정보로 추가
          const planDate = new Date(attraction.date)
          const startDateObj = new Date(startDate.value)
          const dayDiff = Math.floor((planDate - startDateObj) / (1000 * 60 * 60 * 24)) + 1
          
          if (!newScheduleData[dayDiff]) {
            newScheduleData[dayDiff] = []
          }
          
          newScheduleData[dayDiff].push({
            attractionId: attraction.attractionId,
            attractionName: `관광지 ${attraction.attractionId}`,
            name: `관광지 ${attraction.attractionId}`,
            stateName: '정보 없음',
            cityName: '정보 없음',
            location: '위치 정보 없음',
            duration: '시간 미정',
            content: attraction.content || `관광지 ${attraction.attractionId}`,
            date: attraction.date,
            first_image1: null,
            first_image2: null,
            image: null,
            categoryName: '정보 없음',
            attraction_category_name: '정보 없음',
            latitude: null,
            longitude: null,
            attractionOrder: attraction.attractionOrder
          })
        }
      }
      
      // 각 날짜별로 attractionOrder로 정렬
      Object.keys(newScheduleData).forEach(day => {
        newScheduleData[day].sort((a, b) => (a.attractionOrder || 0) - (b.attractionOrder || 0))
      })
      
      scheduleData.value = newScheduleData
    }
    
    console.log('변환된 일정 데이터:', scheduleData.value)
    
  } catch (error) {
    console.error('계획 로드 실패:', error)
    alert('계획 정보를 불러오는데 실패했습니다.')
  } finally {
    isLoading.value = false
  }
}

// 컴포넌트 마운트 시 로직
onMounted(async () => {
  try {
    // 카테고리 목록 로드
    const response = await attractionApi.getAllCategories()
    availableCategories.value = response.data
    
    // 편집 모드일 때 기존 계획 데이터 로드
    if (isEditMode.value && currentPlanId.value) {
      await loadExistingPlan(currentPlanId.value)
    }
    
  } catch (error) {
    console.error('초기화 실패:', error)
    alert('페이지 초기화에 실패했습니다.')
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
  
  // 현재 선택된 장소가 동일한 장소인 경우, 해당 장소의 메모도 업데이트
  if (selectedPlace.value && selectedPlace.value.attractionId === place.attractionId) {
    selectedPlace.value.content = memo;
  }
  
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
  console.log('selectScheduleItem 호출됨:', item) // 디버깅용
  
  selectedPlace.value = {
    ...item,
    // 기존 데이터 유지하면서 호환성 보장
    attractionName: item.attractionName || item.name,
    name: item.name || item.attractionName,
    // 이미지 정보 사용 (기본 이미지 대신 실제 데이터 사용)
    image: item.image || item.first_image1 || item.first_image2,
    first_image1: item.first_image1,
    first_image2: item.first_image2,
    // 위치 정보 사용
    stateName: item.stateName,
    cityName: item.cityName,
    location: item.location,
    // 카테고리 정보
    categoryName: item.categoryName,
    attraction_category_name: item.attraction_category_name,
    // 좌표 정보
    latitude: item.latitude,
    longitude: item.longitude,
    // 메모 정보
    content: item.content,
    // 기본 리뷰 정보 (일정 아이템에는 리뷰가 없으므로 기본값)
    rating: 0,
    reviews: 0,
    reviewCount: 0,
    reviewAvgScore: 0,
    description: item.content || '일정에 추가된 장소입니다.',
  }
  
  // ✅ 지도 중심을 해당 장소로 이동
  if (item.latitude && item.longitude) {
    mapCenter.value = {
      lat: parseFloat(item.latitude),
      lng: parseFloat(item.longitude)
    }
    mapZoom.value = 15
    console.log('일정 아이템 클릭으로 지도 중심 변경:', mapCenter.value, '줌:', mapZoom.value) // 디버깅용
  }
}

const addScheduleItem = () => {
  console.log('일정 추가')
}

// ✅ 일정 순서 변경 처리 함수
const updateScheduleOrder = (newOrderItems) => {
  console.log('일정 순서 변경:', selectedDay.value, newOrderItems)
  
  // 현재 선택된 날짜의 일정을 새로운 순서로 업데이트
  scheduleData.value[selectedDay.value] = newOrderItems
  
  console.log('업데이트된 일정 데이터:', scheduleData.value[selectedDay.value])
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
    attractionName: place.attractionName || place.name,
    name: place.attractionName || place.name,
    stateName: place.stateName,
    cityName: place.cityName,
    location:
      place.stateName && place.cityName
        ? `${place.stateName} ${place.cityName}`
        : place.location || '위치 정보 없음',
    duration: '시간 미정',
    content: memo || place.attractionName || place.name,
    date: date,
    // 이미지 정보 추가 (SearchResults에서 사용)
    first_image1: place.first_image1,
    first_image2: place.first_image2,
    image: place.image,
    // 카테고리 정보 추가
    categoryName: place.categoryName,
    attraction_category_name: place.attraction_category_name,
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

const sendAiMessage = async () => {
  if (!newMessage.value.trim() || isAiProcessing.value) return

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
  
  const originalMessage = newMessage.value
  newMessage.value = ''
  
  try {
    isAiProcessing.value = true
    
    // 로딩 메시지 추가
    const loadingMessage = {
      id: chatMessages.value.length + 1,
      sender: 'ai',
      text: '가냥이가 여행 계획을 생성하고 있습니다...',
      time: new Date().toLocaleTimeString('ko-KR', {
        hour: '2-digit',
        minute: '2-digit',
      }),
      isLoading: true
    }
    
    chatMessages.value.push(loadingMessage)
    
    // 명령어 유형 감지
    const commandType = detectCommandType(originalMessage)
    console.log('감지된 명령 유형:', commandType)
    
    let response
    let query = originalMessage
    
    if (commandType === 'create') {
      // 생성 명령
      // 기간 정보 추가
      if (startDate.value && endDate.value) {
        query = `기간: ${startDate.value} ~ ${endDate.value}, 질문: ${originalMessage}`
      }
      
      response = await aiApi.createPlan(query)
    } else {
      // 업데이트 명령
      const currentPlan = convertScheduleToAiFormat(scheduleData.value, startDate.value, endDate.value)
      response = await aiApi.updatePlan(originalMessage, currentPlan)
    }
    
    console.log('AI 응답:', response.data)
    
    // 로딩 메시지 제거
    const loadingIndex = chatMessages.value.findIndex(msg => msg.isLoading)
    if (loadingIndex !== -1) {
      chatMessages.value.splice(loadingIndex, 1)
    }
    
    // AI 응답 처리
    if (response.data.attractions && response.data.attractions.length > 0) {
      // 일정 데이터 업데이트 (비동기로 이미지 URL 가져오기)
      const newScheduleData = await convertAiResponseToSchedule(response.data.attractions, startDate.value)
      scheduleData.value = newScheduleData
      
      // 설명 메시지 추가
      const description = response.data.description || '여행 계획이 성공적으로 생성/업데이트되었습니다!'
      
      const aiMessage = {
        id: chatMessages.value.length + 1,
        sender: 'ai',
        text: description,
        time: new Date().toLocaleTimeString('ko-KR', {
          hour: '2-digit',
          minute: '2-digit',
        }),
      }
      
      chatMessages.value.push(aiMessage)
      
      // 일정 탭으로 이동
      activeTab.value = 'schedule'
      selectedDay.value = 1
      
      // 지도 재조정
      adjustMapToSchedule()
      
    } else {
      // 에러 메시지
      const errorMessage = {
        id: chatMessages.value.length + 1,
        sender: 'ai',
        text: '죄송합니다. 여행 계획을 생성하는데 실패했습니다. 다른 명령을 시도해 주세요.',
        time: new Date().toLocaleTimeString('ko-KR', {
          hour: '2-digit',
          minute: '2-digit',
        }),
      }
      
      chatMessages.value.push(errorMessage)
    }
    
  } catch (error) {
    console.error('AI 요청 실패:', error)
    
    // 로딩 메시지 제거
    const loadingIndex = chatMessages.value.findIndex(msg => msg.isLoading)
    if (loadingIndex !== -1) {
      chatMessages.value.splice(loadingIndex, 1)
    }
    
    // 에러 메시지 추가
    const errorMessage = {
      id: chatMessages.value.length + 1,
      sender: 'ai',
      text: '에러가 발생했습니다. 다시 시도해 주세요.',
      time: new Date().toLocaleTimeString('ko-KR', {
        hour: '2-digit',
        minute: '2-digit',
      }),
    }
    
    chatMessages.value.push(errorMessage)
  } finally {
    isAiProcessing.value = false
  }
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

    let response
    if (isEditMode.value && currentPlanId.value) {
      // 편집 모드: 기존 계획 업데이트
      response = await planService.updatePlan(currentPlanId.value, planData)
      console.log('업데이트 성공:', response.data)
      alert('여행 계획이 성공적으로 수정되었습니다!')
    } else {
      // 새로운 계획 생성
      response = await planService.createPlan(planData)
      console.log('생성 성공:', response.data)
      alert('여행 계획이 성공적으로 저장되었습니다!')
    }

  } catch (error) {
    console.error('저장 실패:', error)

    if (error.response?.data?.message) {
      alert(`저장 실패: ${error.response.data.message}`)
    } else {
      const action = isEditMode.value ? '수정' : '저장'
      alert(`여행 계획 ${action} 중 오류가 발생했습니다.`)
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