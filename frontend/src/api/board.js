import { localAxios } from '@/utils/http-commons'

const boardAPI = localAxios()

// 게시글 리스트 조회 (좋아요 순으로 정렬)
const getPopularBoards = (success, fail) =>
  boardAPI.get('/api/board', {
    params: {
      sort: 'likeCount',
      direction: 'desc',
      size: 12
    }
  }).then(success).catch(fail)

// 게시글 상세 조회
const getBoardDetail = (boardId, success, fail) =>
  boardAPI.get(`/api/board/${boardId}`).then(success).catch(fail)

// 사용자 게시글 리스트 조회
const getUserBoards = (success, fail) =>
  boardAPI.get('/api/board/user').then(success).catch(fail)

// 게시글 검색
const searchBoards = (keyword, sort = 'likeCount', direction = 'desc', page = 0, size = 12, success, fail) =>
  boardAPI.get('/api/board/search', {
    params: {
      keyword,
      sort,
      direction,
      page,
      size
    }
  }).then(success).catch(fail)

// 게시글 생성
const createBoard = (boardData, success, fail) =>
  boardAPI.post('/api/board', boardData).then(success).catch(fail)

// 게시글 수정
const updateBoard = (boardId, boardData, success, fail) =>
  boardAPI.put(`/api/board/${boardId}`, boardData).then(success).catch(fail)

// 게시글 삭제
const deleteBoard = (boardId, success, fail) =>
  boardAPI.delete(`/api/board/${boardId}`).then(success).catch(fail)

// 게시글 좋아요 추가
const likeBoard = (boardId, success, fail) =>
  boardAPI.post(`/api/board/${boardId}/like`).then(success).catch(fail)

// 게시글 좋아요 삭제
const unlikeBoard = (boardId, success, fail) =>
  boardAPI.delete(`/api/board/${boardId}/like`).then(success).catch(fail)

// 게시글 북마크 추가
const bookmarkBoard = (boardId, success, fail) =>
  boardAPI.post(`/api/board/${boardId}/bookmark`).then(success).catch(fail)

// 게시글 북마크 삭제
const unbookmarkBoard = (boardId, success, fail) =>
  boardAPI.delete(`/api/board/${boardId}/bookmark`).then(success).catch(fail)

// 게시글 댓글 조회
const getBoardComments = (boardId, success, fail) =>
  boardAPI.get(`/api/board/${boardId}/comment`).then(success).catch(fail)

export default {
  getPopularBoards,
  getBoardDetail,
  getUserBoards,
  searchBoards,
  createBoard,
  updateBoard,
  deleteBoard,
  likeBoard,
  unlikeBoard,
  bookmarkBoard,
  unbookmarkBoard,
  getBoardComments,
}
