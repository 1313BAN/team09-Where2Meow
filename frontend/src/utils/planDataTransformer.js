/**
 * 프론트엔드 일정 데이터를 백엔드 API 형식으로 변환
 * @param {Object} planData - 프론트엔드 일정 데이터
 * @returns {Object} 백엔드 API 형식의 데이터
 */
export const transformPlanDataForAPI = (planData) => {
  const { tripTitle, content, startDate, endDate, isPublic, scheduleData } = planData
  
  // 모든 날짜의 일정을 하나의 배열로 합치기
  const attractions = []
  
  Object.keys(scheduleData).forEach(day => {
    const dayNumber = parseInt(day)
    const daySchedules = scheduleData[day] || []
    
    // 해당 날짜 계산 (startDate + (day - 1))
    const scheduleDate = new Date(startDate)
    scheduleDate.setDate(scheduleDate.getDate() + (dayNumber - 1))
    const dateString = scheduleDate.toISOString().split('T')[0] // YYYY-MM-DD 형식
    
    daySchedules.forEach((schedule, index) => {
      attractions.push({
        attractionId: schedule.attractionId || 1, // 실제 attraction ID가 있다면 사용
        content: schedule.content || schedule.name || '',
        date: dateString,
        attractionOrder: index + 1
      })
    })
  })
  
  return {
    title: tripTitle,
    content: content || '',
    startDate: startDate,
    endDate: endDate,
    isPublic: isPublic || false,
    attractions: attractions
  }
}

/**
 * 백엔드 API 응답 데이터를 프론트엔드 형식으로 변환
 * @param {Object} apiResponse - 백엔드 API 응답 데이터
 * @returns {Object} 프론트엔드 형식의 데이터
 */
export const transformAPIDataForFrontend = (apiResponse) => {
  const { title, content, startDate, endDate, isPublic, attractions } = apiResponse
  
  // 날짜별로 일정 그룹화
  const scheduleData = {}
  const start = new Date(startDate)
  
  attractions.forEach(attraction => {
    const attractionDate = new Date(attraction.date)
    const dayDiff = Math.ceil((attractionDate - start) / (1000 * 60 * 60 * 24)) + 1
    
    if (!scheduleData[dayDiff]) {
      scheduleData[dayDiff] = []
    }
    
    scheduleData[dayDiff].push({
      attractionId: attraction.attractionId,
      name: attraction.content,
      location: '위치 정보', // 실제 위치 정보가 있다면 사용
      duration: '시간 미정',
      content: attraction.content,
      attractionOrder: attraction.attractionOrder
    })
  })
  
  // 각 날짜별로 attractionOrder 순으로 정렬
  Object.keys(scheduleData).forEach(day => {
    scheduleData[day].sort((a, b) => a.attractionOrder - b.attractionOrder)
  })
  
  return {
    tripTitle: title,
    content: content,
    startDate: startDate,
    endDate: endDate,
    isPublic: isPublic,
    scheduleData: scheduleData
  }
}
