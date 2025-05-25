<template>
  <div class="container mx-auto px-4 py-8">
    <div class="max-w-4xl mx-auto">
      <h1 class="text-3xl font-bold text-gray-900 mb-6">일정 관리</h1>
      <div class="bg-white rounded-lg shadow-md p-6">
        <p class="text-gray-600 mb-4">안녕하세요, {{ userName }}님!</p>
        <p class="text-gray-600">여기는 로그인이 필요한 일정 관리 페이지입니다.</p>
        <div class="mt-6 p-4 bg-blue-50 rounded-lg">
          <h2 class="text-lg font-semibold text-blue-800 mb-2">페이지 접근 테스트 성공!</h2>
          <p class="text-blue-600">이 페이지에 접근할 수 있다는 것은 로그인이 정상적으로 작동한다는 의미입니다.</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
const userName = computed(() => authStore.userName)
</script>
