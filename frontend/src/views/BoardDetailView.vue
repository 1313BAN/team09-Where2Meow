<template>
  <div class="min-h-screen bg-gray-50">
    <div class="container mx-auto px-4 py-8">
      <div class="max-w-4xl mx-auto">
        <!-- 뒤로가기 버튼 -->
        <div>
          <button
            @click="goBack"
            class="flex items-center gap-2 px-4 py-2 text-gray-600 hover:text-gray-800 transition-colors cursor-pointer"
          >
            <i class="pi pi-arrow-left"></i>
            <span>목록으로 돌아가기</span>
          </button>
        </div>

        <!-- 게시글 상세 컴포넌트 -->
        <BoardDetail />
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import BoardDetail from '@/components/common/board/BoardDetail.vue'

const router = useRouter()

const goBack = () => {
  // 이전 페이지가 게시판이면 뒤로가기, 아니면 게시판 메인으로
  if (window.history.length > 1) {
    router.go(-1)
  } else {
    router.push({ name: 'board' })
  }
}
</script>
