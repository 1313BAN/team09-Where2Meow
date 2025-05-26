<template>
  <div class="search-results">
    <!-- 검색 결과가 없을 때 -->
    <div v-if="!isSearching && searchResults.length === 0" class="no-results">
      <div class="no-results-icon">🔍</div>
      <p class="no-results-text">검색 결과가 없습니다.</p>
      <p class="no-results-subtext">다른 키워드로 검색해보세요.</p>
    </div>

    <!-- 검색 결과 목록 -->
    <div 
      v-for="(attraction, index) in searchResults" 
      :key="attraction.attractionId"
      @click="$emit('selectPlace', attraction, index + 1)"
      class="result-item"
      :class="{ 'selected': selectedPlace?.attractionId === attraction.attractionId }"
    >
      <!-- ✅ 순번 표시 추가 -->
      <div class="item-number">{{ index + 1 }}</div>
      
      <div class="item-image-container">
        <!-- AttractionImage 컴포넌트 사용 -->
        <AttractionImage 
          :imageUrl="attraction.image" 
          class="item-thumbnail"
          :alt="attraction.attractionName"
        />
        <div v-if="attraction.categoryName" class="category-badge">
          {{ attraction.categoryName }}
        </div>
      </div>
      
      <div class="item-details">
        <div class="item-title">{{ attraction.attractionName }}</div>
        <div class="item-location">
          <svg class="location-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"/>
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"/>
          </svg>
          {{ attraction.stateName }} {{ attraction.cityName }}
        </div>
        
        <div class="item-rating" v-if="attraction.reviewCount > 0">
          <div class="rating-stars">
            <span class="star">⭐</span>
            <span class="rating-score">{{ attraction.reviewAvgScore.toFixed(1) }}</span>
          </div>
          <span class="review-count">({{ attraction.reviewCount }}개 리뷰)</span>
        </div>
        <div v-else class="item-rating">
          <span class="no-review">리뷰 없음</span>
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

    <!-- 더보기 버튼 -->
    <div v-if="hasMoreResults && !isSearching && searchResults.length > 0" class="load-more-container">
      <button @click="$emit('loadMoreResults')" class="load-more-button">
        <svg class="load-more-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"/>
        </svg>
        더 많은 결과 보기
      </button>
    </div>

    <!-- 결과 끝 표시 -->
    <div v-if="!hasMoreResults && !isSearching && searchResults.length > 0" class="end-of-results">
      <p>모든 검색 결과를 확인했습니다.</p>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import AttractionImage from '@/components/common/AttractionImage.vue'

const props = defineProps({
  searchResults: Array,
  isSearching: Boolean,
  hasMoreResults: Boolean,
  selectedPlace: Object  // ✅ 추가
})

const emit = defineEmits(['selectPlace', 'loadMoreResults'])
</script>

<style scoped>
.search-results {
  flex: 1;
  overflow-y: auto;
  padding: 15px 15px 0 15px;
  background-color: #fafafa;
}

.search-results::-webkit-scrollbar {
  width: 6px;
}

.search-results::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.search-results::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.search-results::-webkit-scrollbar-thumb:hover {
  background: #a1a1a1;
}

/* 검색 결과 없음 스타일 */
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

.result-item:last-child {
  margin-bottom: 0;
}

/* ✅ 순번 스타일 추가 */
.item-number {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  background-color: #00EDB3;
  color: white;
  border-radius: 50%;
  font-weight: bold;
  font-size: 14px;
  margin-right: 12px;
  flex-shrink: 0;
  align-self: flex-start;
  margin-top: 4px;
}

/* ✅ 선택된 아이템의 순번 스타일 */
.result-item.selected .item-number {
  background-color: #FF6B6B;
  box-shadow: 0 2px 8px rgba(255, 107, 107, 0.3);
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
  background-color: #00EDB3;
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
}

.location-icon {
  width: 14px;
  height: 14px;
  margin-right: 4px;
  color: #999;
}

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

/* 결과 끝 표시 */
.end-of-results {
  padding: 20px;
  text-align: center;
  color: #999;
  font-size: 14px;
  border-top: 1px solid #eee;
  margin-top: 20px;
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
