import { localAxios } from '@/utils/http-commons'
import { getFullImageUrl } from '@/utils/image-utils'

const attractionApiClient = localAxios()

// 이미지 URL이 상대 경로인 경우 전체 URL로 변환
// 이미 utils/image-utils.js에 구현되어 있음

export const attractionApi = {
  /**
   * 관광지 검색
   * @param {Object} params - 검색 파라미터
   * @param {number} params.countryId - 국가 ID
   * @param {number} params.stateId - 시도 ID
   * @param {number} params.cityId - 시군구 ID
   * @param {number} params.categoryId - 카테고리 ID
   * @param {string} params.keyword - 검색어
   * @param {number} params.page - 페이지 번호 (0부터 시작)
   * @param {number} params.size - 페이지 크기
   * @returns {Promise} 검색 결과
   */
  searchAttractions(params = {}) {
    const searchParams = new URLSearchParams()

    if (params.countryId) searchParams.append('countryId', params.countryId)
    if (params.stateId) searchParams.append('stateId', params.stateId)
    if (params.cityId) searchParams.append('cityId', params.cityId)
    if (params.categoryId) searchParams.append('categoryId', params.categoryId)
    if (params.keyword) searchParams.append('keyword', params.keyword)
    if (params.page !== undefined) searchParams.append('page', params.page)
    if (params.size) searchParams.append('size', params.size)
    if (params.sort) searchParams.append('sort', params.sort)  // sort 파라미터 추가

    return attractionApiClient
      .get(`/api/attraction?${searchParams.toString()}`)
      .then((response) => {

        return response
      })
  },

  /**
   * 관광지 상세 정보 조회
   * @param {number} attractionId - 관광지 ID
   * @returns {Promise} 관광지 상세 정보
   */
  getAttractionDetail(attractionId) {
    return attractionApiClient.get(`/api/attraction/detail/${attractionId}`).then((response) => {
      return response
    })
  },

  /**
   * 모든 카테고리 목록 조회
   */
  getAllCategories() {
    return attractionApiClient.get('/api/attraction/categories')
  },

  /**
   * 관광지 이미지 URL 조회
   * @param {number} attractionId - 관광지 ID
   * @returns {Promise} 이미지 URL
   */
  getAttractionImageUrl(attractionId) {
    console.log('관광지 이미지 URL API 호출:', attractionId)
    return attractionApiClient.get(`/api/attraction/${attractionId}/image`)
      .then(response => {
        console.log('관광지 이미지 URL API 응답:', response)
        console.log('응답 데이터:', response.data)
        return response.data // 직접 URL 문자열 반환
      })
      .catch(error => {
        console.error('관광지 이미지 URL API 에러:', error)
        throw error
      })
  },
}

const attractionAPI = localAxios()

// 여행지 리스트 페이징 조회
const getAttractionListPaging = (
  {
    countryId,
    stateId,
    cityId,
    categoryId,
    keyword,
    page = 0,
    size = 10,
    sort = 'attractionName,asc',
  },
  success,
  fail,
) => {
  const params = {
    ...(countryId && { countryId }),
    ...(stateId && { stateId }),
    ...(cityId && { cityId }),
    ...(categoryId && { categoryId }),
    ...(keyword && { keyword }),
    page,
    size,
    sort,
  }

  return attractionAPI.get(`/api/attraction`, { params }).then(success).catch(fail)
}

// 여행지 상세 정보 조회
const getAttractionDetail = (attractionId, success, fail) => {
  return attractionAPI.get(`/api/attraction/detail/${attractionId}`).then(success).catch(fail)
}

export default {
  getAttractionListPaging,
  getAttractionDetail,
  attractionApi, // 추가된 attractionApi 객체
}
