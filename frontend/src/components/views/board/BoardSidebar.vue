<template>
  <div class="bg-white rounded-lg shadow-sm border border-gray-200">
    <!-- 카테고리 섹션 -->
    <div class="p-4">
      <div class="space-y-1">
        <button
          v-for="category in categories"
          :key="category.id"
          @click="$emit('category-change', category.id)"
          :class="[
            'w-full flex items-center gap-3 px-3 py-2 rounded-lg text-left transition-colors cursor-pointer',
            activeCategory === category.id
              ? 'bg-[var(--primary-10)] text-[var(--primary-color)] font-semibold'
              : 'text-gray-700 hover:bg-gray-50',
          ]"
        >
          <i
            :class="[
              category.icon,
              activeCategory === category.id ? 'text-[var(--primary-color)]' : 'text-gray-400',
            ]"
          ></i>
          <span>{{ category.name }}</span>
          <span v-if="category.count !== undefined" class="ml-auto text-sm text-gray-400"
            >({{ category.count }})</span
          >
        </button>
      </div>

      <!-- 게시판 관리 섹션 -->
      <div class="mt-6 pt-4 border-t border-gray-200">
        <h4 class="text-sm font-medium text-gray-500 mb-2">게시판 관리</h4>
        <div class="space-y-1">
          <button
            @click="handleWritePost"
            class="w-full flex items-center gap-3 px-3 py-2 rounded-lg text-left text-white transition-colors cursor-pointer"
            style="background: var(--gradient-primary)"
          >
            <i class="pi pi-plus text-white"></i>
            <span>글쓰기</span>
          </button>

          <button
            @click="handleRefresh"
            class="w-full flex items-center gap-3 px-3 py-2 rounded-lg text-left text-gray-700 hover:bg-gray-50 transition-colors cursor-pointer"
          >
            <i class="pi pi-refresh text-gray-400"></i>
            <span>새로고침</span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, inject } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { storeToRefs } from 'pinia'
import { toast } from 'vue-sonner'

const props = defineProps({
  activeCategory: {
    type: Number,
    required: true,
  },
})

defineEmits(['category-change'])

// 게시판 네비게이션 inject
const boardNavigation = inject('boardNavigation')
const router = useRouter()
const authStore = useAuthStore()
const { isLoggedIn } = storeToRefs(authStore)

// 카테고리 목록 (백엔드 스키마에 따라 1,2,3,4가 실제 카테고리, 0은 전체)
const categories = ref([
  { id: 0, name: '전체 게시글', icon: 'pi pi-list' },
  { id: 1, name: '공지사항', icon: 'pi pi-megaphone' },
  { id: 2, name: '자유게시판', icon: 'pi pi-comments' },
  { id: 3, name: '여행후기', icon: 'pi pi-map' },
  { id: 4, name: '질문게시판', icon: 'pi pi-question-circle' },
])

// 글쓰기 모드로 변경
const handleWritePost = () => {
  if (!isLoggedIn.value) {
    toast.error('로그인이 필요합니다')
    router.push('/login')
    return
  }
  
  if (boardNavigation) {
    boardNavigation.changeToWriteMode()
  }
}

// 새로고침
const handleRefresh = () => {
  window.location.reload()
}
</script>
