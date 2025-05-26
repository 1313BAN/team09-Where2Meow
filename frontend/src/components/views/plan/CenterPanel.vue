<template>
  <div class="center-panel">
    <div class="map-container">
      <GMapMap
        ref="mapRef"
        :center="mapCenter"
        :zoom="mapZoom"
        class="google-map"
        :options="mapOptions"
        @loaded="onMapLoaded"
      >
        <!-- 검색 결과 마커들 -->
        <GMapMarker
          v-for="(place, index) in searchResults"
          :key="place.attractionId"
          :position="{
            lat: parseFloat(place.latitude),
            lng: parseFloat(place.longitude),
          }"
          :icon="
            selectedPlace?.attractionId === place.attractionId
              ? getSelectedMarkerIcon(index + 1)
              : getMarkerIcon(place, index + 1)
          "
          @click="selectPlace(place, index + 1)"
        />
      </GMapMap>
    </div>

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
import { ref, onMounted, nextTick, watch } from 'vue'

const props = defineProps({
  searchResults: Array,
  selectedPlace: Object,
  mapCenter: Object,
  mapZoom: Number,
})

const emit = defineEmits(['selectPlace', 'closePlace', 'addToSchedule'])

const mapRef = ref(null)

const mapOptions = ref({
  zoomControl: true,
  mapTypeControl: true,
  scaleControl: true,
  streetViewControl: true,
  rotateControl: true,
  fullscreenControl: true,
})

// ✅ 마커 아이콘 설정
// 숫자가 포함된 더 큰 마커 아이콘
const getMarkerIcon = (place, index) => {
  const categoryColors = {
    12: '#FF6B6B', // 관광지
    14: '#4ECDC4', // 문화시설
    15: '#45B7D1', // 축제공연행사
    25: '#96CEB4', // 여행코스
    28: '#FFEAA7', // 레포츠
    32: '#DDA0DD', // 숙박
    38: '#FFB347', // 쇼핑
    39: '#F8BBD9', // 음식점
  }

  const color = categoryColors[place.categoryId] || '#999999'

  return {
    url: `data:image/svg+xml;charset=UTF-8,${encodeURIComponent(`
      <svg width="40" height="40" viewBox="0 0 40 40" xmlns="http://www.w3.org/2000/svg">
        <circle cx="20" cy="20" r="18" fill="${color}" stroke="white" stroke-width="3"/>
        <text x="20" y="26" text-anchor="middle" fill="white" font-size="14" font-weight="bold" font-family="Arial, sans-serif">
          ${index}
        </text>
      </svg>
    `)}`,
    scaledSize: { width: 40, height: 40 },
    anchor: { x: 20, y: 20 },
  }
}

// 선택된 마커는 더 크고 다른 색상
const getSelectedMarkerIcon = (index) => {
  return {
    url: `data:image/svg+xml;charset=UTF-8,${encodeURIComponent(`
      <svg width="50" height="50" viewBox="0 0 50 50" xmlns="http://www.w3.org/2000/svg">
        <circle cx="25" cy="25" r="22" fill="#FF6B6B" stroke="white" stroke-width="4"/>
        <circle cx="25" cy="25" r="15" fill="white" opacity="0.3"/>
        <text x="25" y="31" text-anchor="middle" fill="white" font-size="16" font-weight="bold" font-family="Arial, sans-serif">
          ${index}
        </text>
      </svg>
    `)}`,
    scaledSize: { width: 50, height: 50 },
    anchor: { x: 25, y: 25 },
  }
}

const selectPlace = (place) => {
  emit('selectPlace', place)
}

// ✅ mapCenter와 mapZoom 변경 감지
watch(
  [() => props.mapCenter, () => props.mapZoom],
  ([newCenter, newZoom]) => {
    if (mapRef.value && mapRef.value.$mapObject) {
      if (newCenter) {
        mapRef.value.$mapObject.setCenter(newCenter)
      }
      if (newZoom !== undefined && newZoom !== null) {
        mapRef.value.$mapObject.setZoom(newZoom)
      }
    }
  },
  { deep: true },
)

const onMapLoaded = (map) => {
  nextTick(() => {
    if (window.google && window.google.maps) {
      setTimeout(() => {
        window.google.maps.event.trigger(map, 'resize')
        if (props.mapCenter) {
          map.setCenter(props.mapCenter)
        }
        if (props.mapZoom) {
          map.setZoom(props.mapZoom)
        }
      }, 100)
    }
  })
}

onMounted(() => {
  window.addEventListener('resize', () => {
    if (mapRef.value && mapRef.value.$mapObject && window.google) {
      setTimeout(() => {
        window.google.maps.event.trigger(mapRef.value.$mapObject, 'resize')
      }, 100)
    }
  })
})
</script>

<style scoped>
.center-panel {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  position: relative;
  overflow: hidden;
}

.map-container {
  flex: 1;
  width: 100%;
  height: 100%;
  overflow: hidden;
  position: relative;
}

.google-map {
  width: 100% !important;
  height: 100% !important;
  min-height: 400px;
}

.google-map :deep(.gm-style) {
  width: 100% !important;
  height: 100% !important;
}

.google-map :deep(.gm-style > div) {
  width: 100% !important;
  height: 100% !important;
}
</style>
