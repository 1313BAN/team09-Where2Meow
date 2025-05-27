<template>
  <div class="schedule-item-list">
    <div class="day-header">
      <span class="day-title">day{{ selectedDay }}</span>
    </div>
    
    <!-- SearchResults 컴포넌트를 재사용 -->
    <SearchResults
      :mode="'schedule'"
      :scheduleItems="currentDaySchedule"
      :selectedScheduleItem="selectedScheduleItem"
      :isSearching="false"
      @selectItem="handleSelectItem"
      @addScheduleItem="$emit('addScheduleItem')"
      @updateScheduleOrder="handleUpdateScheduleOrder"
    />
  </div>
</template>

<script setup>
import SearchResults from './SearchResults.vue'

defineProps({
  currentDaySchedule: Array,
  selectedDay: Number,
  selectedScheduleItem: Object
})

const emit = defineEmits(['selectScheduleItem', 'addScheduleItem', 'updateScheduleOrder'])

// ✅ 일정 아이템 선택 처리 함수
const handleSelectItem = (item, index) => {
  console.log('일정 아이템 선택:', item, index) // 디버깅용
  emit('selectScheduleItem', item)
}

// ✅ 일정 순서 변경 처리 함수
const handleUpdateScheduleOrder = (newOrderItems) => {
  console.log('일정 순서 변경:', newOrderItems) // 디버깅용
  emit('updateScheduleOrder', newOrderItems)
}
</script>

<style scoped>
.schedule-item-list {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  min-height: 0;
}

.day-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px 10px 20px;
  border-bottom: 2px solid #f0f0f0;
  flex-shrink: 0;
}

.day-title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}
</style>
