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
      @selectItem="$emit('selectScheduleItem', $event)"
      @addScheduleItem="$emit('addScheduleItem')"
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

defineEmits(['selectScheduleItem', 'addScheduleItem'])
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
