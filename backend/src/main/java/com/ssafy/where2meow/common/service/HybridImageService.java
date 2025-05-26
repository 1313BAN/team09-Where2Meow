package com.ssafy.where2meow.common.service;

import com.ssafy.where2meow.attraction.entity.Attraction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class HybridImageService {

  @Value("${app.base-url}")
  private String baseUrl;

  @Autowired
  private ImageCacheService imageCacheService;

  /**
   * 최적의 이미지 URL 반환 (고화질 캐싱 방식)
   */
  public String getBestImageUrl(Attraction attraction) {
    Integer attractionId = attraction.getAttractionId();

    // 1. 캐시된 고화질 이미지 우선 확인
    String cachedUrl = imageCacheService.getCachedImageUrl(attractionId);
    if (cachedUrl != null) {
    log.info("캐시된 고화질 이미지 사용: attractionId={}", attractionId);
      return baseUrl + cachedUrl;
    }

    // 2. 한국관광공사 이미지가 있으면 고화질로 캐싱
    String originalUrl = attraction.getFirstImage1();
    if (originalUrl != null && !originalUrl.trim().isEmpty()) {
      String cachedImageUrl = imageCacheService.downloadAndCacheImage(originalUrl, attractionId);
      if (cachedImageUrl != null) {
        log.info("한국관광공사 이미지 고화질 캐싱 성공: attractionId={}", attractionId);
        return baseUrl + cachedImageUrl;
      } else {
        // 캐싱 실패시 원본 URL 직접 반환
        log.debug("캐싱 실패, 원본 URL 반환: attractionId={}", attractionId);
        return originalUrl;
      }
    }

    // 3. 이미지가 없는 경우 기본 이미지
    log.debug("기본 이미지 사용: attractionId={}", attractionId);
    return baseUrl + "/images/default-attraction.jpg";
  }

  /**
   * 특정 크기의 이미지 URL 반환 (하위 호환성을 위한 메서드)
   */
  public String getBestImageUrl(Attraction attraction, int width, int height) {
    // 크기 파라미터는 무시하고 고화질 이미지 반환
    return getBestImageUrl(attraction);
  }

  /**
   * 이미지 캐시 강제 새로고침
   */
  public String refreshImageCache(Attraction attraction) {
    Integer attractionId = attraction.getAttractionId();
    
    // 기존 캐시 삭제
    imageCacheService.deleteCachedImage(attractionId);
    
    // 새로 캐싱
    return getBestImageUrl(attraction);
  }
}
