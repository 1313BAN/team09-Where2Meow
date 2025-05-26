<template>
  <div class="p-6">
    <!-- 헤더 -->
    <div class="flex items-center justify-between mb-6">
      <div class="flex items-center gap-3">
        <button
          @click="goBack"
          class="p-2 rounded-lg text-gray-400 hover:text-gray-600 hover:bg-gray-50 transition-colors"
        >
          <i class="pi pi-arrow-left text-xl"></i>
        </button>
        <h2 class="text-xl font-semibold text-gray-900">
          {{ isEditMode ? '게시글 수정' : '새 게시글 작성' }}
        </h2>
      </div>
      
      <div class="flex items-center gap-3">
        <button
          @click="goBack"
          class="px-4 py-2 border border-gray-300 rounded-xl text-gray-700 hover:bg-gray-50 transition-colors cursor-pointer"
        >
          취소
        </button>
        <button
          @click="handleSubmit"
          :disabled="!isFormValid || isSubmitting"
          class="px-4 py-2 bg-gradient-to-r from-[var(--primary-color)] to-[var(--secondary-color)] text-white rounded-xl shadow-md hover:shadow-lg disabled:opacity-50 disabled:cursor-not-allowed active:scale-95 transition-all duration-200 ease-in-out cursor-pointer"
        >
          <span v-if="!isSubmitting">{{ isEditMode ? '수정하기' : '작성하기' }}</span>
          <span v-else class="flex items-center gap-2">
            <i class="pi pi-spinner pi-spin"></i>
            {{ isEditMode ? '수정 중...' : '작성 중...' }}
          </span>
        </button>
      </div>
    </div>

    <!-- 작성 폼 -->
    <div class="space-y-6">
      <!-- 카테고리 선택 -->
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-2">
          카테고리 <span class="text-red-500">*</span>
        </label>
        <select
          v-model="form.categoryId"
          class="relative cursor-pointer min-h-[48px] border border-gray-300 rounded-xl hover:border-gray-400 focus:border-[var(--secondary-color)] focus:outline-none bg-white transition-all duration-200 w-full px-3 py-2 pr-8 appearance-none"
          :class="{ 'border-red-300': errors.categoryId }"
        >
          <option value="">카테고리를 선택하세요</option>
          <option
            v-for="category in availableCategories"
            :key="category.id"
            :value="category.id"
          >
            {{ category.name }}
          </option>
        </select>
        <p v-if="errors.categoryId" class="mt-1 text-sm text-red-600">
          {{ errors.categoryId }}
        </p>
      </div>

      <!-- 제목 입력 -->
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-2">
          제목 <span class="text-red-500">*</span>
        </label>
        <input
          v-model="form.title"
          type="text"
          placeholder="제목을 입력하세요"
          maxlength="100"
          class="w-full min-h-[48px] border border-gray-300 rounded-xl hover:border-gray-400 focus:border-[var(--secondary-color)] focus:outline-none bg-white transition-all duration-200 px-3 py-2"
          :class="{ 'border-red-300': errors.title }"
        />
        <div class="flex justify-between mt-1">
          <p v-if="errors.title" class="text-sm text-red-600">
            {{ errors.title }}
          </p>
          <p class="text-sm text-gray-500">
            {{ form.title.length }}/100
          </p>
        </div>
      </div>

      <!-- 내용 입력 -->
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-2">
          내용 <span class="text-red-500">*</span>
        </label>
        <textarea
          v-model="form.content"
          rows="15"
          placeholder="내용을 입력하세요"
          maxlength="5000"
          class="w-full border border-gray-300 rounded-xl hover:border-gray-400 focus:border-[var(--secondary-color)] focus:outline-none bg-white transition-all duration-200 px-3 py-2 resize-none"
          :class="{ 'border-red-300': errors.content }"
        ></textarea>
        <div class="flex justify-between mt-1">
          <p v-if="errors.content" class="text-sm text-red-600">
            {{ errors.content }}
          </p>
          <p class="text-sm text-gray-500">
            {{ form.content.length }}/5000
          </p>
        </div>
      </div>

      <!-- 작성 가이드 -->
      <div class="bg-blue-50 border border-blue-200 rounded-lg p-4">
        <h4 class="text-sm font-medium text-blue-900 mb-2">
          <i class="pi pi-info-circle mr-2"></i>
          작성 가이드
        </h4>
        <ul class="text-sm text-blue-800 space-y-1">
          <li>• 다른 사용자에게 도움이 되는 내용을 작성해주세요</li>
          <li>• 욕설, 비방, 광고성 글은 삭제될 수 있습니다</li>
          <li>• 개인정보가 포함된 내용은 작성하지 마세요</li>
          <li>• 저작권을 침해하는 내용은 게시할 수 없습니다</li>
        </ul>
      </div>
    </div>
  </div>

  <!-- 이탈 확인 모달 -->
  <div
    v-if="showLeaveModal"
    class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50"
    @click="showLeaveModal = false"
  >
    <div class="bg-white rounded-lg p-6 max-w-md w-full mx-4" @click.stop>
      <div class="text-center">
        <div class="w-16 h-16 rounded-full bg-yellow-100 flex items-center justify-center mx-auto mb-4">
          <i class="pi pi-exclamation-triangle text-yellow-500 text-2xl"></i>
        </div>
        <h3 class="text-lg font-semibold text-gray-900 mb-2">작성 취소</h3>
        <p class="text-gray-600 mb-6">
          작성 중인 내용이 있습니다.<br>
          정말로 나가시겠습니까? 작성한 내용은 저장되지 않습니다.
        </p>
        <div class="flex gap-3">
          <button
            @click="showLeaveModal = false"
            class="flex-1 px-4 py-2 border border-gray-300 rounded-xl text-gray-700 hover:bg-gray-50 transition-colors cursor-pointer"
          >
            계속 작성
          </button>
          <button
            @click="confirmLeave"
            class="flex-1 px-4 py-2 bg-red-500 text-white rounded-xl hover:bg-red-600 transition-colors cursor-pointer"
          >
            나가기
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, inject, watch } from 'vue'
import { useRoute } from 'vue-router'
import { createBoard, updateBoard, getBoardDetail } from '@/api/board'
import { toast } from 'vue-sonner'

// 게시판 네비게이션 inject
const boardNavigation = inject('boardNavigation')
const route = useRoute()

// 폼 데이터
const form = ref({
  title: '',
  content: '',
  categoryId: ''
})

// 유효성 검사 에러
const errors = ref({})

// 상태 관리
const isSubmitting = ref(false)
const showLeaveModal = ref(false)
const isEditMode = ref(false)
const editingBoardId = ref(null)
const originalFormData = ref({})

// 사용 가능한 카테고리 (공지사항은 관리자만 작성 가능하다고 가정하고 제외)
const availableCategories = ref([
  { id: 2, name: '자유게시판' },
  { id: 3, name: '여행후기' },
  { id: 4, name: '질문게시판' }
])

// 폼 유효성 검사
const isFormValid = computed(() => {
  return form.value.title.trim() && 
         form.value.content.trim() && 
         form.value.categoryId &&
         Object.keys(errors.value).length === 0
})

// 폼 데이터 변경 감지
const hasFormChanged = computed(() => {
  return form.value.title !== originalFormData.value.title ||
         form.value.content !== originalFormData.value.content ||
         form.value.categoryId !== originalFormData.value.categoryId
})

// 실시간 유효성 검사
const validateForm = () => {
  errors.value = {}
  
  if (!form.value.title.trim()) {
    errors.value.title = '제목을 입력해주세요'
  } else if (form.value.title.length > 100) {
    errors.value.title = '제목은 100자 이하로 입력해주세요'
  }
  
  if (!form.value.content.trim()) {
    errors.value.content = '내용을 입력해주세요'
  } else if (form.value.content.length > 5000) {
    errors.value.content = '내용은 5000자 이하로 입력해주세요'
  }
  
  if (!form.value.categoryId) {
    errors.value.categoryId = '카테고리를 선택해주세요'
  }
}

// 폼 제출
const handleSubmit = async () => {
  validateForm()
  
  if (!isFormValid.value) {
    toast.error('입력한 정보를 확인해주세요')
    return
  }
  
  isSubmitting.value = true
  
  try {
    const boardData = {
      title: form.value.title.trim(),
      content: form.value.content.trim(),
      categoryId: parseInt(form.value.categoryId)
    }
    
    if (isEditMode.value && editingBoardId.value) {
      // 수정
      await updateBoard(editingBoardId.value, boardData)
      toast.success('게시글이 수정되었습니다')
    } else {
      // 새 글 작성
      await createBoard(boardData)
      toast.success('게시글이 작성되었습니다')
    }
    
    // 목록으로 돌아가기
    resetForm()
    if (boardNavigation) {
      boardNavigation.changeToListMode()
    }
    
  } catch (error) {
    console.error('게시글 처리 실패:', error)
    if (error.response?.status === 400) {
      toast.error('입력한 정보가 올바르지 않습니다')
    } else if (error.response?.status === 403) {
      toast.error('권한이 없습니다')
    } else {
      toast.error(isEditMode.value ? '게시글 수정에 실패했습니다' : '게시글 작성에 실패했습니다')
    }
  } finally {
    isSubmitting.value = false
  }
}

// 뒤로 가기
const goBack = () => {
  if (hasFormChanged.value) {
    showLeaveModal.value = true
  } else {
    confirmLeave()
  }
}

// 나가기 확인
const confirmLeave = () => {
  resetForm()
  showLeaveModal.value = false
  if (boardNavigation) {
    boardNavigation.changeToListMode()
  }
}

// 폼 초기화
const resetForm = () => {
  form.value = {
    title: '',
    content: '',
    categoryId: ''
  }
  errors.value = {}
  isEditMode.value = false
  editingBoardId.value = null
  originalFormData.value = {}
}

// 수정 모드 설정
const setEditMode = (board) => {
  if (!board) return
  
  isEditMode.value = true
  editingBoardId.value = board.boardId
  
  form.value = {
    title: board.title || '',
    content: board.content || '',
    categoryId: board.categoryId || ''
  }
  
  // 원본 데이터 저장
  originalFormData.value = { ...form.value }
}

// 폼 값 변경 감지하여 유효성 검사
watch(form, validateForm, { deep: true })

// 카테고리 변경 시 기본값 설정
watch(() => boardNavigation?.activeCategory?.value, (newCategory) => {
  if (newCategory && newCategory !== 0 && !isEditMode.value) {
    // 새 글 작성시에만 카테고리 자동 설정
    const category = availableCategories.value.find(cat => cat.id === newCategory)
    if (category) {
      form.value.categoryId = newCategory
    }
  }
})

// 수정할 게시글 감지
watch(() => boardNavigation?.editBoard, (editBoard) => {
  if (editBoard) {
    setEditMode(editBoard)
    // 수정 완료 후 editBoard 초기화
    boardNavigation.editBoard = null
  }
}, { immediate: true })

onMounted(() => {
  // 수정 모드 처리 (쿼리 파라미터에서 edit 값 확인)
  const editBoardId = route.query.edit
  if (editBoardId) {
    loadBoardForEdit(editBoardId)
  }
  
  // 현재 카테고리에 따른 기본 카테고리 설정
  if (boardNavigation?.activeCategory?.value && boardNavigation.activeCategory.value !== 0) {
    const category = availableCategories.value.find(cat => cat.id === boardNavigation.activeCategory.value)
    if (category && !isEditMode.value) {
      form.value.categoryId = boardNavigation.activeCategory.value
    }
  }
})

// 수정을 위한 게시글 로드
const loadBoardForEdit = async (boardId) => {
  try {
    const boardData = await getBoardDetail(boardId)
    setEditMode(boardData)
  } catch (error) {
    console.error('게시글 로드 실패:', error)
    toast.error('게시글을 로드할 수 없습니다')
  }
}
</script>
