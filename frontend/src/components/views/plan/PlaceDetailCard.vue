<template>
  <div class="map-info-component">
    <div class="info-card">
      <div class="info-image-container">
        <AttractionImage 
          :imageUrl="selectedPlace.image" 
          :alt="selectedPlace.attractionName"
          class="info-image"
        />
      </div>
      <div class="info-details">
        <div class="info-title">{{ selectedPlace.attractionName }}</div>
        <div class="info-address">{{ selectedPlace.stateName }} {{ selectedPlace.cityName }}</div>
        <div class="info-category">{{ selectedPlace.categoryName }}</div>
        <div class="info-rating" v-if="selectedPlace.reviewAvgScore > 0">
          ⭐️ {{ selectedPlace.reviewAvgScore.toFixed(1) }} ({{ selectedPlace.reviewCount }}개 리뷰)
        </div>
        <div class="flex gap-2 mt-3">
          <button 
            @click="$emit('addToSchedule', selectedPlace)"
            class="px-4 py-2 bg-[#00EDB3] text-white rounded-md hover:bg-[#00c49a] transition-colors"
          >
            일정에 추가
          </button>
          <button 
            @click="$emit('closePlace')"
            class="px-4 py-2 bg-gray-200 text-gray-700 rounded-md hover:bg-gray-300 transition-colors"
          >
            닫기
          </button>
        </div>
      </div>
    </div>
  </div>
</template>


<script setup>
import AttractionImage from '@/components/common/AttractionImage.vue'

defineProps({
  selectedPlace: Object
})

defineEmits(['closePlace', 'addToSchedule'])
</script>

<style scoped>
.map-info-component {
  position: absolute;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  width: 90%;
  max-width: 600px;
  background-color: rgba(255, 255, 255, 0.95);
  border-radius: 10px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
  padding: 15px;
  display: flex;
  flex-direction: column;
  align-items: center;
  z-index: 10;
}

.info-card {
  display: flex;
  width: 100%;
  min-height: 150px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.info-image-container {
  width: 150px;
  height: 150px;
  overflow: hidden;
  flex-shrink: 0;
}

.info-image {
  width: 100%;
  height: 100%;
}

.info-details {
  padding: 15px;
  flex-grow: 1;
}

.info-title {
  font-weight: bold;
  font-size: 1.1em;
  margin-bottom: 5px;
  color: #333;
}

.info-description {
  font-size: 0.9em;
  color: #666;
  margin-bottom: 5px;
}

.info-rating {
  font-size: 0.85em;
  color: #ffa500;
}
</style>
