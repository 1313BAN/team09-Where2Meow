<template>
  <div class="chat-header">
    <div class="ai-status">
      <div class="img-wrapper">
        <img src="/images/cat.png" alt="AI 가냥이" class="ai-profile" />
      </div>
      <div class="ai-name">AI 가냥이</div>
    </div>
  </div>
</template>

<style scoped>
.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px;
  border-bottom: 1px solid #eee;
  background-color: #6fbbff;
  flex-shrink: 0;
  color: white;
}

.ai-status {
  display: flex;
  align-items: center;
  gap: 10px;
}

.img-wrapper{
  width: 50px;
  height: 50px;
  border-radius: 50%;
  overflow: hidden;
}

.ai-profile {
  transform: scale(1.7) translateY(8px) translateX(4px);
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

.more-options .icons {
  font-size: 24px;
}
</style>
