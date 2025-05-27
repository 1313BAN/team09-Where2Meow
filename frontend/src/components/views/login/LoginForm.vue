<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
    <div class="max-w-md w-full space-y-8">
      <!-- 헤더 -->
      <div class="text-center">
        <div class="flex justify-center items-center gap-3 mb-4">
          <div class="cat-paw-mask w-12 h-12" style="color: var(--primary-color)"></div>
          <h2 class="text-3xl font-extrabold text-gray-900">어디가냥</h2>
        </div>
        <p class="text-sm text-gray-600">로그인하여 여행을 시작하세요</p>
      </div>

      <!-- 로그인 폼 -->
      <form @submit.prevent="handleLogin" class="mt-8 space-y-6">
        <div class="space-y-4">
          <!-- 이메일 입력 -->
          <div>
            <label for="email" class="block text-sm font-medium text-gray-700 mb-1"> 이메일 </label>
            <div class="relative">
              <input
                id="email"
                v-model="loginForm.email"
                type="email"
                required
                class="appearance-none rounded-lg relative block w-full px-4 py-3 pl-12 border border-gray-300 placeholder-gray-500 text-gray-900 focus:outline-none focus:ring-2 focus:ring-[var(--primary-color)] focus:border-transparent transition-all duration-200"
                placeholder="이메일을 입력하세요"
                :class="{ 'border-red-400': emailError }"
              />
              <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                <i class="pi pi-envelope text-gray-400"></i>
              </div>
            </div>
            <p v-if="emailError" class="mt-1 text-sm text-red-600">{{ emailError }}</p>
          </div>

          <!-- 비밀번호 입력 -->
          <div>
            <label for="password" class="block text-sm font-medium text-gray-700 mb-1">
              비밀번호
            </label>
            <div class="relative">
              <input
                id="password"
                v-model="loginForm.password"
                :type="showPassword ? 'text' : 'password'"
                required
                class="appearance-none rounded-lg relative block w-full px-4 py-3 pl-12 pr-12 border border-gray-300 placeholder-gray-500 text-gray-900 focus:outline-none focus:ring-2 focus:ring-[var(--primary-color)] focus:border-transparent transition-all duration-200"
                placeholder="비밀번호를 입력하세요"
                :class="{ 'border-red-400': passwordError }"
              />
              <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                <i class="pi pi-lock text-gray-400"></i>
              </div>
              <button
                type="button"
                @click="showPassword = !showPassword"
                class="absolute inset-y-0 right-0 pr-3 flex items-center"
              >
                <i
                  :class="[
                    showPassword ? 'pi pi-eye-slash' : 'pi pi-eye',
                    'text-gray-400 hover:text-gray-600',
                  ]"
                ></i>
              </button>
            </div>
            <p v-if="passwordError" class="mt-1 text-sm text-red-600">{{ passwordError }}</p>
          </div>
        </div>

        <!-- 옵션 -->
        <div class="flex items-center justify-between">
          <div class="flex items-center">
            <input
              id="remember-email"
              v-model="loginForm.rememberEmail"
              type="checkbox"
              class="h-4 w-4 text-[var(--primary-color)] focus:ring-[var(--primary-color)] border-gray-300 rounded"
            />
            <label for="remember-email" class="ml-2 block text-sm text-gray-900">
              이메일 저장
            </label>
          </div>
          <div class="flex items-center">
            <input
              id="remember-me"
              v-model="loginForm.rememberMe"
              type="checkbox"
              class="h-4 w-4 text-[var(--primary-color)] focus:ring-[var(--primary-color)] border-gray-300 rounded"
            />
            <label for="remember-me" class="ml-2 block text-sm text-gray-900"> 자동 로그인 </label>
          </div>
        </div>

        <!-- 에러 메시지 -->
        <div v-if="generalError" class="text-center">
          <p class="text-sm text-red-600">{{ generalError }}</p>
        </div>

        <!-- 로그인 버튼 -->
        <div>
          <button
            type="submit"
            :disabled="isLoading"
            class="cursor-pointer group relative w-full flex justify-center py-3 px-4 border border-transparent text-sm font-medium rounded-lg text-white focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[var(--primary-color)] transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed"
            style="background-image: var(--gradient-primary)"
          >
            <span v-if="!isLoading">로그인</span>
            <span v-else class="flex items-center gap-2">
              <i class="pi pi-spinner pi-spin"></i>
              로그인 중...
            </span>
          </button>
        </div>

        <!-- 링크들 -->
        <div class="text-center space-y-2">
          <!-- <div class="text-sm">
            <RouterLink
              to="/find-password"
              class="text-[var(--primary-color)] hover:text-[var(--secondary-color)] transition-colors duration-200"
            >
              비밀번호를 잊으셨나요?
            </RouterLink>
          </div> -->
          <div class="text-sm">
            <span class="text-gray-600">계정이 없으신가요? </span>
            <RouterLink
              to="/signup"
              class="text-[var(--primary-color)] hover:text-[var(--secondary-color)] font-medium transition-colors duration-200"
            >
              회원가입
            </RouterLink>
          </div>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { toast } from 'vue-sonner'

const router = useRouter()
const authStore = useAuthStore()

// 폼 데이터
const loginForm = reactive({
  email: '',
  password: '',
  rememberEmail: false,
  rememberMe: false,
})

// UI 상태
const showPassword = ref(false)
const isLoading = ref(false)

// 에러 상태
const emailError = ref('')
const passwordError = ref('')
const generalError = ref('')

// 폼 검증
const validateForm = () => {
  let isValid = true

  // 이메일 검증
  if (!loginForm.email) {
    emailError.value = '이메일을 입력해주세요'
    isValid = false
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(loginForm.email)) {
    emailError.value = '올바른 이메일 형식이 아닙니다'
    isValid = false
  } else {
    emailError.value = ''
  }

  // 비밀번호 검증
  if (!loginForm.password) {
    passwordError.value = '비밀번호를 입력해주세요'
    isValid = false
  } else {
    passwordError.value = ''
  }

  return isValid
}

// 로그인 처리
const handleLogin = async () => {
  if (!validateForm()) return

  isLoading.value = true
  generalError.value = ''

  try {
    await authStore.login(loginForm)
    toast.success('로그인되었습니다!')

    // redirect 쿠리가 있으면 해당 페이지로, 없으면 메인 페이지로 이동
    const redirectPath = router.currentRoute.value.query.redirect || '/'
    router.push(redirectPath)
  } catch (error) {
    console.error('로그인 실패:', error)

    if (error.response?.status === 401) {
      generalError.value = '이메일 또는 비밀번호가 올바르지 않습니다'
    } else if (error.response?.status === 403) {
      generalError.value = '계정이 비활성화되었습니다'
    } else {
      generalError.value = '로그인 중 오류가 발생했습니다. 다시 시도해주세요'
    }

    toast.error(generalError.value)
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
.cat-paw-mask {
  background-color: #00edb3;
  mask: url('@/assets/cat-paw.png') no-repeat center;
  mask-size: contain;
  -webkit-mask: url('@/assets/cat-paw.png') no-repeat center;
  -webkit-mask-size: contain;
}
</style>
