<template>
  <section
    class="relative h-[500px] bg-cover bg-center text-white flex items-center overflow-hidden"
    :style="{ backgroundImage: `url(${bannerImage})` }"
  >
    <!-- 배경 오버레이 -->
    <div class="absolute inset-0 bg-gradient-to-b from-black/40 to-black/70"></div>

    <div class="container mx-auto px-4 sm:px-6 lg:px-8 relative z-10 max-w-7xl">
      <div class="banner-content max-w-2xl p-8">
        <!-- 메인 제목 -->
        <h1 class="text-4xl font-bold mb-4 leading-tight text-shadow-lg">
          {{ mainTitle }}
        </h1>

        <!-- 고양이 -->
        <div class="flex items-center gap-2 mb-4">
          <span class="text-[var(--accent-color)] text-lg">복잡한 일정도, 가냥이가 척척!</span>
        </div>

        <!-- 부제목 -->
        <p class="text-xl mb-8 text-shadow-sm">
          {{ subtitle }}
        </p>

        <!-- 검색 폼 -->
        <form
          @submit.prevent="handleSearch"
          class="flex gap-3 w-full bg-white/10 backdrop-blur-md p-3 rounded-2xl shadow-lg border border-white/20"
        >
          <div class="flex-1 relative">
            <i
              class="pi pi-pencil absolute left-4 top-1/2 transform -translate-y-1/2 text-[var(--primary-color)]"
            ></i>
            <InputText
              v-model="searchQuery"
              :placeholder="searchPlaceholder"
              class="w-full py-3 pl-11 pr-4 rounded-xl border-0 shadow-inner text-white/80"
              size="large"
            />
          </div>
          <Button
            label="생성하기"
            :loading="isSearching"
            @click="handleSearch"
            pt:root="bg-gradient-to-r from-[var(--primary-color)] to-[var(--secondary-color)] active:scale-95 transition-all duration-200 ease-in-out rounded-xl px-6 py-2 shadow-md text-white border-none flex items-center gap-2 cursor-pointer"
            pt:label="text-white font-semibold text-base tracking-wide"
          />
        </form>

        <!-- 타입라이터 효과 -->
        <div class="mt-6 h-6 overflow-hidden">
          <TypewriterText
            :texts="typewriterTexts"
            :typing-speed="100"
            :deleting-speed="50"
            :pause-duration="2000"
            text-class="text-sm text-white/80"
          />
        </div>
      </div>
    </div>

    <!-- 스크롤 다운 버튼 -->
    <button
      @click="scrollDown"
      class="absolute bottom-6 left-1/2 transform -translate-x-1/2 flex flex-col items-center text-white/70 transition-colors cursor-pointer"
    >
      <span class="text-sm mb-2">더 알아보기</span>
      <i class="pi pi-chevron-down animate-bounce text-xl"></i>
    </button>
  </section>
</template>

<script setup>
import { ref } from 'vue'
import InputText from 'primevue/inputtext'
import Button from 'primevue/button'
import TypewriterText from './TypewriterText.vue'

// Props 정의
const props = defineProps({
  bannerImage: {
    type: String,
    default:
      'https://images.unsplash.com/photo-1580137189272-c9379f8864fd?q=80&w=1920&h=1200&fit=crop',
  },
  mainTitle: {
    type: String,
    default: '어디가냥과 함께하는 스마트한 여행 계획',
  },
  subtitle: {
    type: String,
    default: '가냥이와 떠나는, 똑똑하고 귀여운 여행 준비',
  },
  searchPlaceholder: {
    type: String,
    default: '예: 서울 2박 3일 여행 일정 짜줘',
  },
})

// 반응형 데이터
const searchQuery = ref('')
const isSearching = ref(false)

// 타입라이터 텍스트 배열
const typewriterTexts = [
  '여행 계획, 어디서부터 시작할지 모르겠다면?',
  '가냥이가 똑똑하게 일정을 짜드릴게요!',
  '"제주 2박 3일 추천 코스"처럼 물어보세요',
  '지역별 핫플, 교통, 맛집까지 한 번에!',
  '가냥이와 함께 스마트한 여행 준비 시작해볼까요?',
  '떠나고 싶은 도시를 입력해보세요!',
  '혼자 떠나는 여행도, 친구랑 가는 여행도 OK!',
]

// 스크롤 다운 함수
const scrollDown = () => {
  const nextSection = document.querySelector('.main-view > :nth-child(2)')
  if (nextSection) {
    const offset = 56 // h-14 = 56px
    const top = nextSection.getBoundingClientRect().top + window.scrollY - offset

    window.scrollTo({
      top,
      behavior: 'smooth',
    })
  }
}

// Events
const emit = defineEmits(['search'])

// 검색 핸들러
const handleSearch = async () => {
  if (!searchQuery.value.trim()) return

  isSearching.value = true

  try {
    emit('search', searchQuery.value.trim())
    console.log('검색어:', searchQuery.value)
  } finally {
    isSearching.value = false
  }
}
</script>

<style scoped>
/* 컨테이너 스타일 */
.container {
  max-width: 1200px;
}

/* 배너 콘텐츠 애니메이션 */
.banner-content {
  animation: fadeInUp 1s ease-out;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 텍스트 샤도우 효과 */
.text-shadow-lg {
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
}

.text-shadow-sm {
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .banner-content {
    padding: 1.5rem;
    max-width: 100%;
  }

  h1 {
    font-size: 2rem;
    line-height: 1.2;
  }

  p {
    font-size: 1rem;
  }
}
</style>
