<template>
  <div class="place-detail-card">
    <div class="card-container">
      <!-- 이미지 섹션 -->
      <div class="image-section">
        <AttractionImage 
          :imageUrl="selectedPlace.image || (selectedPlace.first_image1 || selectedPlace.first_image2)" 
          :attractionId="selectedPlace.attractionId || selectedPlace.attraction_id"
          :alt="selectedPlace.attractionName || selectedPlace.attraction_name"
          class="attraction-image"
        />
        <!-- 카테고리 뱃지 -->
        <div class="category-badge" v-if="selectedPlace.categoryName || selectedPlace.attraction_category_name">
          {{ selectedPlace.categoryName || selectedPlace.attraction_category_name }}
        </div>
      </div>
      
      <!-- 콘텐츠 섹션 -->
      <div class="content-section">
        <div class="main-info">
          <h3 class="attraction-title">{{ selectedPlace.attractionName || selectedPlace.attraction_name }}</h3>
          <div class="location-info">
            <svg class="location-icon" fill="currentColor" viewBox="0 0 20 20">
              <path fill-rule="evenodd" d="M5.05 4.05a7 7 0 119.9 9.9L10 18.9l-4.95-4.95a7 7 0 010-9.9zM10 11a2 2 0 100-4 2 2 0 000 4z" clip-rule="evenodd" />
            </svg>
            <span>{{ selectedPlace.stateName || selectedPlace.state_name }} {{ selectedPlace.cityName || selectedPlace.city_name }}</span>
          </div>
          
          <!-- 주소 정보 표시 -->
          <div v-if="selectedPlace.addr1 || selectedPlace.addr2" class="address-info">
            <svg class="address-icon" fill="currentColor" viewBox="0 0 20 20">
              <path fill-rule="evenodd" d="M6 2a1 1 0 00-1 1v1H4a2 2 0 00-2 2v10a2 2 0 002 2h12a2 2 0 002-2V6a2 2 0 00-2-2h-1V3a1 1 0 10-2 0v1H7V3a1 1 0 00-1-1zm0 5a1 1 0 000 2h8a1 1 0 100-2H6z" clip-rule="evenodd" />
            </svg>
            <span>{{ (selectedPlace.addr1 || '') + ' ' + (selectedPlace.addr2 || '') }}</span>
          </div>
        </div>
        
        <!-- 메모 표시 (일정에 있는 장소인 경우) -->
        <div v-if="isInSchedule && selectedPlace.content" class="memo-display">
          <svg class="memo-icon" fill="currentColor" viewBox="0 0 20 20">
            <path d="M13.586 3.586a2 2 0 112.828 2.828l-.793.793-2.828-2.828.793-.793zM11.379 5.793L3 14.172V17h2.828l8.38-8.379-2.83-2.828z" />
          </svg>
          <p class="memo-text">{{ selectedPlace.content }}</p>
        </div>
        
        <!-- 평점 정보 -->
        <div class="rating-section" v-if="selectedPlace.reviewAvgScore > 0">
          <div class="rating-stars">
            <span class="star-icon">⭐</span>
            <span class="rating-score">{{ selectedPlace.reviewAvgScore.toFixed(1) }}</span>
          </div>
          <span class="review-count">({{ selectedPlace.reviewCount }}개 리뷰)</span>
        </div>
        
        <!-- 액션 버튼들 -->
        <div class="action-buttons">
          <button 
            v-if="!isInSchedule"
            @click="showAddToScheduleModal = true"
            class="btn btn-primary"
          >
            <svg class="btn-icon" fill="currentColor" viewBox="0 0 20 20">
              <path fill-rule="evenodd" d="M10 3a1 1 0 011 1v5h5a1 1 0 110 2h-5v5a1 1 0 11-2 0v-5H4a1 1 0 110-2h5V4a1 1 0 011-1z" clip-rule="evenodd" />
            </svg>
            일정에 추가
          </button>
          
          <button 
            v-if="isInSchedule"
            @click="removeFromSchedule"
            class="btn btn-danger"
          >
            <svg class="btn-icon" fill="currentColor" viewBox="0 0 20 20">
              <path fill-rule="evenodd" d="M9 2a1 1 0 00-.894.553L7.382 4H4a1 1 0 000 2v10a2 2 0 002 2h8a2 2 0 002-2V6a1 1 0 100-2h-3.382l-.724-1.447A1 1 0 0011 2H9zM7 8a1 1 0 012 0v6a1 1 0 11-2 0V8zm5-1a1 1 0 00-1 1v6a1 1 0 102 0V8a1 1 0 00-1-1z" clip-rule="evenodd" />
            </svg>
            삭제
          </button>
          
          <button 
            v-if="isInSchedule"
            @click="showEditMemo()"
            class="btn btn-memo"
          >
            <svg class="btn-icon" fill="currentColor" viewBox="0 0 20 20">
              <path d="M13.586 3.586a2 2 0 112.828 2.828l-.793.793-2.828-2.828.793-.793zM11.379 5.793L3 14.172V17h2.828l8.38-8.379-2.83-2.828z" />
            </svg>
            메모
          </button>
          
          <button 
            @click="openAttractionDetail"
            class="btn btn-secondary"
          >
            <svg class="btn-icon" fill="currentColor" viewBox="0 0 20 20">
              <path d="M10 12a2 2 0 100-4 2 2 0 000 4z" />
              <path fill-rule="evenodd" d="M.458 10C1.732 5.943 5.522 3 10 3s8.268 2.943 9.542 7c-1.274 4.057-5.064 7-9.542 7S1.732 14.057.458 10zM14 10a4 4 0 11-8 0 4 4 0 018 0z" clip-rule="evenodd" />
            </svg>
            상세보기
          </button>
          
          <button 
            @click="$emit('closePlace')"
            class="btn btn-close"
          >
            <svg class="btn-icon" fill="currentColor" viewBox="0 0 20 20">
              <path fill-rule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clip-rule="evenodd" />
            </svg>
          </button>
        </div>
      </div>
    </div>
    
    <!-- 일정 추가 모달 -->
    <div v-if="showAddToScheduleModal" class="modal-overlay" @click="closeModal">
      <div class="modal-container" @click.stop>
        <div class="modal-header">
          <h3 class="modal-title">일정에 추가</h3>
          <button @click="closeModal" class="modal-close-btn">
            <svg fill="currentColor" viewBox="0 0 20 20">
              <path fill-rule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clip-rule="evenodd" />
            </svg>
          </button>
        </div>
        
        <div class="modal-body">
          <div class="selected-place-info">
            <div class="place-thumbnail">
              <AttractionImage 
                :imageUrl="selectedPlace.image || (selectedPlace.first_image1 || selectedPlace.first_image2)" 
                :attractionId="selectedPlace.attractionId || selectedPlace.attraction_id"
                :alt="selectedPlace.attractionName || selectedPlace.attraction_name"
                class="thumbnail-image"
              />
            </div>
            <div class="place-details">
              <h4 class="place-name">{{ selectedPlace.attractionName || selectedPlace.attraction_name }}</h4>
              <p class="place-location">{{ selectedPlace.stateName || selectedPlace.state_name }} {{ selectedPlace.cityName || selectedPlace.city_name }}</p>
            </div>
          </div>
          
          <div class="day-selection">
            <label class="selection-label">일정을 추가할 날짜를 선택하세요:</label>
            <div class="day-grid">
              <button 
                v-for="day in availableDays" 
                :key="day.value"
                @click="selectedDay = day.value"
                :class="['day-btn', { 'selected': selectedDay === day.value }]"
              >
                <div class="day-number">{{ day.label }}</div>
                <div class="day-date">{{ day.date }}</div>
              </button>
            </div>
          </div>
          
          <div class="memo-section">
            <label class="selection-label">메모 (선택사항):</label>
            <textarea 
              v-model="memo"
              placeholder="이 장소에 대한 메모를 작성해보세요..."
              class="memo-textarea"
              rows="3"
            ></textarea>
          </div>
        </div>
        
        <div class="modal-footer">
          <button @click="closeModal" class="btn btn-cancel">
            취소
          </button>
          <button 
            @click="addToSchedule"
            :disabled="!selectedDay"
            class="btn btn-confirm"
          >
            일정에 추가
          </button>
        </div>
      </div>
    </div>
    
    <!-- 메모 수정 모달 -->
    <div v-if="showEditMemoModal" class="modal-overlay" @click="closeEditMemoModal">
      <div class="modal-container" @click.stop>
        <div class="modal-header">
          <h3 class="modal-title">메모 수정</h3>
          <button @click="closeEditMemoModal" class="modal-close-btn">
            <svg fill="currentColor" viewBox="0 0 20 20">
              <path fill-rule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clip-rule="evenodd" />
            </svg>
          </button>
        </div>
        
        <div class="modal-body">
          <div class="selected-place-info">
            <div class="place-thumbnail">
              <AttractionImage 
                :imageUrl="selectedPlace.image || (selectedPlace.first_image1 || selectedPlace.first_image2)" 
                :attractionId="selectedPlace.attractionId || selectedPlace.attraction_id"
                :alt="selectedPlace.attractionName || selectedPlace.attraction_name"
                class="thumbnail-image"
              />
            </div>
            <div class="place-details">
              <h4 class="place-name">{{ selectedPlace.attractionName || selectedPlace.attraction_name }}</h4>
              <p class="place-location">{{ selectedPlace.stateName || selectedPlace.state_name }} {{ selectedPlace.cityName || selectedPlace.city_name }}</p>
            </div>
          </div>
          
          <div class="memo-section">
            <label class="selection-label">메모 수정:</label>
            <textarea 
              v-model="editedMemo"
              placeholder="이 장소에 대한 메모를 작성해보세요..."
              class="memo-textarea"
              rows="5"
            ></textarea>
          </div>
        </div>
        
        <div class="modal-footer">
          <button @click="closeEditMemoModal" class="btn btn-cancel">
            취소
          </button>
          <button 
            @click="updateMemo"
            class="btn btn-confirm"
          >
            저장
          </button>
        </div>
      </div>
    </div>
  </div>
</template>


<script setup>
import AttractionImage from '@/components/common/AttractionImage.vue'
import { ref, computed } from 'vue'

const props = defineProps({
  selectedPlace: Object,
  planStartDate: String,
  planEndDate: String,
  isInSchedule: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['closePlace', 'addToSchedule', 'viewDetail', 'removeFromSchedule', 'updateMemo'])

// 모달 상태
const showAddToScheduleModal = ref(false)
const showEditMemoModal = ref(false)
const selectedDay = ref(null)
const memo = ref('')
const editedMemo = ref('')

// 사용 가능한 날짜들 계산
const availableDays = computed(() => {
  if (!props.planStartDate || !props.planEndDate) {
    return []
  }
  
  const startDate = new Date(props.planStartDate)
  const endDate = new Date(props.planEndDate)
  const days = []
  
  const currentDate = new Date(startDate)
  let dayNumber = 1
  
  while (currentDate <= endDate) {
    days.push({
      value: new Date(currentDate).toISOString().split('T')[0],
      label: `${dayNumber}일차`,
      date: formatDate(currentDate)
    })
    
    currentDate.setDate(currentDate.getDate() + 1)
    dayNumber++
  }
  
  return days
})

// 날짜 포맷팅 함수
const formatDate = (date) => {
  const month = date.getMonth() + 1
  const day = date.getDate()
  const weekdays = ['일', '월', '화', '수', '목', '금', '토']
  const weekday = weekdays[date.getDay()]
  
  return `${month}/${day}(${weekday})`
}

// 모달 닫기
const closeModal = () => {
  showAddToScheduleModal.value = false
  selectedDay.value = null
  memo.value = ''
}

// 메모 수정 모달 닫기
const closeEditMemoModal = () => {
  showEditMemoModal.value = false
  editedMemo.value = ''
}

// 일정에 추가
const addToSchedule = () => {
  if (!selectedDay.value) return
  
  emit('addToSchedule', {
    place: props.selectedPlace,
    date: selectedDay.value,
    memo: memo.value
  })
  
  closeModal()
}

// 일정에서 삭제
const removeFromSchedule = () => {
  emit('removeFromSchedule', props.selectedPlace)
}

// 메모 수정 모달 열기
const showEditMemo = () => {
  editedMemo.value = props.selectedPlace.content || ''
  showEditMemoModal.value = true
}

// 메모 업데이트
const updateMemo = () => {
  emit('updateMemo', {
    place: props.selectedPlace,
    memo: editedMemo.value
  })
  
  // 현재 컴포넌트의 선택된 장소 메모도 업데이트
  if (props.selectedPlace) {
    props.selectedPlace.content = editedMemo.value
  }
  
  closeEditMemoModal()
}

// 관광지 상세 페이지를 새 탭으로 열기
const openAttractionDetail = () => {
  const attractionId = props.selectedPlace.attractionId || props.selectedPlace.attraction_id
  if (attractionId) {
    const url = `/attraction/${attractionId}`
    window.open(url, '_blank')
  }
}
</script>

<style scoped>
.place-detail-card {
  position: absolute;
  bottom: 20px;
  left: 20px;
  width: 350px;
  max-width: 90vw;
  z-index: 10;
}

.card-container {
  background: linear-gradient(135deg, #ffffff 0%, #f8fafc 100%);
  border-radius: 20px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
  overflow: hidden;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

/* 이미지 섹션 */
.image-section {
  position: relative;
  height: 150px;
  overflow: hidden;
}

.attraction-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.attraction-image:hover {
  transform: scale(1.05);
}

.category-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  background: linear-gradient(135deg, #00edb3 0%, #00c297 100%);
  color: white;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 600;
  box-shadow: 0 4px 12px rgba(0, 237, 179, 0.3);
  backdrop-filter: blur(10px);
}

/* 콘텐츠 섹션 */
.content-section {
  padding: 16px;
}

.main-info {
  margin-bottom: 12px;
}

.attraction-title {
  font-size: 1.2rem;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 6px 0;
  line-height: 1.3;
}

.location-info {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #64748b;
  font-size: 0.95rem;
}

.location-icon {
  width: 16px;
  height: 16px;
  color: #ef4444;
}

/* 주소 정보 */
.address-info {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #64748b;
  font-size: 0.9rem;
  margin-top: 4px;
  line-height: 1.4;
}

.address-icon {
  width: 16px;
  height: 16px;
  color: #6b7280;
  flex-shrink: 0;
}

.address-info span {
  word-break: break-all;
}

/* 메모 표시 */
.memo-display {
  margin: 10px 0 16px 0;
  padding: 10px 12px;
  background-color: #f1f5f9;
  border-radius: 10px;
  display: flex;
  align-items: flex-start;
  gap: 8px;
}

.memo-icon {
  width: 18px;
  height: 18px;
  color: #6fbbff;
  flex-shrink: 0;
  margin-top: 2px;
}

.memo-text {
  margin: 0;
  font-size: 0.9rem;
  color: #475569;
  line-height: 1.4;
  word-break: break-word;
}

/* 평점 섹션 */
.rating-section {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
  padding: 8px;
  background: linear-gradient(135deg, #fff9c4 0%, #ffd900 30%);
  border-radius: 12px;
  border: 1px solid #ffd900;
}

.rating-stars {
  display: flex;
  align-items: center;
  gap: 4px;
}

.star-icon {
  font-size: 1.1rem;
}

.rating-score {
  font-weight: 700;
  color: #b45309;
  font-size: 1rem;
}

.review-count {
  color: #b45309;
  font-size: 0.85rem;
  font-weight: 500;
}

/* 액션 버튼들 */
.action-buttons {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  align-items: center;
}

.btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  border: none;
  border-radius: 12px;
  font-weight: 600;
  font-size: 0.85rem;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  position: relative;
  overflow: hidden;
}

.btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s;
}

.btn:hover::before {
  left: 100%;
}

.btn-icon {
  width: 16px;
  height: 16px;
}

.btn-primary {
  background: linear-gradient(135deg, #00edb3 0%, #00c297 100%);
  color: white;
  flex: 1;
}

.btn-primary:hover {
  background: linear-gradient(135deg, #00c297 0%, #00a080 100%);
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(0, 237, 179, 0.4);
}

.btn-secondary {
  background: linear-gradient(135deg, #6fbbff 0%, #4a9ae1 100%);
  color: white;
  flex: 1;
}

.btn-secondary:hover {
  background: linear-gradient(135deg, #4a9ae1 0%, #3b82f6 100%);
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(111, 187, 255, 0.4);
}

.btn-close {
  background: linear-gradient(135deg, #f1f5f9 0%, #e2e8f0 100%);
  color: #64748b;
  padding: 8px;
  min-width: 40px;
  justify-content: center;
}

.btn-close:hover {
  background: linear-gradient(135deg, #ff80cf 0%, #e667b8 100%);
  color: white;
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(255, 128, 207, 0.4);
}

.btn-danger {
  background: linear-gradient(135deg, #ff6b6b 0%, #e53e3e 100%);
  color: white;
}

.btn-danger:hover {
  background: linear-gradient(135deg, #e53e3e 0%, #c53030 100%);
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(229, 62, 62, 0.4);
}

.btn-memo {
  background: linear-gradient(135deg, #a3bffa 0%, #7f9cf5 100%);
  color: white;
}

.btn-memo:hover {
  background: linear-gradient(135deg, #7f9cf5 0%, #667eea 100%);
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(127, 156, 245, 0.4);
}

/* 모달 스타일 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  backdrop-filter: blur(4px);
  animation: fadeIn 0.3s ease-out;
}

.modal-container {
  background: white;
  border-radius: 20px;
  width: 90%;
  max-width: 500px;
  max-height: 90vh;
  overflow: hidden;
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.25);
  animation: modalSlideUp 0.3s ease-out;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #e2e8f0;
  background: linear-gradient(135deg, #f8fafc 0%, #ffffff 100%);
}

.modal-title {
  font-size: 1.25rem;
  font-weight: 700;
  color: #1e293b;
  margin: 0;
}

.modal-close-btn {
  background: none;
  border: none;
  color: #64748b;
  cursor: pointer;
  padding: 4px;
  border-radius: 8px;
  transition: all 0.2s ease;
}

.modal-close-btn:hover {
  background: #f1f5f9;
  color: #ef4444;
}

.modal-close-btn svg {
  width: 20px;
  height: 20px;
}

.modal-body {
  padding: 24px;
  overflow-y: auto;
  max-height: calc(90vh - 140px);
}

/* 선택된 장소 정보 */
.selected-place-info {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
  padding: 16px;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  border-radius: 16px;
  border: 1px solid #e2e8f0;
}

.place-thumbnail {
  flex-shrink: 0;
}

.thumbnail-image {
  width: 80px;
  height: 80px;
  border-radius: 12px;
  object-fit: cover;
}

.place-details {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.place-name {
  font-size: 1.1rem;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 4px 0;
}

.place-location {
  color: #64748b;
  font-size: 0.9rem;
  margin: 0;
}

/* 날짜 선택 */
.day-selection {
  margin-bottom: 24px;
}

.selection-label {
  display: block;
  font-weight: 600;
  color: #374151;
  margin-bottom: 12px;
  font-size: 0.95rem;
}

.day-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(100px, 1fr));
  gap: 8px;
}

.day-btn {
  background: linear-gradient(135deg, #ffffff 0%, #f8fafc 100%);
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  padding: 12px 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  text-align: center;
  position: relative;
  overflow: hidden;
}

.day-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(0, 237, 179, 0.1), transparent);
  transition: left 0.5s;
}

.day-btn:hover::before {
  left: 100%;
}

.day-btn:hover {
  border-color: #00edb3;
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(0, 237, 179, 0.2);
}

.day-btn.selected {
  background: linear-gradient(135deg, #00edb3 0%, #00c297 100%);
  border-color: #00c297;
  color: white;
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(0, 237, 179, 0.4);
}

.day-number {
  font-weight: 700;
  font-size: 0.9rem;
  margin-bottom: 2px;
}

.day-date {
  font-size: 0.8rem;
  opacity: 0.8;
}

/* 메모 섹션 */
.memo-section {
  margin-bottom: 24px;
}

.memo-textarea {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  font-family: inherit;
  font-size: 0.9rem;
  resize: vertical;
  transition: all 0.3s ease;
  background: linear-gradient(135deg, #ffffff 0%, #fafbfc 100%);
}

.memo-textarea:focus {
  outline: none;
  border-color: #00edb3;
  box-shadow: 0 0 0 3px rgba(0, 237, 179, 0.1);
}

.memo-textarea::placeholder {
  color: #9ca3af;
}

/* 모달 푸터 */
.modal-footer {
  display: flex;
  gap: 12px;
  padding: 20px 24px;
  border-top: 1px solid #e2e8f0;
  background: linear-gradient(135deg, #f8fafc 0%, #ffffff 100%);
}

.btn-cancel {
  flex: 1;
  background: linear-gradient(135deg, #f1f5f9 0%, #e2e8f0 100%);
  color: #64748b;
  border: 2px solid #e2e8f0;
  padding: 12px 20px;
  border-radius: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-cancel:hover {
  background: linear-gradient(135deg, #e2e8f0 0%, #cbd5e1 100%);
  border-color: #cbd5e1;
  transform: translateY(-1px);
}

.btn-confirm {
  flex: 2;
  background: linear-gradient(135deg, #00edb3 0%, #00c297 100%);
  color: white;
  border: 2px solid #00c297;
  padding: 12px 20px;
  border-radius: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.btn-confirm::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s;
}

.btn-confirm:hover::before {
  left: 100%;
}

.btn-confirm:hover:not(:disabled) {
  background: linear-gradient(135deg, #00c297 0%, #00a080 100%);
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(0, 237, 179, 0.4);
}

.btn-confirm:disabled {
  background: linear-gradient(135deg, #cbd5e1 0%, #94a3b8 100%);
  border-color: #cbd5e1;
  cursor: not-allowed;
  transform: none;
}

/* 애니메이션 */
.card-container {
  animation: slideUp 0.3s ease-out;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes modalSlideUp {
  from {
    opacity: 0;
    transform: translateY(50px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

/* 반응형 - 카드 */
@media (max-width: 640px) {
  .place-detail-card {
    width: calc(100vw - 20px);
    left: 10px;
    bottom: 10px;
  }
  
  .card-container {
    border-radius: 16px;
  }
  
  .content-section {
    padding: 12px;
  }
  
  .attraction-title {
    font-size: 1.1rem;
  }
  
  .action-buttons {
    flex-direction: column;
    gap: 6px;
  }
  
  .btn-primary,
  .btn-secondary,
  .btn-danger,
  .btn-memo {
    width: 100%;
    justify-content: center;
  }
  
  .btn-close {
    width: 100%;
  }
  
  .image-section {
    height: 120px;
  }
}

/* 반응형 - 모달 */
@media (max-width: 640px) {
  .modal-container {
    width: 95%;
    margin: 20px;
  }
  
  .modal-header,
  .modal-footer {
    padding: 16px 20px;
  }
  
  .modal-body {
    padding: 20px;
  }
  
  .day-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .selected-place-info {
    flex-direction: column;
    text-align: center;
  }
  
  .place-thumbnail {
    align-self: center;
  }
  
  .modal-footer {
    flex-direction: column;
  }
  
  .btn-cancel,
  .btn-confirm {
    width: 100%;
  }
}
</style>