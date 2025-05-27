import { localAxios } from '@/utils/http-commons'

const api = localAxios()

// 회원가입
export const signup = async (signupData) => {
  try {
    const response = await api.post('/api/user/signup', signupData)
    return response.data
  } catch (error) {
    throw error
  }
}

// 사용자 정보 수정
export const updateUser = async (userData) => {
  try {
    const response = await api.put('/api/user', userData)
    return response.data
  } catch (error) {
    throw error
  }
}

// 사용자 탈퇴
export const deleteUser = async (uuid) => {
  try {
    const response = await api.delete('/api/user', { 
      data: { uuid } // DELETE 요청에 body 데이터 전송
    })
    return response.data
  } catch (error) {
    throw error
  }
}

// 현재 사용자 정보 조회 (토큰 기반)
export const getCurrentUser = async () => {
  try {
    const response = await api.get('/api/user/me')
    return response.data
  } catch (error) {
    throw error
  }
}
