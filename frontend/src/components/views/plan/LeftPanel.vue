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
      :selectedPlace="selectedPlace"
      @update:selectedDay="$emit('update:selectedDay', $event)"
      @selectScheduleItem="$emit('selectScheduleItem', $event)"
      @addScheduleItem="$emit('addScheduleItem')"
      @updateScheduleOrder="$emit('updateScheduleOrder', $event)"
      @savePlan="$emit('savePlan')"
    />

    <!-- 검색 탭 -->
    <SearchTab
      v-if="activeTab === 'search'"
      :searchQuery="searchQuery"
      :selectedCategoryIds="selectedCategoryIds"
      :availableCategories="availableCategories"
      :searchResults="searchResults"
      :isSearching="isSearching"
      :hasMoreResults="hasMoreResults"
      :selectedPlace="selectedPlace"
      @update:searchQuery="$emit('update:searchQuery', $event)"
      @update:selectedCategoryIds="$emit('update:selectedCategoryIds', $event)"
      @selectPlace="$emit('selectPlace', $event)"
      @loadMoreResults="$emit('loadMoreResults')"
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
  selectedCategoryIds: Array,
  availableCategories: Array,
  searchResults: Array,
  isSearching: Boolean,
  hasMoreResults: Boolean,
  isSaving: Boolean,
  selectedPlace: Object
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
  'update:selectedCategoryIds',
  'selectScheduleItem',
  'addScheduleItem',
  'updateScheduleOrder',
  'selectPlace',
  'savePlan',
  'loadMoreResults'
])
</script>

<style scoped>
.left-panel {
  width: 350px;
  background-color: #fff;
  box-shadow: 2px 0 5px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;
}
</style>
