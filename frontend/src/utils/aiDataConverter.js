import { attractionApi } from '@/api/attraction'

// 일정 데이터를 AI API 형식으로 변환
export const convertScheduleToAiFormat = (scheduleData, startDate, endDate) => {
  const attractions = []
  
  Object.keys(scheduleData).forEach(day => {
    const dayNumber = parseInt(day)
    const scheduleItems = scheduleData[day] || []
    
    // 해당 날짜 계산
    const currentDate = new Date(startDate)
    currentDate.setDate(currentDate.getDate() + dayNumber - 1)
    const dateString = currentDate.toISOString().split('T')[0]
    
    scheduleItems.forEach((item, index) => {
      const attraction = {
        uniqueKey: `${dateString}_${index + 1}_${item.attractionId}`,
        attractionId: item.attractionId,
        attractionName: item.attractionName || item.name,
        addr: item.location || `${item.stateName || ''} ${item.cityName || ''}`.trim(),
        lat: parseFloat(item.latitude) || 0,
        lng: parseFloat(item.longitude) || 0,
        categoryId: item.categoryId || 12, // 기본값으로 관광지
        categoryName: item.categoryName || item.attraction_category_name || '관광지',
        date: dateString,
        attractionOrder: index + 1,
        content: item.content || ''
      }
      
      attractions.push(attraction)
    })
  })
  
  return attractions
}

// AI 응답 데이터를 일정 형식으로 변환 (이미지 URL 포함)
export const convertAiResponseToSchedule = async (attractions, startDate) => {
  const scheduleData = {}
  
  if (!attractions || !Array.isArray(attractions)) {
    console.warn('attractions가 올바른 배열이 아닙니다:', attractions)
    return scheduleData
  }
  
  // 이미지 URL들을 병렬로 가져오기 (백엔드에서 이미 제공한 경우 생략)
  const attractionIds = attractions.map(attraction => attraction.attractionId).filter(id => id)
  let imageUrls = {}
  
  // 백엔드에서 이미지 URL을 제공하지 않은 경우에만 프론트엔드에서 가져오기
  const needImageFetch = attractionIds.some(id => {
    const attraction = attractions.find(attr => attr.attractionId === id)
    return !attraction.imageUrl
  })
  
  if (needImageFetch && attractionIds.length > 0) {
    try {
      console.log('백엔드에서 이미지 URL을 제공하지 않아 프론트엔드에서 가져오는 중...')
      // 모든 관광지 이미지 URL을 한 번에 가져오기
      const imagePromises = attractionIds.map(async (id) => {
        try {
          const imageUrl = await attractionApi.getAttractionImageUrl(id)
          return { id, imageUrl }
        } catch (error) {
          console.warn(`관광지 ${id} 이미지 URL 가져오기 실패:`, error)
          return { id, imageUrl: null }
        }
      })
      
      const imageResults = await Promise.all(imagePromises)
      imageUrls = imageResults.reduce((acc, { id, imageUrl }) => {
        acc[id] = imageUrl
        return acc
      }, {})
      
      console.log('프론트엔드에서 가져온 이미지 URL들:', imageUrls)
    } catch (error) {
      console.error('이미지 URL 가져오기 실패:', error)
    }
  } else {
    console.log('백엔드에서 이미지 URL을 이미 제공했습니다.')
  }
  
  attractions.forEach((attraction, index) => {
    try {
      // 날짜를 기반으로 몇일차인지 계산
      let attractionDate
      if (attraction.date) {
        attractionDate = new Date(attraction.date)
      } else {
        // 날짜 정보가 없으면 기본적으로 첫 날로 설정
        attractionDate = new Date(startDate)
      }
      
      const startDateObj = new Date(startDate)
      const dayDiff = Math.floor((attractionDate - startDateObj) / (1000 * 60 * 60 * 24)) + 1
      
      // 유효한 날짜 범위 확인 (음수 방지)
      const validDay = Math.max(1, dayDiff)
      
      if (!scheduleData[validDay]) {
        scheduleData[validDay] = []
      }
      
      // 이미지 URL 결정: 백엔드 제공 > 프론트엔드 가져오기 > null
      const finalImageUrl = attraction.imageUrl || imageUrls[attraction.attractionId] || null
      
      const scheduleItem = {
        attractionId: attraction.attractionId || `temp_${index}`,
        attractionName: attraction.attractionName || '이름 없음',
        name: attraction.attractionName || '이름 없음',
        stateName: attraction.stateName || '',
        cityName: attraction.cityName || '',
        location: attraction.addr || '위치 정보 없음',
        duration: '시간 미정',
        content: attraction.content || '',
        date: attraction.date || startDate,
        // 이미지 URL 추가
        first_image1: finalImageUrl,
        first_image2: null,
        image: finalImageUrl,
        categoryName: attraction.categoryName || '기본',
        attraction_category_name: attraction.categoryName || '기본',
        latitude: parseFloat(attraction.lat) || 0,
        longitude: parseFloat(attraction.lng) || 0,
        categoryId: attraction.categoryId || 12
      }
      
      scheduleData[validDay].push(scheduleItem)
    } catch (error) {
      console.error('attraction 데이터 변환 오류:', error, attraction)
    }
  })
  
  // 각 일차별로 attractionOrder 순서대로 정렬
  Object.keys(scheduleData).forEach(day => {
    scheduleData[day].sort((a, b) => {
      const orderA = attractions.find(attr => 
        attr.attractionId === a.attractionId && attr.date === a.date
      )?.attractionOrder || 0
      
      const orderB = attractions.find(attr => 
        attr.attractionId === b.attractionId && attr.date === b.date
      )?.attractionOrder || 0
      
      return orderA - orderB
    })
  })
  
  return scheduleData
}

// 명령어에서 생성/업데이트 구분
export const detectCommandType = (message) => {
  const createKeywords = ['생성', '만들', '짜', '계획', '스케줄', '일정 만들', '새로']
  const updateKeywords = ['수정', '변경', '바꿔', '교체', '업데이트', '다시', '추가']
  
  const lowerMessage = message.toLowerCase()
  
  // 업데이트 키워드가 있으면 업데이트
  if (updateKeywords.some(keyword => lowerMessage.includes(keyword))) {
    return 'update'
  }
  
  // 생성 키워드가 있으면 생성
  if (createKeywords.some(keyword => lowerMessage.includes(keyword))) {
    return 'create'
  }
  
  // 기본값은 생성 (안전하게)
  return 'create'
}
