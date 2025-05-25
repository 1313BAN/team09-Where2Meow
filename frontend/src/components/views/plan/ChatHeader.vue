<template>
  <div class="chat-header">
    <div class="ai-status">
      <div class="ai-name">AI 가넷이</div>
      <div class="status-indicator online">온라인</div>
    </div>
    <button class="more-options">
      <span class="material-icons">more_vert</span>
    </button>
  </div>
</template>

<style scoped>
.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #eee;
  background-color: #6FBBFF;
  flex-shrink: 0;
  color: white;
}

.ai-status {
  display: flex;
  align-items: center;
  gap: 10px;
}

.ai-name {
  font-weight: bold;
  color: #fff;
}

.status-indicator {
  font-size: 0.8em;
  padding: 3px 8px;
  border-radius: 12px;
  color: #fff;
  background-color: #ccc;
}

.status-indicator.online {
  background-color: #28a745;
}

.more-options {
  background: none;
  border: none;
  cursor: pointer;
  color: #fff;
}

.more-options .material-icons {
  font-size: 24px;
}
</style>
