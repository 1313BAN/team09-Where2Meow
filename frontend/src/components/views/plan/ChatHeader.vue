<template>
  <div class="chat-header">
    <div class="ai-status">
      <div class="ai-name">AI 가냥이</div>
    </div>
    <button class="more-options" aria-label="더 많은 옵션">
      <span class="icons"
        ><svg
          xmlns="http://www.w3.org/2000/svg"
          height="24"
          viewBox="0 0 24 24"
          width="24"
          fill="currentColor"
        >
          <circle cx="12" cy="6" r="2" />
          <circle cx="12" cy="12" r="2" />
          <circle cx="12" cy="18" r="2" />
        </svg>
      </span>
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
  background-color: #6fbbff;
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

.more-options .icons {
  font-size: 24px;
}
</style>
