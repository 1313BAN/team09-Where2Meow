<template>
  <div :class="['cat-card paw-cursor', cardClass]" @click="handleClick">
    <!-- 이미지 섹션 -->
    <div v-if="image" class="relative overflow-hidden rounded-t-xl">
      <!-- 고양이 발바닥 아이콘 마커 -->
      <div class="absolute top-3 right-3 z-20 bg-white/80 rounded-full p-1 shadow-md">
        <i class="pi pi-heart text-[var(--primary-color)]"></i>
      </div>
      
      <!-- 이미지 -->
      <img 
        :src="image" 
        :alt="imageAlt" 
        class="w-full object-cover transition-transform duration-500" 
        :class="[imageClass, 'hover:scale-110']"
      />
      
      <!-- 오버레이 -->
      <div
        v-if="$slots.overlay"
        class="absolute inset-0 bg-gradient-to-t from-black/80 via-black/40 to-transparent flex items-end"
      >
        <slot name="overlay" />
      </div>
    </div>

    <!-- 컨텐츠 섹션 -->
    <div v-if="title || subtitle || content || $slots.content || $slots.title || $slots.subtitle" 
         class="p-4 bg-white rounded-b-xl">
      <!-- 제목 -->
      <div v-if="title || $slots.title" class="mb-2">
        <slot name="title">
          <h3 :class="['font-bold', titleClass]">{{ title }}</h3>
        </slot>
      </div>

      <!-- 부제목 -->
      <div v-if="subtitle || $slots.subtitle" class="mb-2">
        <slot name="subtitle">
          <p :class="subtitleClass">{{ subtitle }}</p>
        </slot>
      </div>

      <!-- 내용 -->
      <div v-if="content || $slots.content">
        <slot name="content">
          <p :class="contentClass">{{ content }}</p>
        </slot>
      </div>

      <!-- 푸터 -->
      <div v-if="$slots.footer" class="mt-3 pt-3 border-t border-gray-100">
        <slot name="footer" />
      </div>
    </div>
  </div>
</template>

<script setup>
// Props 정의
const props = defineProps({
  title: {
    type: String,
    default: '',
  },
  subtitle: {
    type: String,
    default: '',
  },
  content: {
    type: String,
    default: '',
  },
  image: {
    type: String,
    default: '',
  },
  imageAlt: {
    type: String,
    default: '',
  },
  clickable: {
    type: Boolean,
    default: false,
  },
  cardClass: {
    type: String,
    default: '',
  },
  titleClass: {
    type: String,
    default: 'text-lg text-gray-900',
  },
  subtitleClass: {
    type: String,
    default: 'text-sm text-gray-600',
  },
  contentClass: {
    type: String,
    default: 'text-gray-700',
  },
  imageClass: {
    type: String,
    default: 'h-48',
  },
})

// Events 정의
const emit = defineEmits(['click'])

// 클릭 핸들러
const handleClick = (event) => {
  if (props.clickable) {
    emit('click', event)
  }
}
</script>

<style scoped>
.cat-card {
  transition: all var(--transition-normal);
  position: relative;
  overflow: hidden;
  box-shadow: 0 6px 15px rgba(0, 0, 0, 0.1);
}

.cat-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.15);
}

/* 클릭 가능한 카드 스타일 */
.paw-cursor {
  cursor: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="%23FF8C69"><path d="M17.5,2C15.566,2,14,3.566,14,5.5c0,0.986,0.49,1.912,1.243,2.517C12.355,8.624,10,11.313,10,14.5 C10,18.09,12.91,21,16.5,21s6.5-2.91,6.5-6.5c0-3.187-2.355-5.876-5.243-6.483C18.51,7.412,19,6.486,19,5.5C19,3.566,17.434,2,15.5,2 z M6.5,2C4.566,2,3,3.566,3,5.5c0,0.986,0.49,1.912,1.243,2.517C1.355,8.624-1,11.313-1,14.5C-1,18.09,1.91,21,5.5,21 s6.5-2.91,6.5-6.5c0-3.187-2.355-5.876-5.243-6.483C7.51,7.412,8,6.486,8,5.5C8,3.566,6.434,2,4.5,2z"/></svg>') 5 5, auto;
}
</style>
