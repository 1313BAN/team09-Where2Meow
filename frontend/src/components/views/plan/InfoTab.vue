<template>
  <div class="info-tab">
    <div class="info-content">
      <TripInfoEditor
        :tripTitle="tripTitle"
        :content="content"
        :startDate="startDate"
        :endDate="endDate"
        :isPublic="isPublic"
        @update:tripTitle="$emit('update:tripTitle', $event)"
        @update:content="$emit('update:content', $event)"
        @update:startDate="$emit('update:startDate', $event)"
        @update:endDate="$emit('update:endDate', $event)"
        @update:isPublic="$emit('update:isPublic', $event)"
      />
    </div>
    
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
import TripInfoEditor from './TripInfoEditor.vue'

defineProps({
  tripTitle: String,
  content: String,
  startDate: String,
  endDate: String,
  isPublic: Boolean,
  isSaving: Boolean
})

defineEmits([
  'update:tripTitle',
  'update:content',
  'update:startDate',
  'update:endDate',
  'update:isPublic',
  'savePlan'
])
</script>

<style scoped>
.info-tab {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.info-content {
  flex: 1;
  overflow-y: auto;
}

.info-content::-webkit-scrollbar {
  width: 6px;
}

.info-content::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.info-content::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.info-content::-webkit-scrollbar-thumb:hover {
  background: #a1a1a1;
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
  background-color: #00EDB3;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.save-button:hover:not(:disabled) {
  background-color: #00c49a;
}

.save-button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.save-button.saving {
  background-color: #6c757d;
}
</style>
