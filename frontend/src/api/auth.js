import { localAxios } from '@/utils/http-commons'

const api = localAxios()

// 로그인
export const login = async (loginData) => {
  try {
    const response = await api.post('/api/auth/login', loginData)
    return response.data
  } catch (error) {
    throw error
  }
}

// 로그아웃
export const logout = async () => {
  try {
    const response = await api.post('/api/auth/logout')
    return response.data
  } catch (error) {
    throw error
  }
}

// 아이디 찾기
export const findId = async (findIdData) => {
  try {
    const response = await api.post('/api/auth/find-id', findIdData)
    return response.data
  } catch (error) {
    throw error
  }
}

// 비밀번호 재설정 확인
export const checkUserForPasswordReset = async (checkData) => {
  try {
    const response = await api.post('/api/auth/password/check', checkData)
    return response.data
  } catch (error) {
    throw error
  }
}

// 비밀번호 재설정
export const resetPassword = async (resetData) => {
  try {
    const response = await api.put('/api/auth/password/reset', resetData)
    return response.data
  } catch (error) {
    throw error
  }
}
