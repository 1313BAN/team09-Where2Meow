<template>
  <Button
    :label="label"
    :icon="icon"
    :severity="severity"
    :size="size"
    :outlined="outlined"
    :text="text"
    :rounded="rounded"
    :loading="loading"
    :disabled="disabled"
    :class="customClass"
    @click="handleClick"
  />
</template>

<script setup>
import Button from 'primevue/button'

// Props 정의
const props = defineProps({
  label: {
    type: String,
    default: '',
  },
  icon: {
    type: String,
    default: '',
  },
  severity: {
    type: String,
    default: 'primary',
    validator: (value) =>
      ['primary', 'secondary', 'success', 'info', 'warn', 'help', 'danger', 'contrast'].includes(
        value,
      ),
  },
  size: {
    type: String,
    default: 'normal',
    validator: (value) => ['small', 'normal', 'large'].includes(value),
  },
  outlined: {
    type: Boolean,
    default: false,
  },
  text: {
    type: Boolean,
    default: false,
  },
  rounded: {
    type: Boolean,
    default: false,
  },
  loading: {
    type: Boolean,
    default: false,
  },
  disabled: {
    type: Boolean,
    default: false,
  },
  customClass: {
    type: String,
    default: '',
  },
})

// Events 정의
const emit = defineEmits(['click'])

// 클릭 핸들러
const handleClick = (event) => {
  if (!props.disabled && !props.loading) {
    emit('click', event)
  }
}
</script>
