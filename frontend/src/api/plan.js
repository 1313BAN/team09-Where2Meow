import { localAxios } from '@/utils/http-commons'

const planAPI = localAxios()

// 여행 계획 리스트 조회 (좋아요 순으로 정렬)
const getPopularPlans = (success, fail) =>
  planAPI.get('/api/plan', {
    params: {
      sort: 'likeCount',
      direction: 'desc',
      size: 12
    }
  }).then(success).catch(fail)

// 여행 계획 상세 조회
const getPlanDetail = (planId, success, fail) =>
  planAPI.get(`/api/plan/${planId}`).then(success).catch(fail)

// 사용자 여행 계획 리스트 조회
const getUserPlans = (success, fail) =>
  planAPI.get('/api/plan/user').then(success).catch(fail)

// 여행 계획 생성
const createPlan = (planData, success, fail) =>
  planAPI.post('/api/plan', planData).then(success).catch(fail)

// 여행 계획 수정
const updatePlan = (planId, planData, success, fail) =>
  planAPI.put(`/api/plan/${planId}`, planData).then(success).catch(fail)

// 여행 계획 삭제
const deletePlan = (planId, success, fail) =>
  planAPI.delete(`/api/plan/${planId}`).then(success).catch(fail)

// 여행 계획 좋아요 추가
const likePlan = (planId, success, fail) =>
  planAPI.post(`/api/plan/${planId}/like`).then(success).catch(fail)

// 여행 계획 좋아요 삭제
const unlikePlan = (planId, success, fail) =>
  planAPI.delete(`/api/plan/${planId}/like`).then(success).catch(fail)

// 여행 계획 북마크 추가
const bookmarkPlan = (planId, success, fail) =>
  planAPI.post(`/api/plan/${planId}/bookmark`).then(success).catch(fail)

// 여행 계획 북마크 삭제
const unbookmarkPlan = (planId, success, fail) =>
  planAPI.delete(`/api/plan/${planId}/bookmark`).then(success).catch(fail)

const planAPiClient = localAxios()

const planService = {
  /**
   * 여행 계획 리스트 조회 (공개 + 본인 private 계획)
   * @returns {Promise} 여행 계획 리스트
   */
  getAllPlans() {
    return planAPiClient.get('/api/plan')
  },

  /**
   * 사용자가 생성한 여행 계획 리스트 조회
   * @returns {Promise} 사용자 여행 계획 리스트
   */
  getUserPlans() {
    return planAPiClient.get('/api/plan/user')
  },

  /**
   * 여행 계획 상세 조회
   * @param {number} planId - 여행 계획 ID
   * @returns {Promise} 여행 계획 상세 정보
   */
  getPlanDetail(planId) {
    return planAPiClient.get(`/api/plan/${planId}`)
  },

  /**
   * 여행 계획 생성
   * @param {Object} planData - 여행 계획 데이터
   * @returns {Promise} 생성된 여행 계획 정보
   */
  createPlan(planData) {
    return planAPiClient.post('/api/plan', planData)
  },

  /**
   * 여행 계획 수정
   * @param {number} planId - 여행 계획 ID
   * @param {Object} planData - 수정할 여행 계획 데이터
   * @returns {Promise} 수정된 여행 계획 정보
   */
  updatePlan(planId, planData) {
    return planAPiClient.put(`/api/plan/${planId}`, planData)
  },

  /**
   * 여행 계획 삭제
   * @param {number} planId - 여행 계획 ID
   * @returns {Promise} 삭제 결과
   */
  deletePlan(planId) {
    return planAPiClient.delete(`/api/plan/${planId}`)
  },

  /**
   * 여행 계획 좋아요 추가
   * @param {number} planId - 여행 계획 ID
   * @returns {Promise} 좋아요 추가 결과
   */
  addLike(planId) {
    return planAPiClient.post(`/api/plan/${planId}/like`)
  },

  /**
   * 여행 계획 좋아요 삭제
   * @param {number} planId - 여행 계획 ID
   * @returns {Promise} 좋아요 삭제 결과
   */
  removeLike(planId) {
    return planAPiClient.delete(`/api/plan/${planId}/like`)
  },

  /**
   * 여행 계획 북마크 추가
   * @param {number} planId - 여행 계획 ID
   * @returns {Promise} 북마크 추가 결과
   */
  addBookmark(planId) {
    return planAPiClient.post(`/api/plan/${planId}/bookmark`)
  },

  /**
   * 여행 계획 북마크 삭제
   * @param {number} planId - 여행 계획 ID
   * @returns {Promise} 북마크 삭제 결과
   */
  removeBookmark(planId) {
    return planAPiClient.delete(`/api/plan/${planId}/bookmark`)
  },

  /**
   * 북마크된 여행 계획 목록 조회
   * @returns {Promise} 북마크된 여행 계획 리스트
   */
  getBookmarkedPlans() {
    return planAPiClient.get('/api/plan/bookmarks')
  }
}

export { planService }


export default {
  getPopularPlans,
  getPlanDetail,
  getUserPlans,
  createPlan,
  updatePlan,
  deletePlan,
  likePlan,
  unlikePlan,
  bookmarkPlan,
  unbookmarkPlan,
  planService
}
