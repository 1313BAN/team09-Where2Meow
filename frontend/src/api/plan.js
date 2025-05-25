import { localAxios } from '@/utils/http-commons'

const planAPiClient = localAxios()

// 요청 인터셉터 (인증 토큰 추가)
planAPiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 응답 인터셉터 (에러 처리)
planAPiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401 || error.response?.status === 403) {
      // 인증 실패 시 로그인 페이지로 리다이렉트
      localStorage.removeItem('accessToken')
      // Vue Router를 사용한 안전한 네비게이션
      if (window.app && window.app.$router) {
        window.app.$router.push('/login')
      } else {
        window.location.href = '/login'
      }
    }
    return Promise.reject(error)
  }
)

export const planService = {
  /**
   * 여행 계획 리스트 조회 (공개 + 본인 private 계획)
   * @returns {Promise} 여행 계획 리스트
   */
  getAllPlans() {
    return planAPiClient.get('/plan')
  },

  /**
   * 사용자가 생성한 여행 계획 리스트 조회
   * @returns {Promise} 사용자 여행 계획 리스트
   */
  getUserPlans() {
    return planAPiClient.get('/plan/user')
  },

  /**
   * 여행 계획 상세 조회
   * @param {number} planId - 여행 계획 ID
   * @returns {Promise} 여행 계획 상세 정보
   */
  getPlanDetail(planId) {
    return planAPiClient.get(`/plan/${planId}`)
  },

  /**
   * 여행 계획 생성
   * @param {Object} planData - 여행 계획 데이터
   * @returns {Promise} 생성된 여행 계획 정보
   */
  createPlan(planData) {
    return planAPiClient.post('/plan', planData)
  },

  /**
   * 여행 계획 수정
   * @param {number} planId - 여행 계획 ID
   * @param {Object} planData - 수정할 여행 계획 데이터
   * @returns {Promise} 수정된 여행 계획 정보
   */
  updatePlan(planId, planData) {
    return planAPiClient.put(`/plan/${planId}`, planData)
  },

  /**
   * 여행 계획 삭제
   * @param {number} planId - 여행 계획 ID
   * @returns {Promise} 삭제 결과
   */
  deletePlan(planId) {
    return planAPiClient.delete(`/plan/${planId}`)
  },

  /**
   * 여행 계획 좋아요 추가
   * @param {number} planId - 여행 계획 ID
   * @returns {Promise} 좋아요 추가 결과
   */
  addLike(planId) {
    return planAPiClient.post(`/plan/${planId}/like`)
  },

  /**
   * 여행 계획 좋아요 삭제
   * @param {number} planId - 여행 계획 ID
   * @returns {Promise} 좋아요 삭제 결과
   */
  removeLike(planId) {
    return planAPiClient.delete(`/plan/${planId}/like`)
  },

  /**
   * 여행 계획 북마크 추가
   * @param {number} planId - 여행 계획 ID
   * @returns {Promise} 북마크 추가 결과
   */
  addBookmark(planId) {
    return planAPiClient.post(`/plan/${planId}/bookmark`)
  },

  /**
   * 여행 계획 북마크 삭제
   * @param {number} planId - 여행 계획 ID
   * @returns {Promise} 북마크 삭제 결과
   */
  removeBookmark(planId) {
    return planAPiClient.delete(`/plan/${planId}/bookmark`)
  },

  /**
   * 북마크된 여행 계획 목록 조회
   * @returns {Promise} 북마크된 여행 계획 리스트
   */
  getBookmarkedPlans() {
    return planAPiClient.get('/plan/bookmarks')
  }
}

export default planService
