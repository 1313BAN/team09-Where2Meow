<template>
  <div class="trip-info-editor">
    <div class="form-group">
      <label class="form-label">여행 제목</label>
      <input 
        :value="tripTitle"
        @input="$emit('update:tripTitle', $event.target.value)"
        class="form-input"
        placeholder="여행 제목을 입력하세요"
        maxlength="100"
        required
      />
    </div>
    
    <div class="form-group">
      <label class="form-label">여행 설명</label>
      <textarea 
        :value="content"
        @input="$emit('update:content', $event.target.value)"
        class="form-textarea"
        placeholder="여행에 대한 설명을 입력하세요"
        rows="10"
      />
    </div>
    
    <div class="form-group">
      <label class="form-label">여행 기간</label>
      <div class="date-inputs">
        <input 
          :value="startDate"
          @input="$emit('update:startDate', $event.target.value)"
          type="date"
          class="date-input"
          :max="endDate"
        />
        <span class="date-separator">~</span>
        <input 
          :value="endDate"
          @input="$emit('update:endDate', $event.target.value)"
          type="date"
          class="date-input"
          :min="startDate"
        />
      </div>
    </div>
    
    <div class="form-group checkbox-group">
      <label class="checkbox-label">
        <input 
          :checked="isPublic"
          @change="$emit('update:isPublic', $event.target.checked)"
          type="checkbox"
          class="checkbox-input"
        />
        <span class="checkbox-text">공개 여행 계획</span>
      </label>
    </div>
  </div>
</template>

<script setup>
defineProps({
  tripTitle: String,
  content: String,
  startDate: String,
  endDate: String,
  isPublic: Boolean
})

defineEmits([
  'update:tripTitle',
  'update:content',
  'update:startDate',
  'update:endDate',
  'update:isPublic'
])
</script>

<style scoped>
.trip-info-editor {
  padding: 20px;
  flex-shrink: 0;
}

.form-group {
  margin-bottom: 20px;
}

.form-group:last-child {
  margin-bottom: 0;
}

.form-label {
  display: block;
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.form-input,
.form-textarea,
.date-input {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  background-color: #fff;
  transition: border-color 0.2s ease;
  box-sizing: border-box;
}

.form-input:focus,
.form-textarea:focus,
.date-input:focus {
  outline: none;
  border-color: #00EDB3;
  box-shadow: 0 0 0 2px rgba(0, 237, 179, 0.1);
}

.form-textarea {
  resize: none;
  font-family: inherit;
}

.date-inputs {
  display: flex;
  align-items: center;
  gap: 10px;
}

.date-input {
  flex: 1;
}

.date-separator {
  color: #666;
  font-weight: 500;
  flex-shrink: 0;
}

.checkbox-group {
  margin-bottom: 0;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  margin-bottom: 0;
}

.checkbox-input {
  width: 16px;
  height: 16px;
  accent-color: #00EDB3;
  cursor: pointer;
}

.checkbox-text {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  cursor: pointer;
}
</style>
