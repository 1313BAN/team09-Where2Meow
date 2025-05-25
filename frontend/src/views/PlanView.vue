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
      :selectedCategories="selectedCategories"
      :categories="categories"
      :filteredSearchResults="filteredSearchResults"
      :isSaving="isSaving"
      @update:activeTab="activeTab = $event"
      @update:tripTitle="tripTitle = $event"
      @update:content="content = $event"
      @update:startDate="startDate = $event"
      @update:endDate="endDate = $event"
      @update:isPublic="isPublic = $event"
      @update:selectedDay="selectedDay = $event"
      @update:searchQuery="searchQuery = $event"
      @update:selectedCategories="selectedCategories = $event"
      @selectScheduleItem="selectScheduleItem"
      @addScheduleItem="addScheduleItem"
      @selectPlace="selectPlace"
      @addToSchedule="addToSchedule"
      @savePlan="savePlan"
    />

    <!-- 중앙 지도 영역 -->
    <div class="center-panel">
      <CenterPanel 
        :selectedPlace="selectedPlace"
        @closePlace="selectedPlace = null"
        @addToSchedule="addToSchedule"
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
// 기존 script 내용 동일
import { ref, computed } from 'vue'
import LeftPanel from '@/components/views/plan/LeftPanel.vue'
import CenterPanel from '@/components/views/plan/CenterPanel.vue'
import RightPanel from '@/components/views/plan/RightPanel.vue'
import { planService } from '@/api/plan'
import { transformPlanDataForAPI } from '@/utils/planDataTransformer'

// 반응형 데이터
const activeTab = ref('info')
const tripTitle = ref('제주도 여행')
const content = ref('')
const startDate = ref('2024-06-01')
const endDate = ref('2024-06-03')
const isPublic = ref(false)
const selectedDay = ref(1)
const searchQuery = ref('')
const selectedCategories = ref([])
const selectedPlace = ref(null)
const newMessage = ref('')
const isSaving = ref(false)

// 계산된 속성들과 메서드들은 기존과 동일
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

const filteredSearchResults = computed(() => {
  let results = searchResults.value
  
  if (searchQuery.value) {
    results = results.filter(place => 
      place.name.toLowerCase().includes(searchQuery.value.toLowerCase())
    )
  }
  
  if (selectedCategories.value.length > 0) {
    results = results.filter(place => 
      selectedCategories.value.includes(place.category)
    )
  }
  
  return results
})

// 데이터와 메서드들은 기존과 동일
const categories = ref(['관광지', '음식점', '카페', '숙소', '쇼핑'])

const scheduleData = ref({
  1: [
    {
      attractionId: 1,
      name: '도톤보리 맛집',
      location: '오사카',
      duration: '09:00 - 10:00',
      content: '도톤보리 맛집 방문'
    }
  ]
})

const searchResults = ref([
  {
    id: 1,
    attractionId: 4,
    name: 'Kyoboshi Sembikiya',
    category: '카페',
    rating: 3.8,
    reviews: 381,
    image: 'https://via.placeholder.com/150',
    description: '¥2,000~3,000 · 카페'
  }
])

const chatMessages = ref([
  {
    id: 1,
    sender: 'ai',
    text: '안녕하세요! 제주도 여행 계획을 함께 만들어 볼까요?',
    time: '09:30'
  }
])

// 메서드들은 기존과 동일
const selectScheduleItem = (item) => {
  selectedPlace.value = {
    ...item,
    rating: 4.5,
    reviews: 100,
    image: 'https://via.placeholder.com/150',
    description: '선택된 일정 장소입니다.'
  }
}

const addScheduleItem = () => {
  console.log('일정 추가')
}

const selectPlace = (place) => {
  selectedPlace.value = place
}

const addToSchedule = (place) => {
  if (!scheduleData.value[selectedDay.value]) {
    scheduleData.value[selectedDay.value] = []
  }
  
  scheduleData.value[selectedDay.value].push({
    attractionId: place.attractionId || place.id,
    name: place.name,
    location: place.category,
    duration: '시간 미정',
    content: place.description || place.name
  })
  
  selectedPlace.value = null
}

const sendMessage = () => {
  if (!newMessage.value.trim()) return
  
  const userMessage = {
    id: chatMessages.value.length + 1,
    sender: 'user',
    text: newMessage.value,
    time: new Date().toLocaleTimeString('ko-KR', { 
      hour: '2-digit', 
      minute: '2-digit' 
    })
  }
  
  chatMessages.value.push(userMessage)
  
  setTimeout(() => {
    const aiMessage = {
      id: chatMessages.value.length + 1,
      sender: 'ai',
      text: '좋은 선택이네요! 더 도움이 필요하시면 말씀해 주세요.',
      time: new Date().toLocaleTimeString('ko-KR', { 
        hour: '2-digit', 
        minute: '2-digit' 
      })
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
      scheduleData: scheduleData.value
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
  height: calc(100vh - 56px); /* 헤더바 56px 제외 */
  width: 100vw;
  overflow: hidden;
}

.center-panel {
  flex: 1;
  min-width: 0;
}
</style>
