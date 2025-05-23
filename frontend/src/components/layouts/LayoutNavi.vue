<template>
  <nav
    :class="[
      'fixed top-0 left-0 right-0 w-full z-50 transition-all duration-300',
      scrolled ? 'bg-white shadow-md py-2' : 'bg-white py-2',
    ]"
  >
    <div class="max-w-7xl mx-auto px-4 flex justify-between items-center">
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

      <!-- 메인 메뉴 (데스크탑 기준) -->
      <div class="space-x-8 text-base font-medium hidden md:flex">
        <RouterLink
          v-for="item in menuItems"
          :key="item.path"
          :to="item.path"
          class="flex items-center gap-2 px-3 py-2 rounded-lg text-gray-70 font-semibold"
        >
          <i :class="[item.icon, 'text-[var(--primary-color)]']"></i>
          <span>{{ item.label }}</span>
        </RouterLink>
      </div>

      <!-- 우측 메뉴 -->
      <div class="flex items-center gap-3">
        <!-- 로그인 상태에 따른 버튼 -->
        <div v-if="!isLoggedIn" class="hidden md:block">
          <Button
            label="로그인"
            class="cursor-pointer text-white border-none rounded-lg px-5 py-2 font-medium transition-all duration-200 shadow-sm hover:shadow-md"
            style="background-image: var(--gradient-primary)"
          />
        </div>

        <!-- 로그인된 경우 프로필 -->
        <div v-else class="hidden md:block">
          <button class="w-8 h-8 rounded-full bg-orange-400 flex items-center justify-center">
            <span class="text-white text-sm font-medium">사</span>
          </button>
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
            class="w-8 h-8 rounded-full bg-orange-400 flex items-center justify-center"
          >
            <span class="text-white text-sm font-medium">사</span>
          </button>
          <button
            v-else
            class="w-8 h-8 rounded-full border border-gray-300 flex items-center justify-center"
          >
            <i class="pi pi-user text-gray-600"></i>
          </button>
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
            class="block px-3 py-2 text-gray-700 hover:bg-gray-50 rounded-lg font-medium"
            @click="mobileMenuOpen = false"
          >
            {{ item.label }}
          </RouterLink>
        </div>

        <!-- 모바일 로그인 버튼 (로그인 안된 경우) -->
        <div v-if="!isLoggedIn" class="mt-4 pt-4 border-t border-gray-100">
          <Button
            label="로그인"
            class="w-full cursor-pointer text-white border-none rounded-full py-2 font-medium shadow-sm hover:shadow-md"
            style="background-image: var(--gradient-primary)"
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
import { RouterLink } from 'vue-router'
import Button from 'primevue/button'

const scrolled = ref(false)
const mobileMenuOpen = ref(false)

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
  }
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
  window.addEventListener('resize', handleResize)
  handleScroll()
})

onBeforeUnmount(() => {
  window.removeEventListener('scroll', handleScroll)
  window.removeEventListener('resize', handleResize)
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
</style>
