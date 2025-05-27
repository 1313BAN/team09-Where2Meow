<template>
  <div class="search-results">
    <!-- 검색 결과가 없을 때 -->
    <div v-if="!isSearching && items.length === 0" class="no-results">
      <div class="no-results-icon">🔍</div>
      <p class="no-results-text">{{ mode === 'schedule' ? '일정이 없습니다.' : '검색 결과가 없습니다.' }}</p>
      <p class="no-results-subtext">{{ mode === 'schedule' ? '검색 탭에서 장소를 검색하고 추가해보세요.' : '다른 키워드로 검색해보세요.' }}</p>
    </div>

    <!-- 일정 모드: 드래그 가능한 목록 -->
    <draggable 
      v-if="mode === 'schedule' && items.length > 0"
      v-model="draggableItems"
      item-key="attractionId"
      class="draggable-list"
      :animation="200"
      ghost-class="ghost-item"
      chosen-class="chosen-item"
      drag-class="drag-item"
      @start="onDragStart"
      @end="onDragEnd"
    >
      <template #item="{ element: item, index }">
        <div 
          @click="handleItemClick(item, index)"
          class="result-item schedule-item"
          :class="{ 'selected': isItemSelected(item), 'schedule-mode': mode === 'schedule' }"
        >
          
          <!-- 순번 표시 -->
          <div class="item-number">{{ index + 1 }}</div>
          
          <div class="item-image-container">
            <!-- AttractionImage 컴포넌트 사용 -->
            <AttractionImage 
              :imageUrl="item.image || (item.first_image1 || item.first_image2)" 
              :attractionId="item.attractionId || item.attraction_id"
              class="item-thumbnail"
              :alt="item.attractionName || item.attraction_name"
            />
            <div v-if="item.categoryName || item.attraction_category_name" class="category-badge">
              {{ item.categoryName || item.attraction_category_name }}
            </div>
          </div>
          
          <div class="item-details">
            <div class="item-title">{{ item.attractionName || item.attraction_name }}</div>
            <div class="item-location">
              <svg class="location-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"/>
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"/>
              </svg>
              <span>
                {{ 
                  (item.stateName || item.state_name) && (item.cityName || item.city_name) 
                    ? `${item.stateName || item.state_name} ${item.cityName || item.city_name}`
                    : item.location || '위치 정보 없음'
                }}
              </span>
            </div>
            
            <div v-if="item.content" class="item-memo">
              <svg class="memo-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"/>
              </svg>
              {{ item.content }}
            </div>
            
            <!-- 메모가 없는 일정 모드이거나 리뷰가 있는 경우만 평점 표시 -->
            <div v-else-if="item.reviewCount > 0" class="item-rating">
              <div class="rating-stars">
                <span class="star">⭐</span>
                <span class="rating-score">{{ 
                  item.reviewAvgScore && !isNaN(item.reviewAvgScore) 
                    ? item.reviewAvgScore.toFixed(1) 
                    : '0.0' 
                }}</span>
              </div>
              <span class="review-count">({{ item.reviewCount || 0 }}개 리뷰)</span>
            </div>
          </div>
        </div>
      </template>
    </draggable>

    <!-- 검색 모드: 일반 목록 -->
    <div 
      v-else-if="mode === 'search'"
      v-for="(item, index) in items" 
      :key="`${item.attractionId || item.attraction_id}-${index}`"
      @click="handleItemClick(item, index)"
      class="result-item"
      :class="{ 'selected': isItemSelected(item) }"
    >
      <!-- 순번 표시 -->
      <div class="item-number">{{ index + 1 }}</div>
      
      <div class="item-image-container">
        <!-- AttractionImage 컴포넌트 사용 -->
        <AttractionImage 
          :imageUrl="item.image || (item.first_image1 || item.first_image2)" 
          :attractionId="item.attractionId || item.attraction_id"
          class="item-thumbnail"
          :alt="item.attractionName || item.attraction_name"
        />
        <div v-if="item.categoryName || item.attraction_category_name" class="category-badge">
          {{ item.categoryName || item.attraction_category_name }}
        </div>
      </div>
      
      <div class="item-details">
        <div class="item-title">{{ item.attractionName || item.attraction_name }}</div>
        <div class="item-location">
          <svg class="location-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"/>
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"/>
          </svg>
          <span>
            {{ 
              (item.stateName || item.state_name) && (item.cityName || item.city_name) 
                ? `${item.stateName || item.state_name} ${item.cityName || item.city_name}`
                : item.location || '위치 정보 없음'
            }}
          </span>
        </div>
        
        <!-- 리뷰가 있는 경우만 평점 표시 -->
        <div v-if="item.reviewCount > 0" class="item-rating">
          <div class="rating-stars">
            <span class="star">⭐</span>
            <span class="rating-score">{{ 
              item.reviewAvgScore && !isNaN(item.reviewAvgScore) 
                ? item.reviewAvgScore.toFixed(1) 
                : '0.0' 
            }}</span>
          </div>
          <span class="review-count">({{ item.reviewCount || 0 }}개 리뷰)</span>
        </div>
      </div>
    </div>

    <!-- 로딩 중 -->
    <div v-if="isSearching" class="loading-container">
      <div class="loading-spinner">
        <div class="spinner"></div>
      </div>
      <p class="loading-text">검색 중...</p>
    </div>

    <!-- 더보기 버튼 (검색 모드에서만 표시) -->
    <div v-if="mode === 'search' && hasMoreResults && !isSearching && items.length > 0" class="load-more-container">
      <button @click="$emit('loadMoreResults')" class="load-more-button">
        <svg class="load-more-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"/>
        </svg>
        더 많은 결과 보기
      </button>
    </div>

    <!-- 결과 끝 표시 (검색 모드에서만 표시) -->
    <div v-if="mode === 'search' && !hasMoreResults && !isSearching && items.length > 0" class="end-of-results">
      <p>모든 검색 결과를 확인했습니다.</p>
    </div>
    
    <!-- 일정 추가 버튼 (일정 모드에서만 표시) -->
    <!-- 장소 추가하기 버튼 삭제 -->
  </div>
</template>

<script setup>
import { ref, watch, computed } from 'vue'
import draggable from 'vuedraggable'
import AttractionImage from '@/components/common/AttractionImage.vue'

const props = defineProps({
  // 검색 모드 관련 props
  searchResults: Array,
  isSearching: Boolean,
  hasMoreResults: Boolean,
  selectedPlace: Object,
  
  // 일정 모드 관련 props
  scheduleItems: Array,
  selectedScheduleItem: Object,
  
  // 모드 선택
  mode: {
    type: String,
    default: 'search',
    validator: (value) => ['search', 'schedule'].includes(value)
  }
})

// 모드에 따라 적절한 아이템 배열 선택
const items = computed(() => {
  const result = props.mode === 'search' ? props.searchResults || [] : props.scheduleItems || []
  
  // 디버깅용: 일정 모드일 때 데이터 출력
  if (props.mode === 'schedule' && result.length > 0) {
    console.log('일정 데이터:', result)
    console.log('첫 번째 아이템:', result[0])
  }
  
  return result
})

// 모드에 따라 적절한 선택된 아이템 반환
const selectedItem = computed(() => {
  return props.mode === 'search' ? props.selectedPlace : props.selectedScheduleItem
})

// 아이템 선택 여부 확인
function isItemSelected(item) {
  if (!selectedItem.value || !item) return false
  
  const selectedId = selectedItem.value.attractionId || selectedItem.value.attraction_id
  const itemId = item.attractionId || item.attraction_id
  
  return selectedId == itemId  // 의도적으로 느슨한 비교 사용 (== 연산자)
}

// 드래그 가능한 아이템 배열 (일정 모드에서만 사용)
const draggableItems = computed({
  get() {
    return props.mode === 'schedule' ? props.scheduleItems || [] : []
  },
  set(newItems) {
    if (props.mode === 'schedule') {
      emit('updateScheduleOrder', newItems)
    }
  }
})

// 드래그 상태
const isDragging = ref(false)

const emit = defineEmits(['selectItem', 'loadMoreResults', 'addScheduleItem', 'updateScheduleOrder'])

// 드래그 시작
const onDragStart = (event) => {
  isDragging.value = true
  console.log('드래그 시작:', event)
}

// 드래그 종료
const onDragEnd = (event) => {
  isDragging.value = false
  console.log('드래그 종료:', event)
  
  // 순서가 변경되었는지 확인
  if (event.oldIndex !== event.newIndex) {
    console.log(`아이템이 ${event.oldIndex}에서 ${event.newIndex}로 이동됨`)
    console.log('드래그 종료 후 아이템 순서:', draggableItems.value)
    // draggableItems의 setter가 자동으로 호출되어 updateScheduleOrder 이벤트가 발생함
  }
}

// ✅ 아이템 클릭 처리 함수
const handleItemClick = (item, index) => {
  // 드래그 중일 때는 클릭 이벤트 무시
  if (isDragging.value) {
    return
  }
  
  console.log(`${props.mode} 모드에서 아이템 클릭:`, item, index) // 디버깅용
  
  // 모드에 따라 이벤트 전달
  if (props.mode === 'search') {
    // 검색 모드: selectItem 이벤트 발생 (selectPlace로 연결됨)
    emit('selectItem', item, index + 1)
  } else if (props.mode === 'schedule') {
    // 일정 모드: selectItem 이벤트 발생 (selectScheduleItem으로 연결됨)
    emit('selectItem', item, index + 1)
  }
}
</script>

<style scoped>
.search-results {
  flex: 1;
  overflow-y: auto;
  padding: 15px 15px 0 15px;
  background-color: #fafafa;
  width: 100%;
  display: flex;
  flex-direction: column;
}

.search-results::-webkit-scrollbar {
  width: 8px;
}

.search-results::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

.search-results::-webkit-scrollbar-thumb {
  background: #aaa; /* 더 높은 대비 */
  border-radius: 4px;
  min-height: 20px; /* 최소 높이 보장 */
}

.search-results::-webkit-scrollbar-thumb:hover {
  background: #888; /* 호버 시 더 진한 색상 */
}

.search-results::-webkit-scrollbar-thumb:active {
  background: #666; /* 활성 상태 색상 */
}

/* 결과 없음 스타일 */
.no-results {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 300px;
  text-align: center;
  color: #666;
}

.no-results-icon {
  font-size: 48px;
  margin-bottom: 16px;
  opacity: 0.5;
}

.no-results-text {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 8px;
  color: #333;
}

.no-results-subtext {
  font-size: 14px;
  color: #999;
}

/* 검색 결과 아이템 스타일 */
.result-item {
  display: flex;
  align-items: flex-start;
  padding: 16px;
  margin-bottom: 12px;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  cursor: pointer;
  transition: all 0.2s ease;
  border: 1px solid #f0f0f0;
  position: relative;
  width: 100%; /* 너비 100% 추가 */
}

.result-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
  border-color: #00EDB3;
}

/* ✅ 선택된 아이템 스타일 추가 */
.result-item.selected {
  background-color: #e8f5e8;
  border-color: #00EDB3;
  border-width: 2px;
}

.result-item.schedule-mode {
  border-color: #6FBBFF;
}

.result-item.schedule-mode:hover {
  border-color: #6FBBFF;
}

.result-item.schedule-mode.selected {
  background-color: #e8f5ee;
  border-color: #6FBBFF;
}

.result-item:last-child {
  margin-bottom: 0;
}

/* 순번 스타일 */
.item-number {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  background-color: #ffd900;
  color: white;
  border-radius: 50%;
  font-weight: bold;
  font-size: 14px;
  margin-right: 12px;
  flex-shrink: 0;
  align-self: flex-start;
  margin-top: 4px;
}

/* 선택된 아이템의 순번 스타일 */
.result-item.selected .item-number {
  background-color: #FF6B6B;
  box-shadow: 0 2px 8px rgba(255, 107, 107, 0.3);
}

/* 일정 모드의 순번 스타일 */
.result-item.schedule-mode .item-number {
  background-color: #6FBBFF;
}

.result-item.schedule-mode.selected .item-number {
  background-color: #467eb3;
  box-shadow: 0 2px 8px rgba(111, 187, 255, 0.3);
}

/* 이미지 컨테이너 */
.item-image-container {
  position: relative;
  margin-right: 15px;
  flex-shrink: 0;
  width: 88px;
  height: 88px;
  padding: 8px 8px 0 0;
}

.item-thumbnail {
  width: 80px;
  height: 80px;
  border-radius: 8px;
}

.image-skeleton {
  width: 80px;
  height: 80px;
  border-radius: 8px;
}

.skeleton-animation {
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.4), transparent);
  animation: skeleton-loading 1.5s infinite;
}

@keyframes skeleton-loading {
  0% { left: -100%; }
  100% { left: 100%; }
}

.category-badge {
  position: absolute;
  top: 0;
  right: 0;
  background-color: #6fbbff;
  color: white;
  font-size: 10px;
  font-weight: 600;
  padding: 3px 7px;
  border-radius: 10px;
  white-space: nowrap;
  box-shadow: 0 2px 6px rgba(0, 237, 179, 0.3);
  z-index: 10;
  max-width: 70px;
  overflow: hidden;
  text-overflow: ellipsis;
  transform: translate(2px, -2px);
}

/* 아이템 상세 정보 */
.item-details {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
  justify-content: space-between;
  overflow: hidden; /* 오버플로우 처리 추가 */
}

.item-title {
  font-weight: 600;
  font-size: 15px;
  color: #333;
  margin-bottom: 6px;
  line-height: 1.3;
  word-break: break-word;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.item-location {
  display: flex;
  align-items: center;
  font-size: 13px;
  color: #666;
  margin-bottom: 8px;
  max-width: 100%; /* 최대 너비 제한 */
}

.item-location span {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  flex: 1;
}

.location-icon {
  width: 14px;
  height: 14px;
  margin-right: 4px;
  color: #999;
}

/* 리뷰 평점 스타일 */
.item-rating {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 13px;
}

.rating-stars {
  display: flex;
  align-items: center;
  gap: 4px;
}

.star {
  color: #ffa500;
  font-size: 12px;
}

.rating-score {
  font-weight: 600;
  color: #333;
}

.review-count {
  color: #999;
  font-size: 12px;
}

.no-review {
  color: #999;
  font-size: 12px;
  font-style: italic;
}

/* 일정 메모 스타일 */
.item-memo {
  display: flex;
  align-items: center;
  font-size: 13px;
  color: #555;
  font-style: italic;
  margin-top: 4px;
  line-height: 1.4;
  word-break: break-word;
  overflow: hidden;
  text-overflow: ellipsis;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
}

.memo-icon {
  width: 14px;
  height: 14px;
  margin-right: 4px;
  color: #999;
  flex-shrink: 0;
}

/* 로딩 스타일 */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  color: #666;
}

.loading-spinner {
  margin-bottom: 16px;
}

.spinner {
  width: 32px;
  height: 32px;
  border: 3px solid #f3f3f3;
  border-top: 3px solid #00EDB3;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

.loading-text {
  font-size: 14px;
  color: #666;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 더보기 버튼 */
.load-more-container {
  padding: 20px 0;
  text-align: center;
}

.load-more-button {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  width: 100%;
  padding: 12px 20px;
  background-color: #fff;
  color: #00EDB3;
  border: 2px solid #00EDB3;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

.load-more-button:hover {
  background-color: #00EDB3;
  color: white;
}

.load-more-icon {
  width: 16px;
  height: 16px;
}

/* 일정 추가 버튼 */
.add-schedule-container {
  padding: 20px 0;
  text-align: center;
}

.add-schedule-button {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  width: 100%;
  padding: 12px 20px;
  background-color: #fff;
  color: #6FBBFF;
  border: 2px solid #6FBBFF;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

.add-schedule-button:hover {
  background-color: #6FBBFF;
  color: white;
}

.add-icon {
  width: 16px;
  height: 16px;
}

/* 결과 끝 표시 */
.end-of-results {
  padding: 20px;
  text-align: center;
  color: #999;
  font-size: 14px;
  border-top: 1px solid #eee;
  margin-top: 20px;
}

/* 드래그 앤 드롭 스타일 */
.draggable-list {
  width: 100%;
}

.drag-handle {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  margin-right: 8px;
  color: #999;
  cursor: grab;
  transition: color 0.2s ease;
  flex-shrink: 0;
}

.drag-handle:hover {
  color: #6FBBFF;
}

.drag-handle:active {
  cursor: grabbing;
}

.drag-icon {
  width: 16px;
  height: 16px;
}

/* 드래그 상태 스타일 */
.ghost-item {
  opacity: 0.5;
  background-color: #f0f8ff;
  border: 2px dashed #6FBBFF;
  transform: scale(1.02);
}

.chosen-item {
  box-shadow: 0 8px 32px rgba(111, 187, 255, 0.3);
  transform: scale(1.02);
  border: 2px solid #6FBBFF;
}

.drag-item {
  opacity: 0.8;
  transform: rotate(2deg);
  z-index: 1000;
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .result-item {
    padding: 12px;
  }
  
  .item-number {
    width: 28px;
    height: 28px;
    font-size: 12px;
    margin-right: 10px;
  }
  
  .drag-handle {
    width: 20px;
    height: 20px;
    margin-right: 6px;
  }
  
  .drag-icon {
    width: 14px;
    height: 14px;
  }
  
  .item-image-container {
    width: 66px;
    height: 66px;
    padding: 6px 6px 0 0;
  }
  
  .item-thumbnail,
  .image-skeleton {
    width: 60px;
    height: 60px;
  }
  
  .category-badge {
    font-size: 9px;
    padding: 2px 5px;
    max-width: 50px;
    border-radius: 8px;
  }
  
  .item-title {
    font-size: 14px;
  }
  
  .item-location,
  .item-rating {
    font-size: 12px;
  }
}
</style>
