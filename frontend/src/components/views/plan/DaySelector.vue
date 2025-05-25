<template>
  <div class="day-selector-container">
    <div class="day-selector">
      <button
        v-for="day in totalDays"
        :key="day"
        @click="$emit('update:selectedDay', day)"
        :class="[
          'day-button',
          selectedDay === day ? 'active' : ''
        ]"
      >
        day{{ day }}
      </button>
    </div>
  </div>
</template>

<script setup>
defineProps({
  selectedDay: Number,
  totalDays: Number
})

defineEmits(['update:selectedDay'])
</script>

<style scoped>
.day-selector-container {
  padding: 15px 20px;
  background-color: #f0f2f5;
  flex-shrink: 0;
  border-bottom: 1px solid #e0e0e0;
}

.day-selector {
  display: flex;
  gap: 8px;
  overflow-x: auto;
  padding: 2px 0;
}

.day-selector::-webkit-scrollbar {
  height: 4px;
}

.day-selector::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 2px;
}

.day-selector::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 2px;
}

.day-selector::-webkit-scrollbar-thumb:hover {
  background: #a1a1a1;
}

.day-button {
  padding: 10px 16px;
  border-radius: 20px;
  background-color: #e0e0e0;
  color: #555;
  font-weight: 600;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s ease;
  border: none;
  white-space: nowrap;
  flex-shrink: 0;
  min-width: 60px;
}

.day-button.active {
  background-color: #6FBBFF;
  color: #fff;
}

.day-button:hover:not(.active) {
  background-color: #d0d0d0;
}
</style>
