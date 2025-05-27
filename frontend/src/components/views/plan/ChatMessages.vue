<template>
  <div class="chat-messages">
    <div 
      v-for="message in chatMessages" 
      :key="message.id"
      :class="[
        'message',
        message.sender === 'user' ? 'sent' : 'received'
      ]"
    >
      <div :class="[
        'message-bubble', 
        { 'loading': message.isLoading }
      ]">
        <template v-if="message.isLoading">
          <div class="loading-dots">
            <span></span>
            <span></span>
            <span></span>
          </div>
          {{ message.text }}
        </template>
        <template v-else>
          {{ message.text }}
        </template>
      </div>
      <div class="message-time">{{ message.time }}</div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  chatMessages: Array
})
</script>

<style scoped>
.chat-messages {
  height: 100%;
  padding: 15px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.chat-messages::-webkit-scrollbar {
  width: 6px;
}

.chat-messages::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.chat-messages::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.chat-messages::-webkit-scrollbar-thumb:hover {
  background: #a1a1a1;
}

.message {
  display: flex;
  flex-direction: column;
  max-width: 75%;
}

.message.received {
  align-self: flex-start;
}

.message.sent {
  align-self: flex-end;
}

.message-bubble {
  padding: 10px 15px;
  border-radius: 18px;
  word-wrap: break-word;
  font-size: 0.95em;
  white-space: pre-wrap;
}

.message.received .message-bubble {
  background-color: #e0e0e0;
  color: #333;
  border-bottom-left-radius: 2px;
}

.message.sent .message-bubble {
  background-color: #FFD900;
  color: #333;
  border-bottom-right-radius: 2px;
}

.message-time {
  font-size: 0.75em;
  color: #999;
  margin-top: 5px;
  align-self: flex-end;
  margin-right: 10px;
}

.message.received .message-time {
  align-self: flex-start;
  margin-left: 10px;
}

.message-bubble.loading {
  background-color: #f0f0f0 !important;
  animation: pulse 1.5s ease-in-out infinite;
}

.loading-dots {
  display: inline-flex;
  gap: 3px;
  margin-right: 8px;
  align-items: center;
}

.loading-dots span {
  width: 4px;
  height: 4px;
  background-color: #666;
  border-radius: 50%;
  animation: bounce 1.4s ease-in-out infinite both;
}

.loading-dots span:nth-child(1) {
  animation-delay: -0.32s;
}

.loading-dots span:nth-child(2) {
  animation-delay: -0.16s;
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.7;
  }
}

@keyframes bounce {
  0%, 80%, 100% {
    transform: scale(0);
  }
  40% {
    transform: scale(1);
  }
}
</style>
