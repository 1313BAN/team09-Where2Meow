<template>
  <div class="panel-header">
    <div class="tabs">
      <button 
        @click="$emit('update:activeTab', 'info')"
        :class="[
          'tab',
          activeTab === 'info' ? 'active' : ''
        ]"
      >
        정보
      </button>
      <button 
        @click="$emit('update:activeTab', 'schedule')"
        :class="[
          'tab',
          activeTab === 'schedule' ? 'active' : ''
        ]"
      >
        일정
      </button>
      <button 
        @click="$emit('update:activeTab', 'search')"
        :class="[
          'tab',
          activeTab === 'search' ? 'active' : ''
        ]"
      >
        검색
      </button>
    </div>
  </div>
</template>

<script setup>
defineProps({
  activeTab: String
})

defineEmits(['update:activeTab'])
</script>

<style scoped>
.panel-header {
  padding: 15px;
  border-bottom: 1px solid #eee;
  background-color: #f7f7f7;
  flex-shrink: 0;
}

.tabs {
  display: flex;
  border-radius: 5px;
  overflow: hidden;
  background-color: #e0e0e0;
  width: 100%;
}

.tab {
  flex: 1;
  padding: 8px 15px;
  cursor: pointer;
  font-weight: bold;
  color: #555;
  transition: background-color 0.2s ease;
  border: none;
  background: none;
  text-align: center;
}

.tab.active {
  background-color: #00EDB3;
  color: #fff;
}

.tab:hover:not(.active) {
  background-color: #d0d0d0;
}
</style>
