import { localAxios } from '@/utils/http-commons'

const api = localAxios()

// 게시글 리스트 조회
export const getBoards = async (params = {}) => {
  try {
    console.log('Fetching boards with params:', params) // 디버깅
    const response = await api.get('/api/board', { params })
    console.log('Boards API response:', response.data) // 디버깅
    return response.data
  } catch (error) {
    console.error('Boards API error:', error) // 디버깅
    throw error
  }
}

// 내가 쓴 게시글 리스트 조회
export const getUserBoards = async (params = {}) => {
  try {
    const response = await api.get('/api/board/user', { params })
    return response.data
  } catch (error) {
    throw error
  }
}

// 북마크한 게시글 리스트 조회
export const getBookmarkedBoards = async (params = {}) => {
  try {
    const response = await api.get('/api/board', {
      params: { ...params, bookmarked: true },
    })
    return response.data
  } catch (error) {
    throw error
  }
}

// 게시글 상세 조회
export const getBoardDetail = async (boardId) => {
  try {
    const response = await api.get(`/api/board/${boardId}`)
    return response.data
  } catch (error) {
    throw error
  }
}

// 게시글 검색
export const searchBoards = async (keyword, params = {}) => {
  try {
    const response = await api.get('/api/board/search', {
      params: { keyword, ...params },
    })
    return response.data
  } catch (error) {
    throw error
  }
}

// 게시글 생성
export const createBoard = async (boardData) => {
  try {
    const response = await api.post('/api/board', boardData)
    return response.data
  } catch (error) {
    throw error
  }
}

// 게시글 수정
export const updateBoard = async (boardId, boardData) => {
  try {
    const response = await api.put(`/api/board/${boardId}`, boardData)
    return response.data
  } catch (error) {
    throw error
  }
}

// 게시글 삭제
export const deleteBoard = async (boardId) => {
  try {
    const response = await api.delete(`/api/board/${boardId}`)
    return response.data
  } catch (error) {
    throw error
  }
}

// 게시글 좋아요 추가
export const addBoardLike = async (boardId) => {
  try {
    const response = await api.post(`/api/board/${boardId}/like`)
    return response.data
  } catch (error) {
    throw error
  }
}

// 게시글 좋아요 삭제
export const removeBoardLike = async (boardId) => {
  try {
    const response = await api.delete(`/api/board/${boardId}/like`)
    return response.data
  } catch (error) {
    throw error
  }
}

// 게시글 북마크 추가
export const addBoardBookmark = async (boardId) => {
  try {
    const response = await api.post(`/api/board/${boardId}/bookmark`)
    return response.data
  } catch (error) {
    throw error
  }
}

// 게시글 북마크 삭제
export const removeBoardBookmark = async (boardId) => {
  try {
    const response = await api.delete(`/api/board/${boardId}/bookmark`)
    return response.data
  } catch (error) {
    throw error
  }
}

// 게시글 댓글 목록 조회
export const getBoardComments = async (boardId) => {
  try {
    const response = await api.get(`/api/board/${boardId}/comment`)
    return response.data
  } catch (error) {
    throw error
  }
}

// 인기 게시글 조회 (RecommendationSection용)
export const getPopularBoards = (successCallback, errorCallback) => {
  getBoards({ sort: 'likeCount', direction: 'desc', size: 12 })
    .then((response) => {
      console.log('Popular boards response:', response) // 디버깅용

      // 응답 구조 확인 및 안전한 처리
      let boards = []

      if (response && Array.isArray(response)) {
        boards = response
      } else if (response && response.content && Array.isArray(response.content)) {
        boards = response.content
      } else if (response && response.data && Array.isArray(response.data)) {
        boards = response.data
      } else if (response && response.boards && Array.isArray(response.boards)) {
        boards = response.boards
      } else {
        console.warn('Unexpected response structure:', response)
        boards = [] // 빈 배열로 폴백
      }

      if (successCallback) {
        successCallback({ data: { boards } })
      }
    })
    .catch((error) => {
      console.error('Popular boards fetch error:', error)

      if (errorCallback) {
        errorCallback(error)
      }
    })
}

const boardAPI = {
  getBoards,
  getUserBoards,
  getBookmarkedBoards,
  getBoardDetail,
  searchBoards,
  createBoard,
  updateBoard,
  deleteBoard,
  addBoardLike,
  removeBoardLike,
  addBoardBookmark,
  removeBoardBookmark,
  getBoardComments,
  getPopularBoards,
}

export default boardAPI
