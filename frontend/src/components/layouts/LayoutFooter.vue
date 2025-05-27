<template>
  <footer
    class="bg-[var(--dark-color)] text-white py-12 px-4 cat-pattern-bg relative overflow-hidden"
  >
    <div class="container max-w-7xl mx-auto relative z-10">
      <!-- 메인 푸터 콘텐츠 -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8 mb-8">
        <!-- 회사 정보 -->
        <div class="footer-column">
          <h3 class="text-lg font-semibold mb-6 text-[var(--primary-color)]">
            <span class="flex items-center gap-2">
              <i class="pi pi-home"></i>
              <span>어디가냥</span>
            </span>
          </h3>
          <p class="text-sm text-gray-500 mb-4">© 2025 Where2Meow. All rights reserved.</p>
          <p class="text-sm text-gray-500 mb-6 leading-relaxed">
            가냥이와 함께 여행 계획을 짜는 사이트로, 여행자의 리뷰와 의견을 바탕으로 최적의 여행
            계획을 만들어 드립니다.
          </p>

          <!-- 소셜 미디어 아이콘 -->
          <div class="flex gap-4">
            <a
              v-for="social in socialLinks"
              :key="social.name"
              :href="social.url"
              class="w-10 h-10 bg-gray-700 rounded-full flex items-center justify-center text-sm transition-all duration-300 paw-cursor"
              :aria-label="social.name"
            >
              <i :class="social.icon"></i>
            </a>
          </div>
        </div>

        <!-- 서비스 메뉴 -->
        <div class="footer-column">
          <h3 class="text-lg font-semibold mb-6 text-[var(--primary-color)]">
            <span class="flex items-center gap-2">
              <i class="pi pi-cog"></i>
              <span>서비스</span>
            </span>
          </h3>
          <div class="space-y-3">
            <a
              v-for="service in serviceLinks"
              :key="service.name"
              :href="service.url"
              class="text-sm text-gray-500 transition-colors paw-cursor flex items-center gap-2"
            >
              <i class="pi pi-angle-right text-[var(--primary-color)]"></i>
              {{ service.name }}
            </a>
          </div>
        </div>

        <!-- 링크 메뉴 -->
        <div class="footer-column">
          <h3 class="text-lg font-semibold mb-6 text-[var(--primary-color)]">
            <span class="flex items-center gap-2">
              <i class="pi pi-link"></i>
              <span>링크</span>
            </span>
          </h3>
          <div class="space-y-3">
            <a
              v-for="link in generalLinks"
              :key="link.name"
              :href="link.url"
              class="text-sm text-gray-500 transition-colors paw-cursor flex items-center gap-2"
            >
              <i class="pi pi-angle-right text-[var(--primary-color)]"></i>
              {{ link.name }}
            </a>
          </div>
        </div>

        <!-- 지원 메뉴 -->
        <div class="footer-column">
          <h3 class="text-lg font-semibold mb-6 text-[var(--primary-color)]">
            <span class="flex items-center gap-2">
              <i class="pi pi-question-circle"></i>
              <span>지원</span>
            </span>
          </h3>
          <div class="space-y-3">
            <a
              v-for="support in supportLinks"
              :key="support.name"
              :href="support.url"
              class="text-sm text-gray-500 transition-colors paw-cursor flex items-center gap-2"
            >
              <i class="pi pi-angle-right text-[var(--primary-color)]"></i>
              {{ support.name }}
            </a>
          </div>
        </div>
      </div>

      <!-- 하단 저작권 및 맨 위로 버튼 -->
      <div
        class="flex flex-col md:flex-row justify-between items-center pt-8 border-t border-gray-600"
      >
        <div class="text-xs text-gray-500 mb-4 md:mb-0">
          © 2025 Where2Meow. 가냥이와 함께하는 여행의 모든 것.
        </div>

        <!-- 맨 위로 버튼 -->
        <button
          @click="scrollToTop"
          class="w-12 h-12 bg-[var(--primary-color)] rounded-full flex items-center justify-center text-white transform transition-all shadow-lg cursor-pointer"
          aria-label="맨 위로"
        >
          <i class="pi pi-chevron-up"></i>
        </button>
      </div>
    </div>
  </footer>
</template>

<script setup>
import { ref } from 'vue'

// 소셜 미디어 링크
const socialLinks = ref([
  {
    name: 'Facebook',
    icon: 'pi pi-facebook',
    url: '#',
  },
  {
    name: 'Instagram',
    icon: 'pi pi-instagram',
    url: '#',
  },
  {
    name: 'Twitter',
    icon: 'pi pi-twitter',
    url: '#',
  },
])

// 서비스 링크
const serviceLinks = ref([
  {
    name: '서비스 이용약관',
    url: '#',
  },
  {
    name: '개인정보 처리방침',
    url: '#',
  },
  {
    name: '회사 정보',
    url: '#',
  },
  {
    name: '제휴 문의',
    url: '#',
  },
])

// 일반 링크
const generalLinks = ref([
  {
    name: 'FAQ',
    url: '#',
  },
  {
    name: '뉴스',
    url: '#',
  },
  {
    name: '블로그',
    url: '#',
  },
  {
    name: '연락처',
    url: '#',
  },
])

// 지원 링크
const supportLinks = ref([
  {
    name: '이용안내',
    url: '#',
  },
  {
    name: '고객센터',
    url: '#',
  },
  {
    name: '자주하는질문',
    url: '#',
  },
  {
    name: '고객지원',
    url: '#',
  },
])

// 맨 위로 스크롤 함수
const scrollToTop = () => {
  window.scrollTo({
    top: 0,
    behavior: 'smooth',
  })
}
</script>

<style scoped>
/* 푸터 컨테이너 */
.container {
  max-width: 1200px;
}

/* 푸터 컬럼 반응형 */
.footer-column h3 {
  position: relative;
}

.footer-column h3::after {
  content: '';
  position: absolute;
  bottom: -10px;
  left: 0;
  width: 30px;
  height: 2px;
  background-color: var(--primary-color);
}

/* 링크 호버 효과 */
.footer-column a {
  position: relative;
  overflow: hidden;
  transition: color var(--transition-normal);
}

.footer-column a:hover {
  color: var(--primary-color) !important;
}

.footer-column a::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.1), transparent);
  transition: left 0.5s;
}

.footer-column a:hover::before {
  left: 100%;
}

/* 소셜 미디어 아이콘 */
.footer-column .flex a {
  transition: all 0.3s ease;
}

.footer-column .flex a:hover {
  transform: translateY(-2px);
  background-color: var(--primary-color) !important;
  color: white !important;
}

/* 맨 위로 버튼 호버 효과 */
button[aria-label='맨 위로'] {
  background-color: var(--primary-color) !important;
  transition:
    transform var(--transition-normal),
    background-color var(--transition-normal);
}

button[aria-label='맨 위로']:hover {
  transform: translateY(-3px);
  background-color: var(--secondary-color) !important;
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .grid-cols-4 {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 640px) {
  .grid-cols-4 {
    grid-template-columns: 1fr;
  }

  .flex-row {
    flex-direction: column;
  }

  .text-right {
    text-align: center;
  }
}
</style>
