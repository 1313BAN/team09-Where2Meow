<template>
  <div class="chat-input">
    <input 
      :value="newMessage"
      :disabled="isAiProcessing"
      @input="$emit('update:newMessage', $event.target.value)"
      @keyup.enter="handleSend"
      type="text"
      :placeholder="isAiProcessing ? 'AI가 처리 중입니다...' : '메시지를 입력하세요...'"
    />
    <button 
      @click="handleSend"
      :disabled="isAiProcessing"
      :class="['send-button', { 'processing': isAiProcessing }]"
    >
      <i v-if="isAiProcessing" class="pi pi-spin pi-spinner processing-icon"></i>
      <i v-else class="pi pi-send"></i>
    </button>
  </div>
</template>

<script setup>
const props = defineProps({
  newMessage: String,
  isAiProcessing: Boolean
})

const emit = defineEmits(['update:newMessage', 'sendMessage'])

const handleSend = () => {
  if (!props.isAiProcessing) {
    emit('sendMessage')
  }
}
</script>

<style scoped>
.chat-input {
  display: flex;
  padding: 15px;
  border-top: 1px solid #eee;
  background-color: #fff;
  flex-shrink: 0;
}

.chat-input input[type="text"] {
  flex-grow: 1;
  padding: 10px 15px;
  border: 1px solid #ddd;
  border-radius: 20px;
  outline: none;
  font-size: 1em;
  transition: opacity 0.2s ease;
}

.chat-input input[type="text"]:disabled {
  background-color: #f5f5f5;
  color: #999;
  cursor: not-allowed;
}

.chat-input .send-button {
  background-color: #6FBBFF;
  color: #fff;
  border: none;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  margin-left: 10px;
  transition: background-color 0.2s ease, opacity 0.2s ease;
}

.chat-input .send-button:hover:not(:disabled) {
  background-color: #5aa8e6;
}

.chat-input .send-button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.chat-input .send-button.processing {
  background-color: #ff9800;
}

.chat-input .send-button i {
  font-size: 20px;
}
</style>
