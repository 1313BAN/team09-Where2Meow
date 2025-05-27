<script setup>
import { RouterView } from 'vue-router'
import LayoutNavi from '@/components/layouts/LayoutNavi.vue'
</script>

<template>
  <div class="min-h-screen flex flex-col">
    <LayoutNavi />
    <main class="flex-1">
      <RouterView />
    </main>
  </div>
</template>
