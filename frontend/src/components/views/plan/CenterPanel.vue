<template>
  <div class="center-panel">
    <!-- 지도 -->
    <div class="map-container">
      <GMapMap 
        ref="mapRef"
        :center="center" 
        :zoom="12" 
        class="google-map"
        :options="mapOptions"
        @loaded="onMapLoaded"
      >
        <GMapMarker :position="center" />
      </GMapMap>
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
import { ref, onMounted, nextTick, watch } from 'vue'

const center = ref({ lat: 37.5665, lng: 126.9780 })
const mapRef = ref(null)

const mapOptions = ref({
  zoomControl: true,
  mapTypeControl: true,
  scaleControl: true,
  streetViewControl: true,
  rotateControl: true,
  fullscreenControl: true
})

// 지도 크기 재조정 함수
const resizeMap = () => {
  if (mapRef.value && mapRef.value.$mapObject && window.google) {
    setTimeout(() => {
      window.google.maps.event.trigger(mapRef.value.$mapObject, 'resize')
      mapRef.value.$mapObject.setCenter(center.value)
    }, 100)
  }
}

// 지도 로드 완료 후 처리
const onMapLoaded = (map) => {
  nextTick(() => {
    if (window.google && window.google.maps) {
      // 여러 번 리사이즈 시도
      setTimeout(() => {
        window.google.maps.event.trigger(map, 'resize')
        map.setCenter(center.value)
      }, 100)
      
      setTimeout(() => {
        window.google.maps.event.trigger(map, 'resize')
        map.setCenter(center.value)
      }, 500)
      
      setTimeout(() => {
        window.google.maps.event.trigger(map, 'resize')
        map.setCenter(center.value)
      }, 1000)
    }
  })
}

// 컴포넌트 마운트 후 처리
onMounted(() => {
  // 여러 시점에서 리사이즈 시도
  setTimeout(resizeMap, 200)
  setTimeout(resizeMap, 500)
  setTimeout(resizeMap, 1000)
  setTimeout(resizeMap, 2000)
})

// 윈도우 리사이즈 이벤트 리스너
onMounted(() => {
  window.addEventListener('resize', resizeMap)
})

defineProps({
  selectedPlace: Object,
})

defineEmits(['closePlace', 'addToSchedule'])
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

/* 구글맵에 명시적 크기 지정 */
.google-map {
  width: 100% !important;
  height: 100% !important;
  min-height: 400px;
}

/* 구글맵 내부 요소들도 강제로 크기 조정 */
.google-map :deep(.gm-style) {
  width: 100% !important;
  height: 100% !important;
}

.google-map :deep(.gm-style > div) {
  width: 100% !important;
  height: 100% !important;
}
</style>
