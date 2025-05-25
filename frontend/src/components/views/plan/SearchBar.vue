<template>
  <div class="search-bar">
    <span class="search-icon material-icons">search</span>
    <input 
      :value="searchQuery"
      @input="$emit('update:searchQuery', $event.target.value)"
      class="search-input"
      placeholder="장소를 검색하세요"
    />
  </div>
</template>

<script setup>
defineProps({
  searchQuery: String
})

defineEmits(['update:searchQuery'])
</script>

<style scoped>
.search-bar {
  display: flex;
  align-items: center;
  padding: 10px 15px;
  background-color: #f0f2f5;
  border-bottom: 1px solid #eee;
  flex-shrink: 0;
}

.search-bar .search-icon {
  color: #888;
  margin-right: 10px;
  font-size: 20px;
}

.search-bar .search-input {
  flex-grow: 1;
  padding: 8px 10px;
  border: none;
  border-radius: 5px;
  outline: none;
  font-size: 1em;
  background-color: #fff;
}

.search-bar .search-input::placeholder {
  color: #bbb;
}
</style>
