import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, logout as logoutApi } from '@/api/auth'

export const useAuthStore = defineStore('auth', () => {
  // 상태
  const accessToken = ref(localStorage.getItem('accessToken') || '')
  const user = ref(() => {
    try {
      return JSON.parse(localStorage.getItem('user') || 'null')
    } catch (error) {
      console.error('사용자 정보 파싱 오류:', error)
      return null
    }
  })

  // getters
  const isLoggedIn = computed(() => !!accessToken.value && !!user.value)
  const userName = computed(() => user.value?.name || '')
  const userEmail = computed(() => user.value?.email || '')
  const userUuid = computed(() => user.value?.uuid || '')

  // actions
  const login = async (loginData) => {
    try {
      const response = await loginApi(loginData)

      // 로그인 성공 시 토큰과 사용자 정보 저장
      accessToken.value = response.token
      user.value = {
        uuid: response.uuid,
        name: response.name,
        email: response.email,
        role: response.role,
        nickname: response.nickname || response.name, // 닉네임이 없으면 이름 사용
        phone: response.phone || '',
        image: response.image || '',
      }

      // localStorage에 저장
      localStorage.setItem('accessToken', response.token)
      localStorage.setItem('user', JSON.stringify(user.value))

      return response
    } catch (error) {
      throw error
    }
  }

  const logout = async () => {
    try {
      // 서버에 로그아웃 요청
      await logoutApi()
    } catch (error) {
      console.error('로그아웃 API 호출 실패:', error)
    } finally {
      // 로컬 상태 및 스토리지 정리
      accessToken.value = ''
      user.value = null
      localStorage.removeItem('accessToken')
      localStorage.removeItem('user')
    }
  }

  const checkAuthStatus = () => {
    const token = localStorage.getItem('accessToken')
    const userData = localStorage.getItem('user')

    if (token && userData) {
      try {
        accessToken.value = token
        user.value = JSON.parse(userData)
      } catch (error) {
        console.error('localStorage의 사용자 데이터 파싱 오류:', error)
        // 파싱 실패 시 localStorage 정리
        localStorage.removeItem('accessToken')
        localStorage.removeItem('user')
      }
    }
  }

  return {
    // state
    accessToken,
    user,
    // getters
    isLoggedIn,
    userName,
    userEmail,
    userUuid,
    // actions
    login,
    logout,
    checkAuthStatus,
  }
})
