<template>
  <div class="search-bar">
    <svg xmlns="http://www.w3.org/2000/svg" class="search-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"/>
    </svg>
    <div class="input-container">
      <input 
        ref="searchInput"
        :value="localSearchQuery"
        @input="handleInput"
        class="search-input"
        placeholder="관광지를 검색하세요"
      />
      <div v-if="isSearching" class="loading-spinner">
        <div class="spinner"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, nextTick } from 'vue'

const props = defineProps({
  searchQuery: String,
  isSearching: Boolean
})

const emit = defineEmits(['update:searchQuery'])

const searchInput = ref(null)
const localSearchQuery = ref(props.searchQuery || '')
let searchTimeout = null

// 부모에서 searchQuery가 변경될 때 로컬 값도 업데이트
watch(() => props.searchQuery, (newValue) => {
  if (newValue !== localSearchQuery.value) {
    localSearchQuery.value = newValue || ''
  }
})

const handleInput = (event) => {
  const query = event.target.value
  localSearchQuery.value = query
  
  // 포커스 유지
  const currentFocus = document.activeElement === searchInput.value
  
  // 디바운싱: 300ms 후에 검색 실행
  clearTimeout(searchTimeout)
  searchTimeout = setTimeout(() => {
    emit('update:searchQuery', query)
    
    // 검색 후 포커스 복원
    if (currentFocus) {
      nextTick(() => {
        if (searchInput.value) {
          searchInput.value.focus()
          // 커서를 끝으로 이동
          const length = searchInput.value.value.length
          searchInput.value.setSelectionRange(length, length)
        }
      })
    }
  }, 300)
}
</script>

<style scoped>
.search-bar {
  display: flex;
  align-items: center;
  padding: 15px;
  background-color: #f0f2f5;
  border-bottom: 1px solid #eee;
  flex-shrink: 0;
}

.search-icon {
  width: 20px;
  height: 20px;
  color: #888;
  margin-right: 10px;
  flex-shrink: 0;
}

.input-container {
  flex: 1;
  position: relative;
  display: flex;
  align-items: center;
}

.search-input {
  width: 100%;
  padding: 8px 35px 8px 10px; /* 오른쪽에 스피너 공간 확보 */
  border: none;
  border-radius: 5px;
  outline: none;
  font-size: 14px;
  background-color: #fff;
  transition: box-shadow 0.2s ease;
}

.search-input:focus {
  box-shadow: 0 0 0 2px rgba(0, 237, 179, 0.2);
}

.search-input::placeholder {
  color: #bbb;
}

.loading-spinner {
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
  pointer-events: none; /* 클릭 방지 */
}

.spinner {
  width: 16px;
  height: 16px;
  border: 2px solid #f3f3f3;
  border-top: 2px solid #00EDB3;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>
