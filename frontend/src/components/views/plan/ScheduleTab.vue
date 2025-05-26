<template>
  <div class="schedule-tab">
    <!-- 날짜 선택 -->
    <DaySelector
      :selectedDay="selectedDay"
      :totalDays="totalDays"
      @update:selectedDay="$emit('update:selectedDay', $event)"
    />

    <!-- 일정 목록 -->
    <ScheduleList
      :currentDaySchedule="currentDaySchedule"
      :selectedDay="selectedDay"
      :selectedScheduleItem="selectedPlace"
      @selectScheduleItem="$emit('selectScheduleItem', $event)"
      @addScheduleItem="$emit('addScheduleItem')"
    />

    <!-- 저장 버튼 -->
    <div class="save-button-container">
      <button 
        @click="$emit('savePlan')"
        :disabled="isSaving"
        :class="[
          'save-button',
          isSaving ? 'saving' : ''
        ]"
      >
        {{ isSaving ? '저장 중...' : '저장하기' }}
      </button>
    </div>
  </div>
</template>

<script setup>
import DaySelector from './DaySelector.vue'
import ScheduleList from './ScheduleList.vue'

defineProps({
  selectedDay: Number,
  totalDays: Number,
  currentDaySchedule: Array,
  isSaving: Boolean,
  selectedPlace: Object
})

defineEmits([
  'update:selectedDay',
  'selectScheduleItem',
  'addScheduleItem',
  'savePlan'
])
</script>

<style scoped>
.schedule-tab {
  display: flex;
  flex-direction: column;
  flex: 1;
  overflow: hidden;
}

.save-button-container {
  padding: 15px;
  border-top: 1px solid #eee;
  background-color: #fff;
  flex-shrink: 0;
}

.save-button {
  width: 100%;
  padding: 12px 20px;
  background-color: #6FBBFF;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.save-button:hover:not(:disabled) {
  background-color: #6493bb;
}

.save-button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.save-button.saving {
  background-color: #6c757d;
}
</style>
