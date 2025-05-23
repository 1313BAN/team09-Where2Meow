<script setup>
import { RouterView } from 'vue-router'
</script>

<script>
import LayoutNavi from '@/components/layout/LayoutNavi.vue'
import LayoutFooter from '@/components/layout/LayoutFooter.vue'

export default {
  name: 'App',
  components: {
    LayoutNavi,
    LayoutFooter,
  },
}
</script>

<template>
  <div id="app">
    <LayoutNavi />
    <main class="container mx-auto p-4 mt-4">
      <RouterView />
    </main>
    <LayoutFooter />
  </div>
</template>
