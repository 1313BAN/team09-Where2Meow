<template>
  <div class="category-filters">
    <button
      v-for="category in categories"
      :key="category"
      @click="toggleCategory(category)"
      :class="[
        'filter-button',
        selectedCategories.includes(category) ? 'active' : ''
      ]"
    >
      {{ category }}
    </button>
  </div>
</template>

<script setup>
const props = defineProps({
  categories: Array,
  selectedCategories: Array
})

const emit = defineEmits(['update:selectedCategories'])

const toggleCategory = (category) => {
  const newCategories = [...props.selectedCategories]
  const index = newCategories.indexOf(category)
  
  if (index > -1) {
    newCategories.splice(index, 1)
  } else {
    newCategories.push(category)
  }
  
  emit('update:selectedCategories', newCategories)
}
</script>

<style scoped>
.category-filters {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  padding: 10px 15px;
  border-bottom: 1px solid #eee;
  background-color: #f7f7f7;
  flex-shrink: 0;
}

.filter-button {
  background-color: #e0e0e0;
  color: #555;
  border: none;
  border-radius: 20px;
  padding: 8px 15px;
  font-size: 0.9em;
  cursor: pointer;
  transition: background-color 0.2s ease, color 0.2s ease;
  white-space: nowrap;
}

.filter-button.active {
  background-color: #FF80CF;
  color: #fff;
}

.filter-button:hover {
  background-color: #d0d0d0;
}

.filter-button.active:hover {
  background-color: #e66bb8;
}
</style>
