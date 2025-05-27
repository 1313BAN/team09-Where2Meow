import { localAxios } from '@/utils/http-commons'

const addressAPI = localAxios()

// 모든 나라(country) 가져오기
const getCountries = (success, fail) =>
  addressAPI.get(`/api/address/country`).then(success).catch(fail)

// 특정 나라의 주(state) 가져오기
const getStates = (countryId, success, fail) =>
  addressAPI.get(`/api/address/state`, { params: { countryId } }).then(success).catch(fail)

// 특정 주의 도시(city) 가져오기
const getCities = (stateCode, success, fail) =>
  addressAPI.get(`/api/address/city`, { params: { stateCode } }).then(success).catch(fail)

export default {
  getCountries,
  getStates,
  getCities,
}
