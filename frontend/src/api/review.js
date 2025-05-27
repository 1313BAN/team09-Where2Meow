import { localAxios } from '@/utils/http-commons'

const reviewAPI = localAxios()

// 사용자의 리뷰 목록 조회
const getUserReviews = (uuid, success, fail) => {
  return reviewAPI.get('/api/review/user', {
    data: { uuid }
  }).then(success).catch(fail)
}

// 리뷰 상세 조회
const getReviewDetail = (reviewId, uuid, success, fail) => {
  return reviewAPI.get(`/api/review/${reviewId}`, {
    data: { uuid }
  }).then(success).catch(fail)
}

// 특정 여행지의 리뷰 목록 조회 (페이징)
const getReviewsByAttraction = (attractionId, uuid, page = 0, size = 10, success, fail) => {
  const params = {
    page,
    size,
    sort: 'createdAt,desc'
  }
  
  return reviewAPI.get('/api/review', {
    params,
    data: {
      attractionId,
      uuid
    }
  }).then(success).catch(fail)
}

// 리뷰 생성
const createReview = (reviewData, success, fail) => {
  return reviewAPI.post('/api/review', reviewData).then(success).catch(fail)
}

// 리뷰 수정
const updateReview = (reviewId, reviewData, success, fail) => {
  return reviewAPI.put(`/api/review/${reviewId}`, reviewData).then(success).catch(fail)
}

// 리뷰 삭제
const deleteReview = (reviewId, uuid, success, fail) => {
  return reviewAPI.delete(`/api/review/${reviewId}`, {
    data: { uuid }
  }).then(success).catch(fail)
}

// 리뷰 좋아요 토글
const toggleReviewLike = (reviewId, uuid, success, fail) => {
  return reviewAPI.post('/api/review/like', {
    reviewId,
    uuid
  }).then(success).catch(fail)
}

export default {
  getUserReviews,
  getReviewDetail,
  getReviewsByAttraction,
  createReview,
  updateReview,
  deleteReview,
  toggleReviewLike
}
