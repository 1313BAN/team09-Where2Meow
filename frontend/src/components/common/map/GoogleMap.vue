<template>
  <div class="google-map-container" :style="{ height: height }">
    <!-- 지도가 로드되기 전 로딩 상태 -->
    <div v-if="!mapLoaded" class="loading-container">
      <div class="text-center text-gray-500">
        <i class="pi pi-spinner pi-spin text-2xl mb-2"></i>
        <p>지도를 불러오는 중...</p>
      </div>
    </div>
    
    <!-- 구글맵이 렌더링될 div -->
    <div 
      ref="mapContainer" 
      :class="['map-container', { 'map-loaded': mapLoaded }]"
    ></div>
    
    <!-- 지도 로드 실패 시 -->
    <div v-if="loadError" class="error-container">
      <div class="text-center text-gray-500">
        <i class="pi pi-exclamation-triangle text-2xl mb-2"></i>
        <p>지도를 불러올 수 없습니다</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, nextTick } from 'vue'

const props = defineProps({
  latitude: {
    type: Number,
    required: true,
  },
  longitude: {
    type: Number,
    required: true,
  },
  zoom: {
    type: Number,
    default: 15,
  },
  height: {
    type: String,
    default: '300px',
  },
  markerTitle: {
    type: String,
    default: '위치',
  },
})

const mapContainer = ref(null)
const mapLoaded = ref(false)
const loadError = ref(false)
let map = null
let marker = null

// 구글맵 초기화
const initializeMap = async () => {
  try {
    // 구글맵 API가 로드되었는지 확인
    if (!window.google || !window.google.maps) {
      await loadGoogleMapsAPI()
    }

    if (!mapContainer.value) {
      await nextTick()
    }

    // 지도 옵션 설정
    const mapOptions = {
      center: { lat: props.latitude, lng: props.longitude },
      zoom: props.zoom,
      mapTypeId: window.google.maps.MapTypeId.ROADMAP,
      // 지도 컨트롤 설정
      zoomControl: true,
      streetViewControl: false,
      fullscreenControl: false,
      mapTypeControl: false,
    }

    // 지도 생성
    map = new window.google.maps.Map(mapContainer.value, mapOptions)

    // 마커 생성
    marker = new window.google.maps.Marker({
      position: { lat: props.latitude, lng: props.longitude },
      map: map,
      title: props.markerTitle,
      animation: window.google.maps.Animation.DROP,
    })

    mapLoaded.value = true
    loadError.value = false
  } catch (error) {
    console.error('구글맵 초기화 실패:', error)
    loadError.value = true
    mapLoaded.value = false
  }
}

// 구글맵 API 로드
const loadGoogleMapsAPI = () => {
  return new Promise((resolve, reject) => {
    // 이미 로드되어 있다면 바로 resolve
    if (window.google && window.google.maps) {
      resolve()
      return
    }

    // 구글맵 API 스크립트 생성
    const script = document.createElement('script')
    script.src = `https://maps.googleapis.com/maps/api/js?key=${import.meta.env.VITE_GOOGLE_MAPS_API_KEY}&libraries=places`
    script.defer = true
    script.async = true

    script.onload = () => {
      resolve()
    }

    script.onerror = () => {
      reject(new Error('구글맵 API 로드 실패'))
    }

    document.head.appendChild(script)
  })
}

// 지도 위치 업데이트
const updateMapPosition = () => {
  if (map && marker) {
    const newPosition = { lat: props.latitude, lng: props.longitude }
    
    map.setCenter(newPosition)
    marker.setPosition(newPosition)
    marker.setTitle(props.markerTitle)
  }
}

// props 변경 감지
watch(
  () => [props.latitude, props.longitude],
  () => {
    if (mapLoaded.value) {
      updateMapPosition()
    }
  }
)

watch(
  () => props.markerTitle,
  () => {
    if (marker) {
      marker.setTitle(props.markerTitle)
    }
  }
)

// 컴포넌트 마운트 시 지도 초기화
onMounted(() => {
  initializeMap()
})
</script>

<style scoped>
.google-map-container {
  position: relative;
  width: 100%;
  /* height는 inline style로 설정 */
  border-radius: 12px;
  overflow: hidden;
  background-color: #f5f5f5;
}

.map-container {
  width: 100%;
  height: 100%;
  opacity: 0;
  transition: opacity 0.3s ease-in-out;
}

.map-container.map-loaded {
  opacity: 1;
}

.loading-container,
.error-container {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f9fafb;
  border-radius: 12px;
}

.loading-container {
  z-index: 1;
}

.error-container {
  z-index: 2;
}
</style>
