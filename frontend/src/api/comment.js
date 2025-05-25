import { localAxios } from '@/utils/http-commons'

const api = localAxios()

// 내가 쓴 댓글 리스트 조회
export const getUserComments = async (params = {}) => {
  try {
    const response = await api.get('/api/comment/user', { params })
    return response.data
  } catch (error) {
    throw error
  }
}

// 댓글 생성
export const createComment = async (commentData) => {
  try {
    const response = await api.post('/api/comment', commentData)
    return response.data
  } catch (error) {
    throw error
  }
}

// 댓글 수정
export const updateComment = async (commentId, commentData) => {
  try {
    const response = await api.put(`/api/comment/${commentId}`, commentData)
    return response.data
  } catch (error) {
    throw error
  }
}

// 댓글 삭제
export const deleteComment = async (commentId) => {
  try {
    const response = await api.delete(`/api/comment/${commentId}`)
    return response.data
  } catch (error) {
    throw error
  }
}

// 댓글 좋아요 추가
export const addCommentLike = async (commentId) => {
  try {
    const response = await api.post(`/api/comment/${commentId}/like`)
    return response.data
  } catch (error) {
    throw error
  }
}

// 댓글 좋아요 삭제
export const removeCommentLike = async (commentId) => {
  try {
    const response = await api.delete(`/api/comment/${commentId}/like`)
    return response.data
  } catch (error) {
    throw error
  }
}
