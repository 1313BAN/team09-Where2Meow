<template>
  <div class="chat-input">
    <input 
      :value="newMessage"
      @input="$emit('update:newMessage', $event.target.value)"
      @keyup.enter="$emit('sendMessage')"
      type="text"
      placeholder="메시지를 입력하세요..."
    />
    <button 
      @click="$emit('sendMessage')"
      class="send-button"
    >
      <span class="material-icons">send</span>
    </button>
  </div>
</template>

<script setup>
defineProps({
  newMessage: String
})

defineEmits(['update:newMessage', 'sendMessage'])
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
  transition: background-color 0.2s ease;
}

.chat-input .send-button:hover {
  background-color: #5aa8e6;
}

.chat-input .send-button .material-icons {
  font-size: 20px;
}
</style>
