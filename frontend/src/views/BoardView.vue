<template>
  <div class="min-h-screen bg-gray-50">
    <div class="container mx-auto px-4 py-8">
      <div class="max-w-7xl mx-auto">
        <!-- 페이지 헤더 -->
        <div class="mb-8">
          <h1 class="text-3xl font-bold text-gray-900">게시판</h1>
          <p class="text-gray-600 mt-2">다양한 주제로 소통해보세요</p>
        </div>

        <div class="flex gap-8">
          <!-- 좌측 사이드바 -->
          <div class="w-64 flex-shrink-0">
            <BoardSidebar 
              :active-category="activeCategory"
              @category-change="handleCategoryChange"
            />
          </div>

          <!-- 우측 컨텐츠 영역 -->
          <div class="flex-1">
            <div class="bg-white rounded-lg shadow-sm border border-gray-200">
              <component :is="currentComponent" :category-id="activeCategory" />
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import BoardSidebar from '@/components/views/board/BoardSidebar.vue'
import BoardList from '@/components/views/board/BoardList.vue'
import BoardWrite from '@/components/views/board/BoardWrite.vue'

// 상태 관리
const activeCategory = ref(0) // 0: 전체, 1: 공지사항, 2: 자유게시판, 3: 여행후기, 4: 질문게시판
const activeMenu = ref('list') // list, write

// 카테고리 변경 핸들러
const handleCategoryChange = (categoryId) => {
  activeCategory.value = categoryId
  activeMenu.value = 'list' // 카테고리 변경 시 리스트로 이동
}

// 현재 활성화된 컴포넌트
const currentComponent = computed(() => {
  const components = {
    list: BoardList,
    write: BoardWrite,
  }
  return components[activeMenu.value]
})

// 글쓰기 모드로 변경
const changeToWriteMode = () => {
  activeMenu.value = 'write'
}

// 목록 모드로 변경
const changeToListMode = () => {
  activeMenu.value = 'list'
}

// 전역 이벤트 처리를 위한 provide
import { provide } from 'vue'
provide('boardNavigation', {
  changeToWriteMode,
  changeToListMode,
  activeCategory,
  activeMenu
})
</script>
