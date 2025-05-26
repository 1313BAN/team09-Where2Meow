<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
    <div class="max-w-md w-full space-y-8">
      <!-- 헤더 -->
      <div class="text-center">
        <div class="flex justify-center items-center gap-3 mb-4">
          <div class="cat-paw-mask w-12 h-12" style="color: var(--primary-color)"></div>
          <h2 class="text-3xl font-extrabold text-gray-900">어디가냥</h2>
        </div>
        <p class="text-sm text-gray-600">새로운 여행을 위한 계정을 만들어보세요</p>
      </div>

      <!-- 회원가입 폼 -->
      <form @submit.prevent="handleSignup" class="mt-8 space-y-4">
        <!-- 이름 -->
        <div>
          <label for="name" class="block text-sm font-medium text-gray-700 mb-1">
            이름 <span class="text-red-500">*</span>
          </label>
          <div class="relative">
            <input
              id="name"
              v-model="signupForm.name"
              type="text"
              required
              class="appearance-none rounded-lg relative block w-full px-4 py-3 pl-12 border border-gray-300 placeholder-gray-500 text-gray-900 focus:outline-none focus:ring-2 focus:ring-[var(--primary-color)] focus:border-transparent transition-all duration-200"
              placeholder="이름을 입력하세요"
              :class="{ 'border-red-400': errors.name }"
            />
            <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
              <i class="pi pi-user text-gray-400"></i>
            </div>
          </div>
          <p v-if="errors.name" class="mt-1 text-sm text-red-600">{{ errors.name }}</p>
        </div>

        <!-- 닉네임 -->
        <div>
          <label for="nickname" class="block text-sm font-medium text-gray-700 mb-1">
            닉네임 <span class="text-red-500">*</span>
          </label>
          <div class="relative">
            <input
              id="nickname"
              v-model="signupForm.nickname"
              type="text"
              required
              class="appearance-none rounded-lg relative block w-full px-4 py-3 pl-12 border border-gray-300 placeholder-gray-500 text-gray-900 focus:outline-none focus:ring-2 focus:ring-[var(--primary-color)] focus:border-transparent transition-all duration-200"
              placeholder="닉네임을 입력하세요"
              :class="{ 'border-red-400': errors.nickname }"
            />
            <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
              <i class="pi pi-at text-gray-400"></i>
            </div>
          </div>
          <p v-if="errors.nickname" class="mt-1 text-sm text-red-600">{{ errors.nickname }}</p>
        </div>

        <!-- 이메일 -->
        <div>
          <label for="email" class="block text-sm font-medium text-gray-700 mb-1">
            이메일 <span class="text-red-500">*</span>
          </label>
          <div class="relative">
            <input
              id="email"
              v-model="signupForm.email"
              type="email"
              required
              class="appearance-none rounded-lg relative block w-full px-4 py-3 pl-12 border border-gray-300 placeholder-gray-500 text-gray-900 focus:outline-none focus:ring-2 focus:ring-[var(--primary-color)] focus:border-transparent transition-all duration-200"
              placeholder="이메일을 입력하세요"
              :class="{ 'border-red-400': errors.email }"
            />
            <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
              <i class="pi pi-envelope text-gray-400"></i>
            </div>
          </div>
          <p v-if="errors.email" class="mt-1 text-sm text-red-600">{{ errors.email }}</p>
        </div>

        <!-- 비밀번호 -->
        <div>
          <label for="password" class="block text-sm font-medium text-gray-700 mb-1">
            비밀번호 <span class="text-red-500">*</span>
          </label>
          <div class="relative">
            <input
              id="password"
              v-model="signupForm.password"
              :type="showPassword ? 'text' : 'password'"
              required
              class="appearance-none rounded-lg relative block w-full px-4 py-3 pl-12 pr-12 border border-gray-300 placeholder-gray-500 text-gray-900 focus:outline-none focus:ring-2 focus:ring-[var(--primary-color)] focus:border-transparent transition-all duration-200"
              placeholder="비밀번호를 입력하세요 (8자 이상)"
              :class="{ 'border-red-400': errors.password }"
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
          <p v-if="errors.password" class="mt-1 text-sm text-red-600">{{ errors.password }}</p>
        </div>

        <!-- 비밀번호 확인 -->
        <div>
          <label for="confirmPassword" class="block text-sm font-medium text-gray-700 mb-1">
            비밀번호 확인 <span class="text-red-500">*</span>
          </label>
          <div class="relative">
            <input
              id="confirmPassword"
              v-model="signupForm.confirmPassword"
              :type="showConfirmPassword ? 'text' : 'password'"
              required
              class="appearance-none rounded-lg relative block w-full px-4 py-3 pl-12 pr-12 border border-gray-300 placeholder-gray-500 text-gray-900 focus:outline-none focus:ring-2 focus:ring-[var(--primary-color)] focus:border-transparent transition-all duration-200"
              placeholder="비밀번호를 다시 입력하세요"
              :class="{ 'border-red-400': errors.confirmPassword }"
            />
            <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
              <i class="pi pi-lock text-gray-400"></i>
            </div>
            <button
              type="button"
              @click="showConfirmPassword = !showConfirmPassword"
              class="absolute inset-y-0 right-0 pr-3 flex items-center"
            >
              <i
                :class="[
                  showConfirmPassword ? 'pi pi-eye-slash' : 'pi pi-eye',
                  'text-gray-400 hover:text-gray-600',
                ]"
              ></i>
            </button>
          </div>
          <p v-if="errors.confirmPassword" class="mt-1 text-sm text-red-600">
            {{ errors.confirmPassword }}
          </p>
        </div>

        <!-- 휴대폰 번호 -->
        <div>
          <label for="phone" class="block text-sm font-medium text-gray-700 mb-1">
            휴대폰 번호 <span class="text-red-500">*</span>
          </label>
          <div class="relative">
            <input
              id="phone"
              v-model="signupForm.phone"
              type="tel"
              required
              class="appearance-none rounded-lg relative block w-full px-4 py-3 pl-12 border border-gray-300 placeholder-gray-500 text-gray-900 focus:outline-none focus:ring-2 focus:ring-[var(--primary-color)] focus:border-transparent transition-all duration-200"
              placeholder="010-1234-5678"
              :class="{ 'border-red-400': errors.phone }"
            />
            <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
              <i class="pi pi-phone text-gray-400"></i>
            </div>
          </div>
          <p v-if="errors.phone" class="mt-1 text-sm text-red-600">{{ errors.phone }}</p>
        </div>

        <!-- 에러 메시지 -->
        <div v-if="generalError" class="text-center">
          <p class="text-sm text-red-600">{{ generalError }}</p>
        </div>

        <!-- 회원가입 버튼 -->
        <div class="pt-4">
          <button
            type="submit"
            :disabled="isLoading"
            class="cursor-pointer group relative w-full flex justify-center py-3 px-4 border border-transparent text-sm font-medium rounded-lg text-white focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[var(--primary-color)] transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed"
            style="background-image: var(--gradient-primary)"
          >
            <span v-if="!isLoading">회원가입</span>
            <span v-else class="flex items-center gap-2">
              <i class="pi pi-spinner pi-spin"></i>
              처리 중...
            </span>
          </button>
        </div>

        <!-- 로그인 링크 -->
        <div class="text-center">
          <div class="text-sm">
            <span class="text-gray-600">이미 계정이 있으신가요? </span>
            <RouterLink
              to="/login"
              class="text-[var(--primary-color)] hover:text-[var(--secondary-color)] font-medium transition-colors duration-200"
            >
              로그인
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
import { signup } from '@/api/user'
import { toast } from 'vue-sonner'

const router = useRouter()

// 폼 데이터
const signupForm = reactive({
  name: '',
  nickname: '',
  email: '',
  password: '',
  confirmPassword: '',
  phone: '',
  image: '',
})

// UI 상태
const showPassword = ref(false)
const showConfirmPassword = ref(false)
const isLoading = ref(false)

// 에러 상태
const errors = reactive({
  name: '',
  nickname: '',
  email: '',
  password: '',
  confirmPassword: '',
  phone: '',
})
const generalError = ref('')

// 폼 검증
const validateForm = () => {
  let isValid = true

  // 모든 에러 초기화
  Object.keys(errors).forEach((key) => {
    errors[key] = ''
  })

  // 이름 검증
  if (!signupForm.name.trim()) {
    errors.name = '이름을 입력해주세요'
    isValid = false
  }

  // 닉네임 검증
  if (!signupForm.nickname.trim()) {
    errors.nickname = '닉네임을 입력해주세요'
    isValid = false
  }

  // 이메일 검증
  if (!signupForm.email.trim()) {
    errors.email = '이메일을 입력해주세요'
    isValid = false
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(signupForm.email)) {
    errors.email = '올바른 이메일 형식이 아닙니다'
    isValid = false
  }

  // 비밀번호 검증
  if (!signupForm.password) {
    errors.password = '비밀번호를 입력해주세요'
    isValid = false
  } else if (signupForm.password.length < 8) {
    errors.password = '비밀번호는 8자 이상이어야 합니다'
    isValid = false
  } else if (signupForm.password.length > 100) {
    errors.password = '비밀번호는 100자 이하여야 합니다'
    isValid = false
  }

  // 비밀번호 확인 검증
  if (!signupForm.confirmPassword) {
    errors.confirmPassword = '비밀번호 확인을 입력해주세요'
    isValid = false
  } else if (signupForm.password !== signupForm.confirmPassword) {
    errors.confirmPassword = '비밀번호가 일치하지 않습니다'
    isValid = false
  }

  // 휴대폰 번호 검증
  if (!signupForm.phone.trim()) {
    errors.phone = '휴대폰 번호를 입력해주세요'
    isValid = false
  } else if (!/^01([0|1|6|7|8|9])-?([0-9]{3,4})-?([0-9]{4})$/.test(signupForm.phone)) {
    errors.phone = '올바른 휴대폰 번호 형식이 아닙니다'
    isValid = false
  }

  return isValid
}

// 회원가입 처리
const handleSignup = async () => {
  if (!validateForm()) return

  isLoading.value = true
  generalError.value = ''

  try {
    const signupData = {
      name: signupForm.name.trim(),
      nickname: signupForm.nickname.trim(),
      email: signupForm.email.trim(),
      password: signupForm.password,
      phone: signupForm.phone.trim(),
      image: signupForm.image,
    }

    await signup(signupData)
    toast.success('회원가입이 완료되었습니다! 로그인해주세요.')
    router.push('/login')
  } catch (error) {
    console.error('회원가입 실패:', error.message || '알 수 없는 오류')

    if (error.response?.status === 409) {
      generalError.value = '이미 존재하는 이메일입니다'
    } else if (error.response?.status === 400) {
      const errorMessage = error.response.data?.message || '입력 정보를 확인해주세요'
      generalError.value = errorMessage
    } else {
      generalError.value = '회원가입 중 오류가 발생했습니다. 다시 시도해주세요'
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
