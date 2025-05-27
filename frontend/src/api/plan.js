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
}
