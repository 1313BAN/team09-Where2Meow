<template>
  <div class="schedule-item-list">
    <div class="day-header">
      <span class="day-title">day{{ selectedDay }}</span>
      <!-- <span class="add-button" @click="$emit('addScheduleItem')">add</span> -->
    </div>
    
    <div class="schedule-items">
      <div 
        v-for="(item, index) in currentDaySchedule" 
        :key="index"
        @click="$emit('selectScheduleItem', item)"
        class="schedule-item"
      >
        <div class="item-marker">{{ index + 1 }}</div>
        <div class="item-card">
          <div class="item-details">
            <div class="item-title">{{ item.name }}</div>
            <div class="item-location">{{ item.location }}</div>
          </div>
          <div class="item-duration">{{ item.duration }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  currentDaySchedule: Array,
  selectedDay: Number
})

defineEmits(['selectScheduleItem', 'addScheduleItem'])
</script>

<style scoped>
.schedule-item-list {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  min-height: 0;
}

.schedule-item-list::-webkit-scrollbar {
  width: 6px;
}

.schedule-item-list::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.schedule-item-list::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.schedule-item-list::-webkit-scrollbar-thumb:hover {
  background: #a1a1a1;
}

.day-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 2px solid #f0f0f0;
}

.day-title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.add-button {
  cursor: pointer;
  color: #00EDB3;
  font-size: 14px;
  font-weight: 600;
  padding: 6px 12px;
  border-radius: 4px;
  transition: background-color 0.2s ease;
}

.add-button:hover {
  background-color: rgba(0, 237, 179, 0.1);
}

.schedule-item {
  display: flex;
  align-items: flex-start;
  gap: 15px;
  margin-bottom: 16px;
  cursor: pointer;
}

.item-marker {
  width: 28px;
  height: 28px;
  background-color: #FFD900;
  border-radius: 50%;
  flex-shrink: 0;
  display: flex;
  justify-content: center;
  align-items: center;
  font-weight: bold;
  color: #333;
  font-size: 14px;
  margin-top: 4px;
}

.item-card {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  flex: 1;
  padding: 16px;
  transition: box-shadow 0.2s ease;
}

.item-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
}

.item-details {
  margin-bottom: 8px;
}

.item-title {
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
  font-size: 15px;
}

.item-location {
  font-size: 13px;
  color: #666;
}

.item-duration {
  font-size: 12px;
  color: #999;
  text-align: right;
}
</style>
