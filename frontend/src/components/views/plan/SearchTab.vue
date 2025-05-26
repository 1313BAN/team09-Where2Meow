<template>
  <div class="search-tab">
    <div class="search-panel-content">
      <!-- 검색바 -->
      <SearchBar
        :searchQuery="searchQuery"
        :isSearching="isSearching"
        @update:searchQuery="$emit('update:searchQuery', $event)"
      />

      <!-- 카테고리 필터 -->
      <CategoryFilter
        :availableCategories="availableCategories"
        :selectedCategoryIds="selectedCategoryIds"
        @update:selectedCategoryIds="$emit('update:selectedCategoryIds', $event)"
      />

      <!-- 검색 결과 -->
      <SearchResults
        mode="search"
        :searchResults="searchResults"
        :isSearching="isSearching"
        :hasMoreResults="hasMoreResults"
        :searchQuery="searchQuery"
        :selectedPlace="selectedPlace"
        @selectItem="$emit('selectPlace', $event)"
        @loadMoreResults="$emit('loadMoreResults')"
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
  selectedCategoryIds: Array,
  availableCategories: Array,
  searchResults: Array,
  isSearching: Boolean,
  hasMoreResults: Boolean,
  selectedPlace: Object
})

defineEmits(['update:searchQuery', 'update:selectedCategoryIds', 'selectPlace', 'loadMoreResults'])
</script>

<style scoped>
.search-tab {
  display: flex;
  flex-direction: column;
  flex: 1;
  overflow: hidden;
}

.search-panel-content {
  display: flex;
  flex-direction: column;
  height: 100%;
  padding: 0;
}
</style>
