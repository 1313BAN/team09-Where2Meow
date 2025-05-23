<script setup>
import { RouterView } from 'vue-router'
import LayoutNavi from '@/components/layouts/LayoutNavi.vue'
import LayoutFooter from '@/components/layouts/LayoutFooter.vue'
</script>

<template>
  <div class="min-h-screen flex flex-col">
    <LayoutNavi />
    <main class="flex-1">
      <RouterView />
    </main>
    <LayoutFooter />
  </div>
</template>
