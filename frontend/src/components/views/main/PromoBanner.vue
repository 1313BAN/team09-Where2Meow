<template>
  <section class="relative py-16 px-4 overflow-hidden" :style="{ backgroundColor: bgColor }">
    <!-- 배경 도형들 -->
    <div class="absolute top-0 right-0 w-48 h-48 rounded-full opacity-70 transform translate-x-12 -translate-y-12"
         :style="{ backgroundColor: shape1Color }"></div>
    <div class="absolute bottom-0 right-48 w-36 h-36 rounded-full opacity-70 transform translate-y-12"
         :style="{ backgroundColor: shape2Color }"></div>
    
    <div class="container max-w-7xl mx-auto relative z-10">
      <div class="flex flex-col lg:flex-row items-center">
        <!-- 좌측 콘텐츠 -->
        <div class="flex-1 max-w-lg mb-8 lg:mb-0 lg:pr-8">
          <!-- 배지 -->
          <div class="flex items-center mb-6">
            <img
              v-if="badgeImage"
              :src="badgeImage"
              :alt="badgeText"
              class="w-8 h-8 mr-3"
            />
            <Tag
              v-else
              :value="badgeText"
              severity="success"
              class="mr-3"
            />
            <h3 class="text-lg font-semibold">{{ badgeText }}</h3>
          </div>
          
          <!-- 메인 제목 -->
          <h2 class="text-3xl lg:text-4xl font-bold mb-4 text-gray-900">
            {{ title }}
          </h2>
          
          <!-- 설명 -->
          <p class="text-gray-600 mb-8 leading-relaxed">
            {{ description }}
          </p>
          
          <!-- 액션 버튼 -->
          <CommonButton
            :label="buttonText"
            severity="contrast"
            size="large"
            rounded
            @click="handleButtonClick"
            custom-class="px-8 py-3"
          />
        </div>
        
        <!-- 우측 이미지들 -->
        <div class="flex-1 relative h-64 lg:h-80">
          <div
            v-for="(image, index) in images"
            :key="index"
            class="absolute rounded-lg overflow-hidden shadow-lg"
            :style="getImageStyle(index)"
          >
            <img
              :src="image.src"
              :alt="image.alt"
              class="w-full h-full object-cover"
              :style="{ width: image.width, height: image.height }"
            />
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import Tag from 'primevue/tag'
import CommonButton from '@/components/common/CommonButton.vue'

// Props
const props = defineProps({
  badgeImage: {
    type: String,
    default: ''
  },
  badgeText: {
    type: String,
    default: "Travellers' Choice 어워드"
  },
  title: {
    type: String,
    default: '베스트 오브 베스트'
  },
  description: {
    type: String,
    default: '여행자가 선정한 전세계 1%에 속하는 공항, 액티비티, 음식점, 호텔 등'
  },
  buttonText: {
    type: String,
    default: '자세히 보기'
  },
  bgColor: {
    type: String,
    default: '#fff6d5'
  },
  shape1Color: {
    type: String,
    default: '#ffc107'
  },
  shape2Color: {
    type: String,
    default: '#4caf50'
  },
  images: {
    type: Array,
    default: () => [
      {
        src: 'https://images.unsplash.com/photo-1566073771259-6a8506099945?w=300&h=200&fit=crop',
        alt: '여행 이미지 1',
        width: '280px',
        height: '200px'
      },
      {
        src: 'https://images.unsplash.com/photo-1469474968028-56623f02e42e?w=250&h=180&fit=crop',
        alt: '여행 이미지 2',
        width: '240px',
        height: '180px'
      }
    ]
  }
})

// Events
const emit = defineEmits(['button-click'])

// 이미지 위치 스타일 계산
const getImageStyle = (index) => {
  const positions = [
    { right: '20%', bottom: '15%', zIndex: 1 },
    { right: '10%', bottom: '20%', zIndex: 2 }
  ]
  
  return positions[index] || {}
}

// 버튼 클릭 핸들러
const handleButtonClick = () => {
  emit('button-click')
  console.log('프로모 배너 버튼 클릭')
}
</script>

<style scoped>
/* 애니메이션 효과 */
.container > div {
  animation: fadeInUp 0.8s ease-out;
}

/* 이미지 호버 효과 */
.absolute img {
  transition: transform 0.3s ease;
}

.absolute:hover img {
  transform: scale(1.05);
}

/* 반응형 디자인 */
@media (max-width: 1024px) {
  .flex-1:last-child {
    display: none;
  }
}

@media (max-width: 768px) {
  h2 {
    font-size: 2rem;
  }
  
  .py-16 {
    padding-top: 3rem;
    padding-bottom: 3rem;
  }
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
</style>