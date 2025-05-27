import { localAxios } from '@/utils/http-commons'

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
}
