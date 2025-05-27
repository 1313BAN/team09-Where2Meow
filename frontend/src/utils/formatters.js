/**
 * 날짜를 상대적 시간으로 포맷팅
 * @param {string} dateString - ISO 날짜 문자열
 * @returns {string} 포맷된 날짜 문자열
 */
export const formatRelativeTime = (dateString) => {
  const date = new Date(dateString)
  const now = new Date()
  const diffMs = now - date
  const diffMins = Math.floor(diffMs / 60000)
  const diffHours = Math.floor(diffMs / 3600000)
  const diffDays = Math.floor(diffMs / 86400000)

  if (diffMins < 1) return '방금 전'
  if (diffMins < 60) return `${diffMins}분 전`
  if (diffHours < 24) return `${diffHours}시간 전`
  if (diffDays < 7) return `${diffDays}일 전`
  return date.toLocaleDateString()
}

/**
 * 날짜를 YYYY.MM.DD 형식으로 포맷팅
 * @param {string} dateString - ISO 날짜 문자열
 * @returns {string} 포맷된 날짜 문자열
 */
export const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  })
}

/**
 * 날짜를 YYYY.MM.DD HH:mm 형식으로 포맷팅
 * @param {string} dateString - ISO 날짜 문자열
 * @returns {string} 포맷된 날짜 문자열
 */
export const formatDateTime = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

/**
 * 숫자를 천 단위로 콤마를 추가하여 포맷팅
 * @param {number} num - 포맷팅할 숫자
 * @returns {string} 포맷된 숫자 문자열
 */
export const formatNumber = (num) => {
  if (num >= 1000000) {
    return Math.floor(num / 100000) / 10 + 'M'
  }
  if (num >= 1000) {
    return Math.floor(num / 100) / 10 + 'K'
  }
  return num.toString()
}

/**
 * 문자열을 지정된 길이로 자르고 ... 추가
 * @param {string} str - 자를 문자열
 * @param {number} length - 최대 길이
 * @returns {string} 자른 문자열
 */
export const truncateText = (str, length = 100) => {
  if (!str) return ''
  if (str.length <= length) return str
  return str.substring(0, length) + '...'
}
