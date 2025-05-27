<template>
  <div class="min-h-screen bg-gray-50">
    <div class="container mx-auto px-4 py-8">
      <div class="max-w-7xl mx-auto">
        <!-- 페이지 헤더 -->
        <div class="mb-8">
          <h1 class="text-3xl font-bold text-gray-900">마이페이지</h1>
          <p class="text-gray-600 mt-2">계정 정보를 관리하고 활동 내역을 확인하세요</p>
        </div>

        <div class="flex gap-8">
          <!-- 좌측 사이드바 -->
          <div class="w-64 flex-shrink-0">
            <div class="bg-white rounded-lg shadow-sm border border-gray-200">
              <!-- 프로필 섹션 -->
              <div class="p-6 border-b border-gray-200">
                <div class="flex items-center gap-3">
                  <div
                    class="w-12 h-12 rounded-full flex items-center justify-center"
                    style="background: var(--gradient-primary)"
                  >
                    <span class="text-white text-lg font-bold">{{
                      userName.charAt(0) || '?'
                    }}</span>
                  </div>
                  <div>
                    <h3 class="font-semibold text-gray-900">{{ userName }}</h3>
                    <p class="text-sm text-gray-600">{{ userEmail }}</p>
                  </div>
                </div>
              </div>

              <!-- 메뉴 섹션 -->
              <div class="p-4">
                <div class="space-y-1">
                  <button
                    v-for="item in menuItems"
                    :key="item.key"
                    @click="activeMenu = item.key"
                    :class="[
                      'w-full flex items-center gap-3 px-3 py-2 rounded-lg text-left transition-colors cursor-pointer',
                      activeMenu === item.key
                        ? 'bg-[var(--primary-10)] text-[var(--primary-dark)] font-semibold'
                        : 'text-gray-700 hover:bg-gray-50',
                    ]"
                  >
                    <i
                      :class="[
                        item.icon,
                        activeMenu === item.key ? 'text-[var(--primary-dark)]' : 'text-gray-400',
                      ]"
                    ></i>
                    <span>{{ item.label }}</span>
                  </button>
                </div>

                <!-- 계정 관리 섹션 -->
                <div class="mt-6 pt-4 border-t border-gray-200">
                  <h4 class="text-sm font-medium text-gray-500 mb-2">계정 관리</h4>
                  <div class="space-y-1">
                    <button
                      @click="handleLogout"
                      class="w-full flex items-center gap-3 px-3 py-2 rounded-lg text-left text-gray-700 hover:bg-gray-50 transition-colors cursor-pointer"
                    >
                      <i class="pi pi-sign-out text-gray-400"></i>
                      <span>로그아웃</span>
                    </button>
                    <button
                      @click="showDeleteAccountModal = true"
                      class="w-full flex items-center gap-3 px-3 py-2 rounded-lg text-left text-red-600 hover:bg-red-50 transition-colors cursor-pointer"
                    >
                      <i class="pi pi-user-minus text-red-500"></i>
                      <span>회원탈퇴</span>
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 우측 컨텐츠 영역 -->
          <div class="flex-1">
            <div class="bg-white rounded-lg shadow-sm border border-gray-200">
              <!-- 컴포넌트 동적 렌더링 -->
              <component :is="currentComponent" />
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 회원탈퇴 확인 모달 -->
    <div
      v-if="showDeleteAccountModal"
      class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50"
      @click="showDeleteAccountModal = false"
    >
      <div class="bg-white rounded-lg p-6 max-w-md w-full mx-4" @click.stop>
        <div class="text-center">
          <div
            class="w-16 h-16 rounded-full bg-red-100 flex items-center justify-center mx-auto mb-4"
          >
            <i class="pi pi-exclamation-triangle text-red-500 text-2xl"></i>
          </div>
          <h3 class="text-lg font-semibold text-gray-900 mb-2">회원탈퇴</h3>
          <p class="text-gray-600 mb-6">
            정말로 회원탈퇴를 하시겠습니까?<br />
            탈퇴 후에는 모든 데이터가 삭제되며 복구할 수 없습니다.
          </p>
          <div class="flex gap-3">
            <button
              @click="showDeleteAccountModal = false"
              class="flex-1 px-4 py-2 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50 transition-colors cursor-pointer"
            >
              취소
            </button>
            <button
              @click="handleDeleteAccount"
              :disabled="isDeleting"
              class="flex-1 px-4 py-2 bg-red-500 text-white rounded-lg hover:bg-red-600 disabled:opacity-50 disabled:cursor-not-allowed transition-colors cursor-pointer"
            >
              <span v-if="!isDeleting">탈퇴하기</span>
              <span v-else class="flex items-center justify-center gap-2">
                <i class="pi pi-spinner pi-spin"></i>
                처리 중...
              </span>
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { storeToRefs } from 'pinia'
import { toast } from 'vue-sonner'
import { deleteUser } from '@/api/user'

// 컴포넌트 import
import ProfileSettings from '@/components/views/mypage/ProfileSettings.vue'
import MyPosts from '@/components/views/mypage/MyPosts.vue'
import MyComments from '@/components/views/mypage/MyComments.vue'
import BookmarkedPosts from '@/components/views/mypage/BookmarkedPosts.vue'

const router = useRouter()
const authStore = useAuthStore()
const { userName, userEmail, userUuid } = storeToRefs(authStore)

// 상태 관리
const activeMenu = ref('profile')
const showDeleteAccountModal = ref(false)
const isDeleting = ref(false)

// 메뉴 항목
const menuItems = ref([
  { key: 'profile', label: '프로필 설정', icon: 'pi pi-user' },
  { key: 'posts', label: '내가 쓴 글', icon: 'pi pi-file-edit' },
  { key: 'comments', label: '내가 쓴 댓글', icon: 'pi pi-comment' },
  { key: 'bookmarks', label: '북마크한 게시글', icon: 'pi pi-bookmark' },
])

// 현재 활성화된 컴포넌트
const currentComponent = computed(() => {
  const components = {
    profile: ProfileSettings,
    posts: MyPosts,
    comments: MyComments,
    bookmarks: BookmarkedPosts,
  }
  return components[activeMenu.value]
})

// 로그아웃 처리
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

// 회원탈퇴 처리
const handleDeleteAccount = async () => {
  if (!userUuid.value) {
    toast.error('사용자 정보를 찾을 수 없습니다')
    return
  }

  isDeleting.value = true

  try {
    await deleteUser(userUuid.value)
    await authStore.logout()
    toast.success('회원탈퇴가 완료되었습니다')
    router.push('/')
  } catch (error) {
    console.error('회원탈퇴 실패:', error)
    toast.error('회원탈퇴 중 오류가 발생했습니다')
  } finally {
    isDeleting.value = false
    showDeleteAccountModal.value = false
  }
}
</script>
