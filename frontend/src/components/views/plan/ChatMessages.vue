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
      <div class="message-bubble">{{ message.text }}</div>
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
</style>
