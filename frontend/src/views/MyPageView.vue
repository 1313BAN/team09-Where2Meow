<template>
  <div class="container mx-auto px-4 py-8">
    <div class="max-w-4xl mx-auto">
      <h1 class="text-3xl font-bold text-gray-900 mb-6">마이페이지</h1>
      <div class="bg-white rounded-lg shadow-md p-6">
        <div class="flex items-center gap-4 mb-6">
          <div class="w-16 h-16 rounded-full flex items-center justify-center" style="background: var(--gradient-primary)">
            <span class="text-white text-2xl font-bold">{{ userName.charAt(0) }}</span>
          </div>
          <div>
            <h2 class="text-xl font-semibold text-gray-900">{{ userName }}님</h2>
            <p class="text-gray-600">{{ userEmail }}</p>
          </div>
        </div>
        
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div class="p-4 bg-gray-50 rounded-lg">
            <h3 class="font-semibold text-gray-900 mb-2">계정 정보</h3>
            <p class="text-sm text-gray-600">이름: {{ userName }}</p>
            <p class="text-sm text-gray-600">이메일: {{ userEmail }}</p>
            <p class="text-sm text-gray-600">UUID: {{ userUuid }}</p>
          </div>
          
          <div class="p-4 bg-green-50 rounded-lg">
            <h3 class="font-semibold text-green-800 mb-2">로그인 상태</h3>
            <p class="text-sm text-green-600">정상적으로 로그인되어 있습니다.</p>
            <p class="text-sm text-green-600">인증이 필요한 페이지에 접근 가능합니다.</p>
          </div>
        </div>
        
        <div class="mt-6 pt-6 border-t border-gray-200">
          <button
            @click="handleLogout"
            class="px-4 py-2 bg-red-500 text-white rounded-lg hover:bg-red-600 transition-colors cursor-pointer"
          >
            로그아웃
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { toast } from 'vue-sonner'

const router = useRouter()
const authStore = useAuthStore()

const userName = computed(() => authStore.userName)
const userEmail = computed(() => authStore.userEmail)
const userUuid = computed(() => authStore.userUuid)

const handleLogout = async () => {
  try {
    await authStore.logout()
    toast.success('로그아웃되었습니다')
    router.push('/')
  } catch (error) {
    console.error('로그아웃 실패:', error)
    toast.error('로그아웃 중 오류가 발생했습니다')
  }
}
</script>
