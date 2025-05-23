<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { RouterLink } from 'vue-router'
import Button from 'primevue/button'
import InputText from 'primevue/inputtext'

const scrolled = ref(false)
const mobileMenuOpen = ref(false)

// 메뉴 항목 정의
const menuItems = ref([
  { label: '일정', path: '/schedule', icon: 'pi pi-calendar' },
  { label: '여행 정보', path: '/info', icon: 'pi pi-info-circle' },
  { label: '핫플레이스', path: '/hotplace', icon: 'pi pi-map-marker' },
  { label: '커뮤니티', path: '/community', icon: 'pi pi-users' },
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

<template>
  <nav
    :class="[
      'fixed top-0 left-0 right-0 w-full z-50 transition-all duration-300',
      scrolled ? 'bg-white shadow-md py-2' : 'bg-transparent py-3',
    ]"
  >
    <div class="max-w-7xl mx-auto px-4 flex justify-between items-center">
      <!-- 로고 -->      
      <div class="text-xl font-bold">
        <RouterLink to="/" class="flex items-center gap-2 link-cat paw-cursor">
          <span class="text-[var(--primary-color)] flex items-center">
            <i class="pi pi-home text-xl mr-1"></i>
            <span class="font-bold">어디가</span>
          </span>
          <span class="text-[var(--secondary-color)] flex items-center">
            <span class="font-bold">냥</span>
            <i class="pi pi-heart-fill text-[var(--primary-color)] text-sm ml-1 animate-pulse"></i>
          </span>
        </RouterLink>
      </div>

      <!-- 메인 메뉴 -->      
      <div class="space-x-6 text-sm font-medium hidden md:flex">
        <RouterLink 
          v-for="item in menuItems" 
          :key="item.path"
          :to="item.path" 
          class="link-cat paw-cursor flex items-center gap-1 py-1 px-2 rounded-lg hover:bg-[var(--light-color)]"
          active-class="text-[var(--primary-color)] font-bold"
        >
          <i :class="[item.icon, 'text-[var(--primary-color)]']"></i>
          <span>{{ item.label }}</span>
        </RouterLink>
      </div>

      <!-- 사용자 메뉴 -->      
      <div class="flex items-center gap-3">
        <!-- 검색 버튼 -->
        <button 
          class="w-9 h-9 rounded-full flex items-center justify-center text-[var(--primary-color)] hover:bg-[var(--light-color)] transition-colors paw-cursor hidden md:flex"
          aria-label="검색"
        >
          <i class="pi pi-search"></i>
        </button>

        <!-- 로그인 버튼 -->        
        <Button
          label="로그인"
          icon="pi pi-sign-in"
          iconPos="left"
          class="paw-cursor"
          pt:root="bg-gradient-to-r from-[var(--primary-color)] to-[var(--secondary-color)] hover:from-[#FF7A54] hover:to-[#4E249A] active:scale-95 transition-all duration-200 ease-in-out rounded-full px-6 py-2 shadow-md text-white border-none flex items-center gap-2 cursor-pointer"
          pt:label="text-white font-semibold text-base tracking-wide"
        />

        <!-- 모바일 메뉴 버튼 -->
        <button 
          @click="mobileMenuOpen = !mobileMenuOpen" 
          class="w-10 h-10 rounded-lg flex md:hidden items-center justify-center text-[var(--primary-color)] hover:bg-[var(--light-color)] transition-colors paw-cursor"
          aria-label="메뉴"
        >
          <i :class="[mobileMenuOpen ? 'pi pi-times' : 'pi pi-bars']"></i>
        </button>
      </div>
    </div>

    <!-- 모바일 메뉴 -->    
    <div 
      v-show="mobileMenuOpen" 
      class="md:hidden bg-white absolute top-full left-0 right-0 shadow-lg py-4 px-6 z-40 cat-pattern-bg"
    >
      <div class="flex flex-col gap-4">
        <RouterLink 
          v-for="item in menuItems" 
          :key="item.path"
          :to="item.path" 
          class="flex items-center gap-2 p-3 rounded-lg hover:bg-white/50 transition-colors paw-cursor"
          @click="mobileMenuOpen = false"
        >
          <i :class="[item.icon, 'text-[var(--primary-color)] text-xl']"></i>
          <span class="font-medium">{{ item.label }}</span>
        </RouterLink>

        <div class="relative mt-2 rounded-lg overflow-hidden">
          <InputText
            placeholder="검색어를 입력하세요"
            class="w-full p-3 pr-10 bg-white/80"
          />
          <button class="absolute right-3 top-1/2 transform -translate-y-1/2 text-[var(--primary-color)]">
            <i class="pi pi-search"></i>
          </button>
        </div>
      </div>
    </div>
  </nav>
  
  <!-- 네비게이션 공간 확보 -->
  <div :class="['transition-all duration-300', scrolled ? 'h-14' : 'h-16']"></div>
</template>
