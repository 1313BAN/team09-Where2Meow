/**
 * 고화질 이미지 URL 처리를 위한 유틸리티 함수
 */

// 기본 이미지 URL
export const DEFAULT_IMAGE_URL = '/images/default-attraction.jpg'

/**
 * 이미지 URL을 올바른 형식으로 변환합니다.
 * @param {string} imageUrl - 원본 이미지 URL
 * @returns {string} - 처리된 이미지 URL
 */
export function getFullImageUrl(imageUrl) {
  if (!imageUrl) return DEFAULT_IMAGE_URL
  
  // 이미 완전한 URL인 경우 (외부 이미지 또는 백엔드 URL)
  if (imageUrl.startsWith('http')) {
    return imageUrl
  }
  
  // 기본 이미지 경로로 시작하는 경우
  if (imageUrl === DEFAULT_IMAGE_URL) {
    return imageUrl
  }
  
  // 원래 방식으로 돌아가기: 드롭인 방식으로 수정
  return imageUrl
}

/**
 * 이미지 URL에 캐시 버스팅 파라미터를 추가합니다.
 * @param {string} imageUrl - 원본 이미지 URL
 * @returns {string} - 캐시 버스팅이 적용된 이미지 URL
 */
export function addCacheBusting(imageUrl) {
  if (!imageUrl || imageUrl === DEFAULT_IMAGE_URL) return imageUrl
  
  const timestamp = Date.now()
  const separator = imageUrl.includes('?') ? '&' : '?'
  return `${imageUrl}${separator}t=${timestamp}`
}

/**
 * 이미지 로딩 에러시 기본 이미지로 대체
 * @param {Event} event - 이미지 로드 에러 이벤트
 */
export function handleImageError(event) {
  const img = event.target
  if (img.src !== DEFAULT_IMAGE_URL) {
    img.src = DEFAULT_IMAGE_URL
  }
}

/**
 * 이미지 레이지 로딩을 위한 Intersection Observer 생성
 * @param {Function} callback - 이미지가 뷰포트에 들어왔을 때 실행할 콜백
 * @returns {IntersectionObserver} - Intersection Observer 인스턴스
 */
export function createImageObserver(callback) {
  const options = {
    root: null,
    rootMargin: '50px',
    threshold: 0.1
  }
  
  return new IntersectionObserver(callback, options)
}

/**
 * 고화질 이미지를 위한 프리로딩
 * @param {string} imageUrl - 프리로드할 이미지 URL
 * @returns {Promise} - 이미지 로딩 완료를 나타내는 Promise
 */
export function preloadImage(imageUrl) {
  return new Promise((resolve, reject) => {
    const img = new Image()
    img.onload = () => resolve(img)
    img.onerror = reject
    img.src = imageUrl
  })
}
