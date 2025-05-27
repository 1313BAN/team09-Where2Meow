<template>
  <div class="min-h-screen bg-white">
    <div class="container mx-auto px-4 sm:px-6 lg:px-8 max-w-7xl">
      <AttractionSearchSection />
      <AttractionListSection />
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import AttractionSearchSection from '@/components/views/attraction/AttractionSearchSection/AttractionSearchSection.vue'
import AttractionListSection from '@/components/views/attraction/AttractionListSection/AttractionListSection.vue'

const router = useRouter()
</script>
