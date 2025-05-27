<template>
  <div class="min-h-screen bg-white">
    <div class="container mx-auto px-4 sm:px-6 lg:px-8 max-w-7xl">
      <AttractionSearchSection @search="handleSearch" />
      <AttractionListSection :search-params="searchParams" />
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import AttractionSearchSection from '@/components/views/attraction/AttractionSearchSection/AttractionSearchSection.vue'
import AttractionListSection from '@/components/views/attraction/AttractionListSection/AttractionListSection.vue'

// 검색 파라미터 상태
const searchParams = ref({})

// 검색 핸들러
const handleSearch = (params) => {
  console.log('검색 파라미터:', params)
  searchParams.value = { ...params }
}
</script>
