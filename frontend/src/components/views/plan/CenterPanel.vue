<template>
  <div class="center-panel">
    <!-- 지도 -->
    <div class="map-container">
      <div id="map" class="map">
        <!-- 구글맵 로드 영역 -->
        <div class="map-placeholder">
          <p>지도 영역</p>
        </div>
      </div>
    </div>

    <!-- 하단 상세정보 컴포넌트 -->
    <PlaceDetailCard
      v-if="selectedPlace"
      :selectedPlace="selectedPlace"
      @closePlace="$emit('closePlace')"
      @addToSchedule="$emit('addToSchedule', $event)"
    />
  </div>
</template>

<script setup>
import PlaceDetailCard from './PlaceDetailCard.vue'

defineProps({
  selectedPlace: Object
})

defineEmits(['closePlace', 'addToSchedule'])
</script>

<style scoped>
.center-panel {
  width: 100%;
  height: 100%; /* 100vh에서 100%로 변경 */
  display: flex;
  flex-direction: column;
  position: relative;
  overflow: hidden;
}

.map-container {
  flex: 1;
  background-color: #e0e0e0;
  position: relative;
  overflow: hidden;
  width: 100%;
  height: 100%;
}

.map {
  width: 100%;
  height: 100%;
  border: none;
  box-sizing: border-box;
}

.map-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #666;
  font-size: 18px;
  background-color: #f5f5f5;
}
</style>
