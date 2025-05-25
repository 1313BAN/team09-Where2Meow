<template>
  <div class="tab-content active">
    <div class="search-panel-content">
      <!-- 검색바 -->
      <SearchBar
        :searchQuery="searchQuery"
        @update:searchQuery="$emit('update:searchQuery', $event)"
      />

      <!-- 카테고리 필터 -->
      <CategoryFilter
        :categories="categories"
        :selectedCategories="selectedCategories"
        @update:selectedCategories="$emit('update:selectedCategories', $event)"
      />

      <!-- 검색 결과 -->
      <SearchResults
        :filteredSearchResults="filteredSearchResults"
        @selectPlace="$emit('selectPlace', $event)"
      />
    </div>
  </div>
</template>

<script setup>
import SearchBar from './SearchBar.vue'
import CategoryFilter from './CategoryFilter.vue'
import SearchResults from './SearchResults.vue'

defineProps({
  searchQuery: String,
  selectedCategories: Array,
  categories: Array,
  filteredSearchResults: Array
})

defineEmits([
  'update:searchQuery',
  'update:selectedCategories',
  'selectPlace'
])
</script>

<style scoped>
.tab-content {
  display: flex;
  flex-direction: column;
  flex-grow: 1;
  overflow-y: auto;
}

.tab-content.active {
  display: flex;
}

.search-panel-content {
  display: flex;
  flex-direction: column;
  height: 100%;
  padding: 0;
}
</style>
