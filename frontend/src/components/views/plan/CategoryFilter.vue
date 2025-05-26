<template>
  <div class="category-filters">
    <div class="filter-buttons">
      <button
        v-for="category in availableCategories"
        :key="category.categoryId"
        @click="selectCategory(category.categoryId)"
        :class="[
          'filter-button',
          selectedCategoryIds.includes(category.categoryId) ? 'active' : ''
        ]"
      >
        {{ category.categoryName }}
      </button>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  availableCategories: Array,
  selectedCategoryIds: Array
})

const emit = defineEmits(['update:selectedCategoryIds'])

const selectCategory = (categoryId) => {
  // 이미 선택된 카테고리를 다시 누르면 선택 해제
  if (props.selectedCategoryIds.includes(categoryId)) {
    emit('update:selectedCategoryIds', [])
  } else {
    // 새로운 카테고리 선택 (기존 선택 취소하고 새로 선택)
    emit('update:selectedCategoryIds', [categoryId])
  }
}
</script>

<style scoped>
.category-filters {
  border-bottom: 1px solid #eee;
  background-color: #f7f7f7;
  flex-shrink: 0;
}

.filter-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  padding: 15px;
}

.filter-button {
  background-color: #e0e0e0;
  color: #555;
  border: none;
  border-radius: 20px;
  padding: 8px 15px;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  white-space: nowrap;
}

.filter-button.active {
  background-color: #FF80CF;
  color: #fff;
  box-shadow: 0 2px 8px rgba(255, 128, 207, 0.3);
}

.filter-button:hover:not(.active) {
  background-color: #d0d0d0;
}
</style>
