import { localAxios } from '@/utils/http-commons'

const reviewAPI = localAxios()

// 사용자의 리뷰 목록 조회
const getUserReviews = (uuid, success, fail) => {
  return reviewAPI.get('/api/review/user', {
    params: { uuid }
  }).then(success).catch(fail)
}

// 리뷰 상세 조회
const getReviewDetail = (reviewId, uuid, success, fail) => {
  return reviewAPI.get(`/api/review/${reviewId}`, {
    params: { uuid }
  }).then(success).catch(fail)
}

// 특정 여행지의 리뷰 목록 조회 (페이징)
const getReviewsByAttraction = (attractionId, uuid, page = 0, size = 10, success, fail) => {
  const params = {
    attractionId,
    uuid,
    page,
    size,
    sort: 'createdAt,desc'
  }
  
  return reviewAPI.get('/api/review', {
    params
  }).then(success).catch(fail)
}

// 리뷰 생성
const createReview = (reviewData, success, fail) => {
  console.log('리뷰 생성 API 호출:', reviewData)
  return reviewAPI.post('/api/review', reviewData)
    .then(success)
    .catch((error) => {
      console.error('리뷰 생성 API 에러:', error)
      if (fail) fail(error)
    })
}

// 리뷰 수정
const updateReview = (reviewId, reviewData, success, fail) => {
  console.log('리뷰 수정 API 호출:', { reviewId, reviewData })
  return reviewAPI.put(`/api/review/${reviewId}`, reviewData)
    .then(success)
    .catch((error) => {
      console.error('리뷰 수정 API 에러:', error)
      if (fail) fail(error)
    })
}

// 리뷰 삭제
const deleteReview = (reviewId, uuid, success, fail) => {
  return reviewAPI.delete(`/api/review/${reviewId}`, {
    params: { uuid }
  }).then(success).catch(fail)
}

// 리뷰 좋아요 토글
const toggleReviewLike = (reviewId, uuid, success, fail) => {
  return reviewAPI.post('/api/review/like', {
    reviewId,
    uuid
  }).then(success).catch(fail)
}

// 리뷰 수정 (대안: 쿼리 파라미터로 UUID 전달)
const updateReviewWithParams = (reviewId, reviewData, uuid, success, fail) => {
  console.log('리뷰 수정 API 호출 (파라미터 방식):', { reviewId, reviewData, uuid })
  return reviewAPI.put(`/api/review/${reviewId}`, reviewData, {
    params: { uuid }
  })
    .then(success)
    .catch((error) => {
      console.error('리뷰 수정 API 에러:', error)
      if (fail) fail(error)
    })
}

export default {
  getUserReviews,
  getReviewDetail,
  getReviewsByAttraction,
  createReview,
  updateReview,
  updateReviewWithParams, // 대안 함수 추가
  deleteReview,
  toggleReviewLike
}
