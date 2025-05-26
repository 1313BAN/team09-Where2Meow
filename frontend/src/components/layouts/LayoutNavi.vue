<template>
  <nav
    :class="[
      'fixed top-0 left-0 right-0 w-full z-50 transition-all duration-300',
      scrolled ? 'bg-white shadow-md py-2' : 'bg-white py-2',
    ]"
  >
    <div class="container mx-auto px-4 sm:px-6 lg:px-8 max-w-7xl">
      <div class="flex justify-between items-center relative">
        <!-- 로고 -->
        <div class="text-xl font-bold">
          <RouterLink to="/" class="flex items-center gap-2 link-cat paw-cursor">
            <span
              class="flex items-center font-extrabold text-transparent bg-clip-text"
              style="background-image: var(--gradient-primary)"
            >
              <div class="cat-paw-mask w-8 h-8" style="color: var(--primary-color)"></div>
              <span>어디가냥</span>
            </span>
          </RouterLink>
        </div>

        <!-- 메인 메뉴 (데스크탑 기준) - 중앙 정렬 -->
        <!-- 높이 유지를 위한 플레이스홀더 -->
        <div class="flex-1 flex justify-center relative">
          <div class="hidden md:flex items-center space-x-8 text-base font-medium">
            <RouterLink
              v-for="item in menuItems"
              :key="item.path"
              :to="item.path"
              class="flex items-center gap-2 px-3 py-2 rounded-lg text-gray-70 font-semibold cursor-pointer"
            >
              <i :class="[item.icon, 'text-[var(--primary-color)]']"></i>
              <span>{{ item.label }}</span>
            </RouterLink>
          </div>
        </div>

        <!-- 우측 메뉴 -->
        <div class="flex items-center gap-3">
          <!-- 로그인 상태에 따른 버튼 -->
          <div v-if="!isLoggedIn" class="hidden md:block">
            <Button
              label="로그인"
              @click="handleLoginClick"
              pt:root="bg-gradient-to-r from-[var(--primary-color)] to-[var(--secondary-color)] active:scale-95 transition-all duration-200 ease-in-out rounded-xl px-6 py-2 shadow-md text-white border-none flex items-center gap-2 cursor-pointer"
              pt:label="text-white font-semibold text-base tracking-wide"
            />
          </div>

          <!-- 로그인된 경우 프로필 -->
          <div v-else class="hidden md:block relative" data-dropdown="profile">
            <button
              @click="profileDropdownOpen = !profileDropdownOpen"
              class="w-8 h-8 rounded-full flex items-center justify-center transition-colors cursor-pointer"
              style="background: var(--gradient-primary)"
              :title="`${userName}님`"
            >
              <span class="text-white text-sm font-medium">{{ userName.charAt(0) }}</span>
            </button>

            <!-- 프로필 드롭다운 - 말풍선 스타일 -->
            <div
              v-show="profileDropdownOpen"
              class="absolute right-0 top-full mt-3 w-44 bg-white rounded-lg shadow-xl border border-gray-100 py-1 z-50 dropdown-bubble"
            >
              <!-- 화살표 -->
              <div
                class="absolute -top-2 right-3 w-4 h-4 bg-white border-l border-t border-gray-100 transform rotate-45"
              ></div>

              <RouterLink
                to="/mypage"
                @click="profileDropdownOpen = false"
                class="flex items-center gap-3 px-4 py-3 text-gray-800 hover:bg-gray-50 transition-colors cursor-pointer font-semibold"
                style="font-family: 'PretendardVariable', sans-serif"
              >
                <i class="pi pi-user text-[var(--primary-color)] text-sm"></i>
                <span class="text-sm">마이페이지</span>
              </RouterLink>

              <hr class="my-1 border-gray-100" />

              <button
                @click="handleLogout"
                class="w-full flex items-center gap-3 px-4 py-3 text-gray-800 hover:bg-gray-50 transition-colors cursor-pointer font-semibold"
                style="font-family: 'PretendardVariable', sans-serif"
              >
                <i class="pi pi-sign-out text-[var(--danger-color)] text-sm"></i>
                <span class="text-sm">로그아웃</span>
              </button>
            </div>
          </div>

          <!-- 모바일 메뉴 버튼 -->
          <button
            @click="mobileMenuOpen = !mobileMenuOpen"
            class="cursor-pointer w-10 h-10 rounded-lg flex md:hidden items-center justify-center text-gray-700 transition-colors"
            aria-label="메뉴"
          >
            <i :class="[mobileMenuOpen ? 'pi pi-times' : 'pi pi-bars']"></i>
          </button>

          <!-- 모바일 프로필/로그인 버튼 -->
          <div class="md:hidden">
            <button
              v-if="isLoggedIn"
              @click="handleMobileProfileClick"
              class="w-8 h-8 rounded-full flex items-center justify-center transition-colors cursor-pointer"
              style="background: var(--gradient-primary)"
              :title="`${userName}님`"
            >
              <span class="text-white text-sm font-medium">{{ userName.charAt(0) }}</span>
            </button>
            <button
              v-else
              @click="handleLoginClick"
              class="w-8 h-8 rounded-full border border-gray-300 flex items-center justify-center hover:bg-gray-50 transition-colors cursor-pointer"
            >
              <i class="pi pi-user text-gray-600"></i>
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 모바일 메뉴 -->
    <div
      v-show="mobileMenuOpen"
      class="md:hidden bg-white absolute top-full left-0 right-0 shadow-lg border-t border-gray-100"
    >
      <div class="px-4 py-4">
        <!-- 모바일 메뉴 아이템들 -->
        <div class="space-y-1">
          <RouterLink
            v-for="item in menuItems"
            :key="item.path"
            :to="item.path"
            class="block px-3 py-2 text-gray-700 hover:bg-gray-50 rounded-lg font-semibold cursor-pointer"
            @click="mobileMenuOpen = false"
          >
            {{ item.label }}
          </RouterLink>
        </div>

        <!-- 모바일 로그인 버튼 (로그인 안된 경우) -->
        <div v-if="!isLoggedIn" class="mt-4 pt-4 border-t border-gray-100">
          <Button
            label="로그인"
            @click="handleLoginClick"
            class="w-full cursor-pointer text-white border-none rounded-full py-2 font-medium shadow-sm hover:shadow-md"
            style="background-image: var(--gradient-primary)"
          />
        </div>

        <!-- 모바일 사용자 메뉴 (로그인된 경우) -->
        <div v-else class="mt-4 pt-4 border-t border-gray-100 space-y-3">
          <div class="text-center text-sm text-gray-600 mb-3">{{ userName }}님 환영합니다</div>

          <!-- 모바일 로그아웃 버튼 - 디자인 개선 -->
          <Button
            label="로그아웃"
            @click="handleLogout"
            class="w-full cursor-pointer text-white border-none rounded-full py-2 font-medium shadow-sm hover:shadow-md"
            style="background: var(--danger-color)"
          />
        </div>
      </div>
    </div>
  </nav>

  <!-- 네비게이션 공간 확보 -->
  <div :class="['transition-all duration-300 h-14']"></div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { storeToRefs } from 'pinia'
import Button from 'primevue/button'
import { useAuthStore } from '@/stores/auth'
import { toast } from 'vue-sonner'

const router = useRouter()
const authStore = useAuthStore()

const scrolled = ref(false)
const mobileMenuOpen = ref(false)
const profileDropdownOpen = ref(false)

// 인증 상태 가져오기
const { isLoggedIn, userName } = storeToRefs(authStore)

// 메뉴 항목 정의
const menuItems = ref([
  { label: '일정', path: '/plan', icon: 'pi pi-calendar' },
  { label: '여행지', path: '/attraction', icon: 'pi pi-map-marker' },
  { label: '게시판', path: '/board', icon: 'pi pi-users' },
])

// 스크롤 이벤트 처리
function handleScroll() {
  scrolled.value = window.scrollY > 20
}

// 화면 사이즈 변경 처리
function handleResize() {
  if (window.innerWidth >= 768) {
    mobileMenuOpen.value = false
    profileDropdownOpen.value = false
  }
}

// 드롭다운 외부 클릭 처리
function handleClickOutside(event) {
  const dropdown = document.querySelector('[data-dropdown="profile"]')
  if (profileDropdownOpen.value && dropdown && !dropdown.contains(event.target)) {
    profileDropdownOpen.value = false
  }
}

// 로그인 버튼 클릭
const handleLoginClick = () => {
  router.push('/login')
  mobileMenuOpen.value = false
}

// 모바일 프로필 클릭 - 바로 마이페이지로 이동
const handleMobileProfileClick = () => {
  router.push('/mypage')
}

// 로그아웃 처리
const handleLogout = async () => {
  try {
    await authStore.logout()
    toast.success('로그아웃되었습니다')
    router.push('/')
    profileDropdownOpen.value = false
    mobileMenuOpen.value = false
  } catch (error) {
    console.error('로그아웃 실패:', error)
    toast.error('로그아웃 중 오류가 발생했습니다')
  }
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
  window.addEventListener('resize', handleResize)
  window.addEventListener('click', handleClickOutside)
  handleScroll()

  // 인증 상태 확인
  authStore.checkAuthStatus()
})

onBeforeUnmount(() => {
  window.removeEventListener('scroll', handleScroll)
  window.removeEventListener('resize', handleResize)
  window.removeEventListener('click', handleClickOutside)
})
</script>

<style scoped>
.cat-paw-mask {
  background-color: #00edb3;
  mask: url('@/assets/cat-paw.png') no-repeat center;
  mask-size: contain;
  -webkit-mask: url('@/assets/cat-paw.png') no-repeat center;
  -webkit-mask-size: contain;
}

.dropdown-bubble {
  box-shadow:
    0 10px 25px -5px rgba(0, 0, 0, 0.1),
    0 10px 10px -5px rgba(0, 0, 0, 0.04);
}
</style>
