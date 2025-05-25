<template>
  <div class="bg-white rounded-lg shadow-sm border border-gray-200">
    <!-- 카테고리 섹션 -->
    <div class="p-4 border-b border-gray-200">
      <h2 class="text-lg font-semibold text-gray-900 mb-4">계정 메뉴</h2>
      
      <nav class="space-y-2">
        <button
          v-for="item in menuItems"
          :key="item.key"
          @click="$emit('tab-change', item.key)"
          :class="[
            'w-full flex items-center gap-3 px-4 py-3 text-left rounded-lg transition-colors cursor-pointer',
            activeTab === item.key
              ? 'bg-[var(--primary-color)] bg-opacity-10 text-[var(--primary-color)] font-semibold'
              : 'text-gray-700 hover:bg-gray-50'
          ]"
        >
          <i :class="[item.icon, activeTab === item.key ? 'text-[var(--primary-color)]' : 'text-gray-500']"></i>
          <span>{{ item.label }}</span>
        </button>
      </nav>
    </div>

    <!-- 계정 관리 섹션 -->
    <div class="p-4">
      <h3 class="text-sm font-semibold text-gray-900 mb-3">계정 관리</h3>
      
      <div class="space-y-2">
        <button
          @click="$emit('logout')"
          class="w-full flex items-center gap-3 px-4 py-3 text-left rounded-lg text-gray-700 hover:bg-gray-50 transition-colors cursor-pointer"
        >
          <i class="pi pi-sign-out text-gray-500"></i>
          <span>로그아웃</span>
        </button>
        
        <button
          @click="$emit('delete-account')"
          class="w-full flex items-center gap-3 px-4 py-3 text-left rounded-lg text-red-600 hover:bg-red-50 transition-colors cursor-pointer"
        >
          <i class="pi pi-trash text-red-500"></i>
          <span>회원탈퇴</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

defineProps({
  activeTab: {
    type: String,
    required: true
  }
})

defineEmits(['tab-change', 'logout', 'delete-account'])

const menuItems = computed(() => [
  {
    key: 'profile',
    label: '프로필 설정',
    icon: 'pi pi-user'
  },
  {
    key: 'posts',
    label: '내가 쓴 글',
    icon: 'pi pi-file-edit'
  },
  {
    key: 'comments',
    label: '내가 쓴 댓글',
    icon: 'pi pi-comments'
  },
  {
    key: 'bookmarks',
    label: '북마크한 게시글',
    icon: 'pi pi-bookmark'
  }
])
</script>
