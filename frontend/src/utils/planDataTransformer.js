/**
 * 프론트엔드 일정 데이터를 백엔드 API 형식으로 변환
 * @param {Object} planData - 프론트엔드 일정 데이터
 * @returns {Object} 백엔드 API 형식의 데이터
 */
export const transformPlanDataForAPI = (planData) => {
  // 입력 검증
  if (!planData) {
    throw new Error('planData는 필수입니다.')
  }

  if (!planData.startDate) {
    throw new Error('startDate는 필수입니다.')
  }

  if (!planData.scheduleData || typeof planData.scheduleData !== 'object') {
    throw new Error('scheduleData는 필수이며 객체여야 합니다.')
  }

  const { tripTitle, content, startDate, endDate, isPublic, scheduleData } = planData

  // 모든 날짜의 일정을 하나의 배열로 합치기
  const attractions = []

  try {
    Object.keys(scheduleData).forEach((day) => {
      const dayNumber = parseInt(day)
      if (isNaN(dayNumber)) {
        console.warn(`유효하지 않은 day 값: ${day}`)
        return
      }
      const daySchedules = scheduleData[day] || []

      // 해당 날짜 계산 (startDate + (day - 1))
      const scheduleDate = new Date(startDate + 'T00:00:00.000Z')
      if (isNaN(scheduleDate.getTime())) {
        throw new Error(`유효하지 않은 startDate: ${startDate}`)
      }
      scheduleDate.setDate(scheduleDate.getDate() + (dayNumber - 1))
      const dateString = scheduleDate.toISOString().split('T')[0] // YYYY-MM-DD 형식

      daySchedules.forEach((schedule, index) => {
        if (!schedule.attractionId) {
          console.warn(
            `Day ${dayNumber}, index ${index}: attractionId가 없는 스케줄 항목이 발견되었습니다.`,
            schedule,
          )
          throw new Error() // 또는 throw new Error()
        }
        attractions.push({
          attractionId: schedule.attractionId,
          content: schedule.content || schedule.name || '',
          date: dateString,
          attractionOrder: index + 1,
        })
      })
    })
  } catch (error) {
    console.error('planData 변환 중 오류 발생:', error)
    throw new Error(`planData 변환 실패: ${error.message}`)
  }

  return {
    title: tripTitle,
    content: content || '',
    startDate: startDate,
    endDate: endDate,
    isPublic: isPublic || false,
    attractions: attractions,
  }
}

/**
 * 백엔드 API 응답 데이터를 프론트엔드 형식으로 변환
 * @param {Object} apiResponse - 백엔드 API 응답 데이터
 * @returns {Object} 프론트엔드 형식의 데이터
 */
export const transformAPIDataForFrontend = (apiResponse) => {
  // 입력 검증
  if (!apiResponse) {
    throw new Error('apiResponse는 필수입니다.')
  }

  if (!apiResponse.startDate) {
    throw new Error('startDate는 필수입니다.')
  }

  if (!Array.isArray(apiResponse.attractions)) {
    throw new Error('attractions는 배열이어야 합니다.')
  }

  const { title, content, startDate, endDate, isPublic, attractions } = apiResponse

  // 날짜별로 일정 그룹화
  const scheduleData = {}
  const start = new Date(startDate + 'T00:00:00.000Z') // UTC 기준

  if (isNaN(start.getTime())) {
    throw new Error(`유효하지 않은 startDate: ${startDate}`)
  }

  try {
    attractions.forEach((attraction) => {
      if (!attraction.attractionId || !attraction.date) {
        console.warn('필수 필드가 없는 attraction:', attraction)
        return
      }
      const attractionDate = new Date(attraction.date + 'T00:00:00.000Z')
      if (isNaN(attractionDate.getTime())) {
        console.warn(`유효하지 않은 attraction date: ${attraction.date}`)
        return
      }
      const dayDiff = Math.ceil((attractionDate - start) / (1000 * 60 * 60 * 24)) + 1

      if (!scheduleData[dayDiff]) {
        scheduleData[dayDiff] = []
      }

      scheduleData[dayDiff].push({
        attractionId: attraction.attractionId,
        name: attraction.content,
        location: attraction.location || '위치 정보', // 실제 위치 정보가 있다면 사용
        duration: attraction.duration || '시간 미정',
        content: attraction.content,
        attractionOrder: attraction.attractionOrder,
      })
    })
  } catch (error) {
    console.error('API 응답 변환 중 오류 발생:', error)
    throw new Error(`API 응답 변환 실패: ${error.message}`)
  }

  // 각 날짜별로 attractionOrder 순으로 정렬
  Object.keys(scheduleData).forEach((day) => {
    scheduleData[day].sort((a, b) => a.attractionOrder - b.attractionOrder)
  })

  return {
    tripTitle: title,
    content: content,
    startDate: startDate,
    endDate: endDate,
    isPublic: isPublic,
    scheduleData: scheduleData,
  }
}
