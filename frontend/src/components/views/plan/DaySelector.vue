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
  padding: 10px 15px;
  background-color: #f0f2f5;
  flex-shrink: 0;
  display: flex;
  align-items: center;
}

.day-selector {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  grid-auto-rows: min-content;
  gap: 8px;
  padding: 0;
  width: 100%;
}

.day-button {
  padding: 8px 16px;
  border-radius: 20px;
  background-color: #e0e0e0;
  color: #555;
  font-weight: 600;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s ease;
  border: none;
  text-align: center;
  margin: 0;
  height: auto;
}

.day-button.active {
  background-color: #6FBBFF;
  color: #fff;
}

.day-button:hover:not(.active) {
  background-color: #d0d0d0;
}

/* 반응형 그리드 */
@media (max-width: 768px) {
  .day-selector {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 480px) {
  .day-selector {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
