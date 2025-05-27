import { localAxios } from '@/utils/http-commons'

const http = localAxios()

const aiApi = {
  // AI 여행 계획 생성
  createPlan: async (query) => {
    const response = await http.post('/api/ai/create', { query })
    return response
  },

  // AI 여행 계획 업데이트
  updatePlan: async (query, currentPlan) => {
    const response = await http.post('/api/ai/update', {
      query,
      plan: {
        attractions: currentPlan
      }
    })
    return response
  }
}

export { aiApi }
