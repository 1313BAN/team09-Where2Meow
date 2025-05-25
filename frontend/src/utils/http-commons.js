import axios from 'axios'

const { VITE_API_BASE_URL } = import.meta.env

// local vue api axios instance
const localAxios = ()=>{
  const instance = axios.create({
    baseURL: VITE_API_BASE_URL,
    // baseURL: "http://localhost:8080",
    headers: {
      'Content-Type': 'application/json;charset=utf-8'
    }
  })
  
  // 요청 인터셉터 - 토큰 자동 추가
  instance.interceptors.request.use(
    (config) => {
      const token = localStorage.getItem('accessToken')
      if (token) {
        config.headers.Authorization = `Bearer ${token}`
      }
      return config
    },
    (error) => {
      return Promise.reject(error)
    }
  )
  
  // 응답 인터셉터 - 토큰 만료 시 로그아웃 처리
  instance.interceptors.response.use(
    (response) => {
      return response
    },
    (error) => {
      if (error.response?.status === 401 && error.response?.data?.message === 'JWT token expired') {
        // 토큰 만료 시 로그아웃 처리
        localStorage.removeItem('accessToken')
        localStorage.removeItem('user')
        window.location.href = '/login'
      }
      return Promise.reject(error)
    }
  )
  
  return instance
}

export { localAxios }
