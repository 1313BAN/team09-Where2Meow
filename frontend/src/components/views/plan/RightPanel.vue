<template>
  <div class="right-panel">
    <!-- 채팅 헤더 -->
    <div class="chat-header-container">
      <ChatHeader />
    </div>

    <!-- 채팅 메시지 -->
    <div class="chat-messages-container">
      <ChatMessages :chatMessages="chatMessages" />
    </div>

    <!-- 채팅 입력 -->
    <div class="chat-input-container">
      <ChatInput
        :newMessage="newMessage"
        @update:newMessage="$emit('update:newMessage', $event)"
        @sendMessage="$emit('sendMessage')"
      />
    </div>
  </div>
</template>

<script setup>
import ChatHeader from './ChatHeader.vue'
import ChatMessages from './ChatMessages.vue'
import ChatInput from './ChatInput.vue'

defineProps({
  chatMessages: Array,
  newMessage: String
})

defineEmits(['update:newMessage', 'sendMessage'])
</script>

<style scoped>
.right-panel {
  width: 350px;
  background-color: #f9f9f9;
  box-shadow: -2px 0 5px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  height: 100%; /* 100vh에서 100%로 변경 */
  overflow: hidden;
}

.chat-header-container {
  flex-shrink: 0;
}

.chat-messages-container {
  flex: 1;
  overflow: hidden;
}

.chat-input-container {
  flex-shrink: 0;
}
</style>
