<template>
  <div class="search-results">
    <div 
      v-for="place in filteredSearchResults" 
      :key="place.id"
      @click="$emit('selectPlace', place)"
      class="result-item"
    >
      <img 
        :src="place.image" 
        :alt="place.name"
        class="item-thumbnail"
      />
      <div class="item-details">
        <div class="item-title">{{ place.name }}</div>
        <div class="item-rating">
          <span>⭐</span>
          <span>{{ place.rating }}</span>
          <span>({{ place.reviews }})</span>
        </div>
        <div class="item-info">{{ place.description }}</div>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  filteredSearchResults: Array
})

defineEmits(['selectPlace'])
</script>

<style scoped>
.search-results {
  flex-grow: 1;
  padding: 0 15px;
}

.result-item {
  display: flex;
  align-items: flex-start;
  padding: 15px 0;
  border-bottom: 1px solid #eee;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.result-item:hover {
  background-color: #f9f9f9;
}

.result-item:last-child {
  border-bottom: none;
}

.item-thumbnail {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 8px;
  margin-right: 15px;
  flex-shrink: 0;
}

.result-item .item-details {
  flex-grow: 1;
  display: flex;
  flex-direction: column;
}

.result-item .item-title {
  font-weight: bold;
  font-size: 1.1em;
  color: #333;
  margin-bottom: 3px;
}

.result-item .item-rating {
  font-size: 0.9em;
  color: #666;
  margin-bottom: 3px;
}

.result-item .item-rating span:first-child {
  color: #ffa500;
}

.result-item .item-info {
  font-size: 0.85em;
  color: #888;
}
</style>
