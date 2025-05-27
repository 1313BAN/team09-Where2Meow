<template>
  <div 
    class="bg-white rounded-2xl overflow-hidden shadow-lg transition-all duration-300 cursor-pointer border-2 border-transparent hover:-translate-y-1 hover:border-[#00EDB3]"
    style="box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);"
    @click="$emit('click', plan)"
  >
    <div 
      class="relative w-full flex items-center justify-center overflow-hidden"
      style="height: 180px; background: linear-gradient(135deg, #6FBBFF 0%, #00EDB3 100%);"
    >
      <img v-if="plan.image" :src="plan.image" :alt="plan.title" class="w-full h-full object-cover" />
      <div v-else class="w-full h-full bg-gray-200 flex items-center justify-center text-gray-400 text-base font-medium">
        <span>400 × 180</span>
      </div>
      <div class="absolute top-3 right-3 bg-black/60 px-3 py-1.5 rounded-full backdrop-blur-sm">
        <span class="text-white text-xs font-medium">{{ getDuration() }}</span>
      </div>
    </div>
    
    <div class="p-5">
      <h3 class="text-base font-semibold text-gray-900 mb-3 leading-snug line-clamp-2">
        {{ plan.title }}
      </h3>
      
      <div class="flex flex-col gap-1 mb-3">
        <span class="text-sm text-gray-600 font-medium">{{ getLocationText() }}</span>
        <span class="text-xs text-gray-400">{{ getDateRange() }}</span>
      </div>
      
      <div class="flex gap-3 mb-3 text-xs text-gray-500">
        <span class="flex items-center gap-1">조회 {{ plan.viewCount }}</span>
        <span class="flex items-center gap-1">좋아요 {{ plan.likeCount }}</span>
      </div>
      
      <div class="flex justify-between items-center">
        <span 
          class="px-3 py-1 rounded-full text-xs font-medium"
          :class="getStatusBadgeClass()"
        >
          {{ getStatusText() }}
        </span>
        
        <div class="flex gap-2 items-center text-sm">
          <button 
            @click.stop="toggleBookmark" 
            class="p-1 rounded transition-colors hover:bg-gray-100 disabled:opacity-50"
            :disabled="bookmarkLoading"
          >
            <span v-if="plan.bookmarked" class="text-[#FFD900]">★</span>
            <span v-else class="text-gray-300">☆</span>
          </button>
          
          <button 
            @click.stop="toggleLike" 
            class="p-1 rounded transition-colors hover:bg-gray-100 disabled:opacity-50"
            :disabled="likeLoading"
          >
            <span class="transition-colors" :class="plan.liked ? 'text-[#FF80CF]' : 'text-gray-300'">♥</span>
          </button>
          
          <span v-if="plan.public" class="text-[#00EDB3]">🌐</span>
          <span v-else class="text-gray-400">🔒</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import planAPI from '@/api/plan'

const planService = planAPI.planService

export default {
  name: 'TravelPlanCard',
  props: {
    plan: {
      type: Object,
      required: true
    }
  },
  emits: ['click', 'update'],
  data() {
    return {
      bookmarkLoading: false,
      likeLoading: false
    }
  },
  methods: {
    getDuration() {
      const start = new Date(this.plan.startDate);
      const end = new Date(this.plan.endDate);
      const diffTime = Math.abs(end - start);
      const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24)) + 1;
      return `${diffDays - 1}박 ${diffDays}일`;
    },
    getLocationText() {
      if (this.plan.title.includes('제주')) return '제주도';
      if (this.plan.title.includes('서울')) return '서울';
      if (this.plan.title.includes('부산')) return '부산';
      return '여행지';
    },
    getDateRange() {
      const start = new Date(this.plan.startDate);
      const end = new Date(this.plan.endDate);
      const formatDate = (date) => {
        return `${date.getFullYear()}.${String(date.getMonth() + 1).padStart(2, '0')}.${String(date.getDate()).padStart(2, '0')}`;
      };
      return `${formatDate(start)} - ${formatDate(end)}`;
    },
    getStatusClass() {
      const today = new Date();
      const startDate = new Date(this.plan.startDate);
      const endDate = new Date(this.plan.endDate);
      
      if (today < startDate) return 'upcoming';
      if (today >= startDate && today <= endDate) return 'ongoing';
      return 'completed';
    },
    getStatusText() {
      const statusClass = this.getStatusClass();
      const statusMap = {
        upcoming: '예정됨',
        ongoing: '진행중',
        completed: '완료됨'
      };
      return statusMap[statusClass];
    },
    getStatusBadgeClass() {
      const statusClass = this.getStatusClass();
      const baseClass = 'bg-gray-100 text-gray-600';
      
      switch(statusClass) {
        case 'upcoming':
          return 'text-[#6FBBFF] bg-blue-50';
        case 'ongoing':
          return 'text-[#FFD900] bg-yellow-50';
        case 'completed':
          return 'text-[#00EDB3] bg-green-50';
        default:
          return baseClass;
      }
    },
    async toggleBookmark() {
      if (this.bookmarkLoading) return;
      
      this.bookmarkLoading = true;
      try {
        if (this.plan.bookmarked) {
          await planService.removeBookmark(this.plan.planId);
        } else {
          await planService.addBookmark(this.plan.planId);
        }
        
        this.$emit('update', {
          ...this.plan,
          bookmarked: !this.plan.bookmarked
        });
      } catch (error) {
        console.error('북마크 처리 중 오류가 발생했습니다:', error);
      } finally {
        this.bookmarkLoading = false;
      }
    },
    async toggleLike() {
      if (this.likeLoading) return;
      
      this.likeLoading = true;
      try {
        if (this.plan.liked) {
          await planService.removeLike(this.plan.planId);
        } else {
          await planService.addLike(this.plan.planId);
        }
        
        this.$emit('update', {
          ...this.plan,
          liked: !this.plan.liked,
          likeCount: this.plan.liked ? this.plan.likeCount - 1 : this.plan.likeCount + 1
        });
      } catch (error) {
        console.error('좋아요 처리 중 오류가 발생했습니다:', error);
      } finally {
        this.likeLoading = false;
      }
    }
  }
}
</script>

<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.hover\:shadow-lg:hover {
  box-shadow: 0 8px 24px rgba(0, 237, 179, 0.15);
}
</style>
