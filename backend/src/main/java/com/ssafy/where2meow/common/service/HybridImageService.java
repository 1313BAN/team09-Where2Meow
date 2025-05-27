package com.ssafy.where2meow.common.service;

import com.ssafy.where2meow.attraction.entity.Attraction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class HybridImageService {

  @Value("${app.base-url}")
  private String baseUrl;

  @Autowired
  private ImageCacheService imageCacheService;

  /**
   * 최적의 이미지 URL 반환 (고화질 캐싱 방식) - 임시 해결책 적용
   */
  public String getBestImageUrl(Attraction attraction) {
    if (attraction == null) {
      log.warn("Attraction 객체가 null입니다. 기본 이미지를 반환합니다.");
      return baseUrl + "/images/default-attraction.jpg";
    }

    Integer attractionId = attraction.getAttractionId();
    if (attractionId == null) {
      log.warn("Attraction ID가 null입니다. 기본 이미지를 반환합니다.");
      return baseUrl + "/images/default-attraction.jpg";
    }

    log.debug("이미지 URL 생성 시작: attractionId={}, baseUrl={}", attractionId, baseUrl);

    // 1. 캐시된 고화질 이미지 우선 확인
    String cachedUrl = imageCacheService.getCachedImageUrl(attractionId);
    if (cachedUrl != null) {
      String fullUrl = baseUrl + cachedUrl;
      log.info("캐시된 고화질 이미지 사용: attractionId={}, cachedUrl={}, fullUrl={}", attractionId, cachedUrl, fullUrl);
      return fullUrl;
    }

    // 2. 한국관광공사 이미지가 있으면 원본 URL 즉시 반환 + 백그라운드 캐싱
    String originalUrl = attraction.getFirstImage1();
    log.debug("DB에서 가져온 원본 이미지 URL: attractionId={}, originalUrl={}", attractionId, originalUrl);
    
    if (originalUrl != null && !originalUrl.trim().isEmpty()) {
      // 백그라운드에서 비동기적 캐싱 시작
      CompletableFuture.runAsync(() -> {
        try {
          log.info("백그라운드 이미지 캐싱 시작: attractionId={}", attractionId);
          imageCacheService.downloadAndCacheImage(originalUrl, attractionId);
        } catch (Exception e) {
          log.warn("백그라운드 이미지 캐싱 실패: attractionId={}, error={}", attractionId, e.getMessage());
        }
      });

      // 원본 URL 즉시 반환 (사용자가 즉시 이미지를 볼 수 있음)
      log.info("원본 URL 즉시 반환 (백그라운드 캐싱): attractionId={}, originalUrl={}", attractionId, originalUrl);
      return originalUrl;
    }

    // 3. 이미지가 없는 경우 기본 이미지
    String defaultUrl = baseUrl + "/images/default-attraction.jpg";
    log.debug("기본 이미지 사용: attractionId={}, defaultUrl={}", attractionId, defaultUrl);
    return defaultUrl;
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
    if (attraction == null || attraction.getAttractionId() == null) {
      throw new IllegalArgumentException("유효하지 않은 Attraction 객체입니다.");
    }

    Integer attractionId = attraction.getAttractionId();

    // 기존 캐시 삭제
    boolean deleted = imageCacheService.deleteCachedImage(attractionId);
    if (!deleted) {
      log.warn("기존 캐시 삭제 실패: attractionId={}", attractionId);
    }

    // 새로 캐싱
    return getBestImageUrl(attraction);
  }
}
