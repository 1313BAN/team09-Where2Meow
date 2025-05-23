<template>
  <div class="typewriter-container" :class="containerClass">
    <p class="typewriter-text" :class="textClass">
      <i v-if="showIcon" :class="iconClass"></i>
      <span>{{ currentText }}</span>
    </p>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, watch } from 'vue'

// Props 정의
const props = defineProps({
  texts: {
    type: Array,
    required: true,
  },
  typingSpeed: {
    type: Number,
    default: 100, // 타이핑 속도 (ms)
  },
  deletingSpeed: {
    type: Number,
    default: 50, // 삭제 속도 (ms)
  },
  pauseDuration: {
    type: Number,
    default: 2000, // 텍스트 완성 후 대기 시간 (ms)
  },
  showIcon: {
    type: Boolean,
    default: true,
  },
  iconClass: {
    type: String,
    default: 'pi pi-info-circle mr-2 text-[var(--accent-color)]',
  },
  containerClass: {
    type: String,
    default: '',
  },
  textClass: {
    type: String,
    default: 'text-sm text-white/80',
  },
  autoStart: {
    type: Boolean,
    default: true,
  },
  loop: {
    type: Boolean,
    default: true,
  },
})

// 반응형 데이터
const currentText = ref('')
const currentTextIndex = ref(0)
const charIndex = ref(0)
const isTyping = ref(true)
const isActive = ref(false)

let typingTimer = null

// 메서드
const typeWriter = () => {
  if (!isActive.value) return

  const currentFullText = props.texts[currentTextIndex.value] || ''

  if (isTyping.value) {
    // 타이핑 중
    if (charIndex.value < currentFullText.length) {
      currentText.value = currentFullText.substring(0, charIndex.value + 1)
      charIndex.value++
      scheduleNext(props.typingSpeed)
    } else {
      // 타이핑 완료, 대기 후 삭제 시작
      setTimeout(() => {
        if (isActive.value) {
          isTyping.value = false
          scheduleNext(props.deletingSpeed)
        }
      }, props.pauseDuration)
    }
  } else {
    // 삭제 중
    if (charIndex.value > 0) {
      currentText.value = currentFullText.substring(0, charIndex.value - 1)
      charIndex.value--
      scheduleNext(props.deletingSpeed)
    } else {
      // 삭제 완료, 다음 텍스트로 이동
      isTyping.value = true
      if (props.loop) {
        currentTextIndex.value = (currentTextIndex.value + 1) % props.texts.length
      } else if (currentTextIndex.value < props.texts.length - 1) {
        currentTextIndex.value++
      } else {
        // 루프가 아니고 마지막 텍스트까지 완료
        return
      }
      scheduleNext(props.typingSpeed)
    }
  }
}

const scheduleNext = (delay) => {
  clearTimeout(typingTimer)
  typingTimer = setTimeout(typeWriter, delay)
}

const start = () => {
  if (props.texts.length === 0) return

  isActive.value = true
  currentTextIndex.value = 0
  charIndex.value = 0
  isTyping.value = true
  currentText.value = ''
  typeWriter()
}

const stop = () => {
  isActive.value = false
  clearTimeout(typingTimer)
}

const reset = () => {
  stop()
  currentText.value = ''
  currentTextIndex.value = 0
  charIndex.value = 0
  isTyping.value = true
}

const restart = () => {
  reset()
  start()
}

// props.texts가 변경되면 재시작
watch(
  () => props.texts,
  () => {
    if (isActive.value) {
      restart()
    }
  },
  { deep: true },
)

// 생명주기
onMounted(() => {
  if (props.autoStart && props.texts.length > 0) {
    start()
  }
})

onBeforeUnmount(() => {
  stop()
})

// 외부에서 제어할 수 있도록 메서드 노출
defineExpose({
  start,
  stop,
  reset,
  restart,
  isActive: () => isActive.value,
})
</script>

<style scoped>
.typewriter-container {
  border-left: 3px solid var(--primary-color);
  padding-left: 10px;
  opacity: 0.9;
}

.typewriter-text {
  display: inline-block;
  overflow: hidden;
  white-space: nowrap;
}

/* 커서 효과 (선택사항) */
.typewriter-text::after {
  content: '|';
  animation: blink 1s infinite;
  margin-left: 2px;
}

@keyframes blink {
  0%,
  50% {
    opacity: 1;
  }
  51%,
  100% {
    opacity: 0;
  }
}
</style>
