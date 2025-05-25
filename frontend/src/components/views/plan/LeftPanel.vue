<template>
  <div class="left-panel">
    <!-- 탭 헤더 (3개 탭) -->
    <PanelHeader 
      :activeTab="activeTab"
      @update:activeTab="$emit('update:activeTab', $event)"
    />

    <!-- 정보 탭 -->
    <InfoTab
      v-if="activeTab === 'info'"
      :tripTitle="tripTitle"
      :content="content"
      :startDate="startDate"
      :endDate="endDate"
      :isPublic="isPublic"
      :isSaving="isSaving"
      @update:tripTitle="$emit('update:tripTitle', $event)"
      @update:content="$emit('update:content', $event)"
      @update:startDate="$emit('update:startDate', $event)"
      @update:endDate="$emit('update:endDate', $event)"
      @update:isPublic="$emit('update:isPublic', $event)"
      @savePlan="$emit('savePlan')"
    />

    <!-- 일정 탭 -->
    <ScheduleTab
      v-if="activeTab === 'schedule'"
      :selectedDay="selectedDay"
      :totalDays="totalDays"
      :currentDaySchedule="currentDaySchedule"
      :isSaving="isSaving"
      @update:selectedDay="$emit('update:selectedDay', $event)"
      @selectScheduleItem="$emit('selectScheduleItem', $event)"
      @addScheduleItem="$emit('addScheduleItem')"
      @savePlan="$emit('savePlan')"
    />

    <!-- 검색 탭 -->
    <SearchTab
      v-if="activeTab === 'search'"
      :searchQuery="searchQuery"
      :selectedCategories="selectedCategories"
      :categories="categories"
      :filteredSearchResults="filteredSearchResults"
      @update:searchQuery="$emit('update:searchQuery', $event)"
      @update:selectedCategories="$emit('update:selectedCategories', $event)"
      @selectPlace="$emit('selectPlace', $event)"
    />
  </div>
</template>

<script setup>
import PanelHeader from './PanelHeader.vue'
import InfoTab from './InfoTab.vue'
import ScheduleTab from './ScheduleTab.vue'
import SearchTab from './SearchTab.vue'

defineProps({
  activeTab: String,
  tripTitle: String,
  content: String,
  startDate: String,
  endDate: String,
  isPublic: Boolean,
  selectedDay: Number,
  totalDays: Number,
  currentDaySchedule: Array,
  searchQuery: String,
  selectedCategories: Array,
  categories: Array,
  filteredSearchResults: Array,
  isSaving: Boolean
})

defineEmits([
  'update:activeTab',
  'update:tripTitle',
  'update:content',
  'update:startDate',
  'update:endDate',
  'update:isPublic',
  'update:selectedDay',
  'update:searchQuery',
  'update:selectedCategories',
  'selectScheduleItem',
  'addScheduleItem',
  'selectPlace',
  'savePlan'
])
</script>

<style scoped>
.left-panel {
  width: 300px;
  background-color: #fff;
  box-shadow: 2px 0 5px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  height: 100%; /* 100vh에서 100%로 변경 */
  overflow: hidden;
}
</style>
