<template>
  <div class="p-6">
    <div class="mb-6">
      <h2 class="text-xl font-semibold text-gray-900">프로필 설정</h2>
      <p class="text-gray-600 mt-1">개인정보를 수정하고 관리하세요</p>
    </div>

    <form @submit.prevent="handleUpdateProfile" class="space-y-6">
      <!-- 프로필 이미지 -->
      <div class="flex items-center gap-6">
        <div
          class="w-20 h-20 rounded-full flex items-center justify-center"
          style="background: var(--gradient-primary)"
        >
          <span class="text-white text-2xl font-bold">{{ profileForm.name.charAt(0) }}</span>
        </div>
        <div>
          <h3 class="font-medium text-gray-900">프로필 사진</h3>
          <p class="text-sm text-gray-600">현재는 이름의 첫 글자로 표시됩니다</p>
        </div>
      </div>

      <!-- 이름 -->
      <div>
        <label for="name" class="block text-sm font-medium text-gray-700 mb-2">
          이름 <span class="text-red-500">*</span>
        </label>
        <input
          id="name"
          v-model="profileForm.name"
          type="text"
          required
          class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[var(--primary-color)] focus:border-transparent transition-all duration-200"
          :class="{ 'border-red-400': errors.name }"
        />
        <p v-if="errors.name" class="mt-1 text-sm text-red-600">{{ errors.name }}</p>
      </div>

      <!-- 닉네임 -->
      <div>
        <label for="nickname" class="block text-sm font-medium text-gray-700 mb-2">
          닉네임 <span class="text-red-500">*</span>
        </label>
        <input
          id="nickname"
          v-model="profileForm.nickname"
          type="text"
          required
          class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[var(--primary-color)] focus:border-transparent transition-all duration-200"
          :class="{ 'border-red-400': errors.nickname }"
        />
        <p v-if="errors.nickname" class="mt-1 text-sm text-red-600">{{ errors.nickname }}</p>
      </div>

      <!-- 이메일 -->
      <div>
        <label for="email" class="block text-sm font-medium text-gray-700 mb-2">
          이메일 <span class="text-red-500">*</span>
        </label>
        <input
          id="email"
          v-model="profileForm.email"
          type="email"
          required
          class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[var(--primary-color)] focus:border-transparent transition-all duration-200"
          :class="{ 'border-red-400': errors.email }"
        />
        <p v-if="errors.email" class="mt-1 text-sm text-red-600">{{ errors.email }}</p>
      </div>

      <!-- 휴대폰 번호 -->
      <div>
        <label for="phone" class="block text-sm font-medium text-gray-700 mb-2">
          휴대폰 번호 <span class="text-red-500">*</span>
        </label>
        <input
          id="phone"
          v-model="profileForm.phone"
          type="tel"
          required
          placeholder="010-1234-5678"
          class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[var(--primary-color)] focus:border-transparent transition-all duration-200"
          :class="{ 'border-red-400': errors.phone }"
        />
        <p v-if="errors.phone" class="mt-1 text-sm text-red-600">{{ errors.phone }}</p>
      </div>

      <!-- 에러 메시지 -->
      <div v-if="generalError" class="p-4 bg-red-50 border border-red-200 rounded-lg">
        <p class="text-sm text-red-600">{{ generalError }}</p>
      </div>

      <!-- 성공 메시지 -->
      <div v-if="successMessage" class="p-4 bg-green-50 border border-green-200 rounded-lg">
        <p class="text-sm text-green-600">{{ successMessage }}</p>
      </div>

      <!-- 저장 버튼 -->
      <div class="flex justify-end pt-4 border-t border-gray-200">
        <button
          type="submit"
          :disabled="isLoading"
          class="px-6 py-3 text-white rounded-lg transition-colors cursor-pointer disabled:opacity-50 disabled:cursor-not-allowed"
          style="background: var(--gradient-primary)"
        >
          <span v-if="!isLoading">저장하기</span>
          <span v-else class="flex items-center gap-2">
            <i class="pi pi-spinner pi-spin"></i>
            저장 중...
          </span>
        </button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { storeToRefs } from 'pinia'
import { updateUser } from '@/api/user'
import { toast } from 'vue-sonner'

const authStore = useAuthStore()
const { user, userUuid } = storeToRefs(authStore)

// 폼 데이터
const profileForm = reactive({
  name: '',
  nickname: '',
  email: '',
  phone: '',
  image: '',
})

// 상태 관리
const isLoading = ref(false)
const errors = reactive({
  name: '',
  nickname: '',
  email: '',
  phone: '',
})
const generalError = ref('')
const successMessage = ref('')

// 폼 검증
const validateForm = () => {
  let isValid = true

  // 모든 에러 초기화
  Object.keys(errors).forEach((key) => {
    errors[key] = ''
  })

  // 이름 검증
  if (!profileForm.name.trim()) {
    errors.name = '이름을 입력해주세요'
    isValid = false
  }

  // 닉네임 검증
  if (!profileForm.nickname.trim()) {
    errors.nickname = '닉네임을 입력해주세요'
    isValid = false
  }

  // 이메일 검증
  if (!profileForm.email.trim()) {
    errors.email = '이메일을 입력해주세요'
    isValid = false
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(profileForm.email)) {
    errors.email = '올바른 이메일 형식이 아닙니다'
    isValid = false
  }

  // 휴대폰 번호 검증
  if (!profileForm.phone.trim()) {
    errors.phone = '휴대폰 번호를 입력해주세요'
    isValid = false
  } else if (!/^01([0|1|6|7|8|9])[-\s]?([0-9]{3,4})[-\s]?([0-9]{4})$/.test(profileForm.phone)) {
    errors.phone = '올바른 휴대폰 번호 형식이 아닙니다'
    isValid = false
  }

  return isValid
}

// 프로필 업데이트 처리
const handleUpdateProfile = async () => {
  if (!validateForm()) return

  isLoading.value = true
  generalError.value = ''
  successMessage.value = ''

  try {
    const updateData = {
      uuid: userUuid.value,
      name: profileForm.name.trim(),
      nickname: profileForm.nickname.trim(),
      email: profileForm.email.trim(),
      phone: profileForm.phone.trim(),
      image: profileForm.image,
    }

    const updatedUser = await updateUser(updateData)

    // 스토어 상태 업데이트
    authStore.user = {
      ...authStore.user,
      name: updatedUser.name,
      email: updatedUser.email,
    }

    // localStorage 업데이트
    localStorage.setItem('user', JSON.stringify(authStore.user))

    successMessage.value = '프로필이 성공적으로 업데이트되었습니다'
    toast.success('프로필이 업데이트되었습니다')

    // 성공 메시지 3초 후 자동 제거
    setTimeout(() => {
      successMessage.value = ''
    }, 3000)
  } catch (error) {
    console.error('프로필 업데이트 실패:', error)

    if (error.response?.status === 409) {
      generalError.value = '이미 사용 중인 이메일입니다'
    } else if (error.response?.status === 400) {
      generalError.value = error.response.data?.message || '입력 정보를 확인해주세요'
    } else {
      generalError.value = '프로필 업데이트 중 오류가 발생했습니다'
    }

    toast.error(generalError.value)
  } finally {
    isLoading.value = false
  }
}

// 초기 데이터 로드
onMounted(async () => {
  if (user.value) {
    profileForm.name = user.value.name || ''
    profileForm.nickname = user.value.nickname || user.value.name || '' // 닉네임이 없으면 이름 사용
    profileForm.email = user.value.email || ''
    profileForm.phone = user.value.phone || '010-0000-0000' // 기본값 설정
    profileForm.image = user.value.image || ''
  }
})
</script>
