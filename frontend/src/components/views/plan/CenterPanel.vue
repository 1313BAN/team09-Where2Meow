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
        <template v-if="showSearchMarkers">
          <GMapMarker
            v-for="(place, index) in searchResults"
            :key="`search-${place.attractionId}`"
            :position="{
              lat: parseFloat(place.latitude),
              lng: parseFloat(place.longitude),
            }"
            :icon="
              selectedPlace?.attractionId === place.attractionId
                ? getSelectedMarkerIcon(index + 1)
                : getMarkerIcon(place, index + 1)
            "
            :zIndex="selectedPlace?.attractionId === place.attractionId ? 1000 : 100 + index"
            @click="selectPlace(place, index + 1)"
          />
        </template>

        <!-- 일정 마커들 -->
        <template v-if="showScheduleMarkers">
          <GMapMarker
            v-for="(scheduleItem, index) in scheduleItems"
            :key="`schedule-${scheduleItem.attractionId}-${index}`"
            :position="{
              lat: parseFloat(scheduleItem.latitude),
              lng: parseFloat(scheduleItem.longitude),
            }"
            :icon="getScheduleMarkerIcon(index + 1)"
            :zIndex="200 + index"
            @click="$emit('selectScheduleItem', scheduleItem)"
          />

          <!-- 일정 경로 선 -->
          <GMapPolyline
            v-if="scheduleItems.length > 1"
            :path="schedulePath"
            :options="{
              strokeColor: '#6FBBFF',
              strokeOpacity: 0.8,
              strokeWeight: 4
            }"
          />
        </template>
      </GMapMap>
    </div>

    <PlaceDetailCard
      v-if="selectedPlace"
      :selectedPlace="selectedPlace"
      :planStartDate="planStartDate"
      :planEndDate="planEndDate"
      :isInSchedule="isInSchedule"
      @closePlace="$emit('closePlace')"
      @addToSchedule="$emit('addToSchedule', $event)"
      @removeFromSchedule="$emit('removeFromSchedule', $event)"
      @updateMemo="$emit('updateMemo', $event)"
      @viewDetail="viewPlaceDetail"
    />
  </div>
</template>

<script setup>
import PlaceDetailCard from './PlaceDetailCard.vue'
import { ref, onMounted, nextTick, watch, computed } from 'vue'

const props = defineProps({
  searchResults: Array,
  selectedPlace: Object,
  mapCenter: Object,
  mapZoom: Number,
  planStartDate: String,
  planEndDate: String,
  // 추가: 현재 일차의 일정 아이템들
  currentDaySchedule: {
    type: Array,
    default: () => []
  },
  // 현재 선택된 장소가 일정에 있는지 여부
  isInSchedule: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['selectPlace', 'closePlace', 'addToSchedule', 'selectScheduleItem', 'removeFromSchedule', 'updateMemo'])

const mapRef = ref(null)

const mapOptions = ref({
  zoomControl: true,
  mapTypeControl: true,
  scaleControl: true,
  streetViewControl: true,
  rotateControl: true,
  fullscreenControl: true,
  gestureHandling: 'cooperative', // 스크롤 동작 우선순위 설정
  scrollwheel: true,               // 마우스 휠 스크롤 활성화
  navigationControl: true,        // 네비게이션 컨트롤 활성화
  animatedZoom: true,            // 준 애니메이션 활성화
  clickableIcons: false,          // 구글 맵의 기본 POI 아이콘 클릭 비활성화
})

// 검색 결과와 일정 마커 보이기/숨기기 설정
const showScheduleMarkers = computed(() => {
  // currentDaySchedule이 있을 때만 일정 마커 표시
  return props.currentDaySchedule && props.currentDaySchedule.length > 0;
});

const showSearchMarkers = computed(() => {
  // searchResults가 있을 때만 검색 마커 표시
  return props.searchResults && props.searchResults.length > 0;
});

// 일정 아이템들의 좌표 계산
const scheduleItems = computed(() => {
  if (!props.currentDaySchedule || props.currentDaySchedule.length === 0) {
    return [];
  }
  
  return props.currentDaySchedule.map(item => {
    // 기존 일정 아이템에 latitude, longitude가 없을 경우를 대비한 처리
    return {
      ...item,
      latitude: item.latitude || 0,
      longitude: item.longitude || 0
    }
  }).filter(item => item.latitude && item.longitude); // 유효한 좌표가 있는 아이템만 필터링
});

// 일정 경로를 위한 좌표 배열
const schedulePath = computed(() => {
  return scheduleItems.value.map(item => ({
    lat: parseFloat(item.latitude),
    lng: parseFloat(item.longitude)
  }));
});

// 일정 마커 아이콘
const getScheduleMarkerIcon = (index) => {
  return {
    url: `data:image/svg+xml;charset=UTF-8,${encodeURIComponent(`
      <svg width="40" height="40" viewBox="0 0 40 40" xmlns="http://www.w3.org/2000/svg">
        <circle cx="20" cy="20" r="18" fill="#6FBBFF" stroke="white" stroke-width="3"/>
        <text x="20" y="26" text-anchor="middle" fill="white" font-size="14" font-weight="bold" font-family="Arial, sans-serif">
          ${index}
        </text>
      </svg>
    `)}`,
    scaledSize: { width: 40, height: 40 },
    anchor: { x: 20, y: 20 },
  }
}

// 부드럽게 이동하기 위한 사용자 정의 함수
const smoothlyAnimateTo = (map, targetLocation, targetZoom) => {
  if (!map || !targetLocation) return
  
  // 현재 위치
  const currentLat = map.getCenter().lat()
  const currentLng = map.getCenter().lng()
  
  // 목표 위치
  const targetLat = targetLocation.lat
  const targetLng = targetLocation.lng
  
  // 애니메이션 기본 설정
  const frames = 60  // 애니메이션 프레임 수
  const duration = 800  // 애니메이션 시간 (ms)
  
  // 애니메이션 구현
  let start = null
  
  const animate = (timestamp) => {
    if (!start) start = timestamp
    const progress = Math.min((timestamp - start) / duration, 1)
    
    // 이지함수를 통한 부드러운 이동 공식
    const easedProgress = easeInOutCubic(progress)
    
    // 현재 위치에서 목표 위치로 점진적 이동
    const nextLat = currentLat + (targetLat - currentLat) * easedProgress
    const nextLng = currentLng + (targetLng - currentLng) * easedProgress
    
    // 지도 이동
    map.setCenter({ lat: nextLat, lng: nextLng })
    
    // 애니메이션 진행 중
    if (progress < 1) {
      window.requestAnimationFrame(animate)
    } else {
      // 애니메이션 완료 후 확대/축소 적용
      if (targetZoom && map.getZoom() !== targetZoom) {
        map.setZoom(targetZoom)
      }
    }
  }
  
  // 애니메이션 시작
  window.requestAnimationFrame(animate)
}

// 이진함수 - 부드러운 애니메이션 효과를 위한 함수
const easeInOutCubic = (t) => {
  return t < 0.5 
    ? 4 * t * t * t 
    : (t - 1) * (2 * t - 2) * (2 * t - 2) + 1
}

// 장소 상세 보기
const viewPlaceDetail = (place) => {
  // 여기에서 장소 상세 보기 기능을 구현할 수 있습니다.
  // 예를 들어, 모달이나 별도의 페이지로 이동 등
  console.log('장소 상세 보기:', place);
};
const getMarkerIcon = (place, index) => {
  return {
    url: `data:image/svg+xml;charset=UTF-8,${encodeURIComponent(`
      <svg width="40" height="40" viewBox="0 0 40 40" xmlns="http://www.w3.org/2000/svg">
        <circle cx="20" cy="20" r="18" fill="#ffd900" stroke="white" stroke-width="3"/>
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
  
  // 선택된 마커를 지도 중앙에 부드럽게 표시
  if (mapRef.value && mapRef.value.$mapObject) {
    // 사용자 정의 애니메이션 함수 호출
    smoothlyAnimateTo(
      mapRef.value.$mapObject,
      {
        lat: parseFloat(place.latitude),
        lng: parseFloat(place.longitude)
      },
      props.mapZoom // 현재 zoom 유지
    )
  }
}

// 일정 변경 감지 시 지도 중심 및 줌 조정
watch(
  () => props.currentDaySchedule,
  (newSchedule) => {
    if (newSchedule && newSchedule.length > 0 && mapRef.value && mapRef.value.$mapObject) {
      // 모든 일정 마커가 화면에 보이도록 자동 맞춤
      fitMapToSchedule();
    }
  },
  { deep: true }
);

// 모든 일정 마커가 화면에 보이도록 지도 맞춤
const fitMapToSchedule = () => {
  if (!mapRef.value || !mapRef.value.$mapObject || schedulePath.value.length === 0) return;
  
  const map = mapRef.value.$mapObject;
  const bounds = new window.google.maps.LatLngBounds();
  
  // 모든 일정 좌표를 범위에 추가
  schedulePath.value.forEach(point => {
    bounds.extend(new window.google.maps.LatLng(point.lat, point.lng));
  });
  
  // 지도를 범위에 맞춤
  map.fitBounds(bounds);
  
  // 줌 레벨이 너무 가깝거나 멀지 않도록 조정
  const listener = window.google.maps.event.addListener(map, 'idle', () => {
    if (map.getZoom() > 16) map.setZoom(16);
    window.google.maps.event.removeListener(listener);
  });
}

// ✅ mapCenter와 mapZoom 변경 감지
watch(
  [() => props.mapCenter, () => props.mapZoom],
  ([newCenter, newZoom]) => {
    if (mapRef.value && mapRef.value.$mapObject) {
      if (newCenter) {
        // 사용자 정의 애니메이션 함수를 사용하여 부드럽게 이동
        smoothlyAnimateTo(mapRef.value.$mapObject, newCenter, newZoom)
      } else if (newZoom !== undefined && newZoom !== null && !newCenter) {
        // 중앙 이동 없이 zoom만 변경할 경우
        if (mapRef.value.$mapObject.getZoom() !== newZoom) {
          mapRef.value.$mapObject.setZoom(newZoom)
        }
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
        
        // 맵 고급 애니메이션 설정 추가
        if (map.setOptions) {
          map.setOptions({
            gestureHandling: 'cooperative',
            scrollwheel: true,
            navigationControl: true,
            animatedZoom: true,
            disableDefaultUI: false,
            zoomAnimation: true,
            panAnimation: true
          })
        }
        
        if (props.currentDaySchedule && props.currentDaySchedule.length > 0) {
          // 일정이 있으면 그에 맞춰 지도 조정
          fitMapToSchedule();
        } else if (props.mapCenter) {
          if (props.mapZoom) {
            // 처음 지도 로드시 부드럽게 이동
            smoothlyAnimateTo(map, props.mapCenter, props.mapZoom)
          } else {
            smoothlyAnimateTo(map, props.mapCenter, map.getZoom())
          }
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
