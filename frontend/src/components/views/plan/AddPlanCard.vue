<template>
  <div 
    class="rounded-2xl flex items-center justify-center cursor-pointer transition-all duration-300 border-2 border-dashed border-white/50 hover:-translate-y-1 hover:shadow-xl"
    style="background: linear-gradient(135deg, #FF80CF 0%, #FFD900 100%); min-height: 280px;"
    @click="$emit('click')"
  >
    <div class="flex flex-col items-center gap-3 text-white">
      <div class="text-5xl font-light leading-none">+</div>
      <span class="text-base font-semibold">일정 추가</span>
    </div>
  </div>
</template>

<script>
export default {
  name: 'AddPlanCard',
  emits: ['click']
}
</script>
