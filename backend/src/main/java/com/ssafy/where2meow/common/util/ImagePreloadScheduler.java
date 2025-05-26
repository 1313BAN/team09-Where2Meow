package com.ssafy.where2meow.common.util;

import com.ssafy.where2meow.attraction.entity.Attraction;
import com.ssafy.where2meow.attraction.repository.AttractionRepository;
import com.ssafy.where2meow.common.service.HybridImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ImagePreloadScheduler {

  @Autowired
  private AttractionRepository attractionRepository;

  @Autowired
  private HybridImageService hybridImageService;

  /**
   * 인기 관광지 이미지 사전 로딩 (매일 새벽 2시 실행)
   */
  @Scheduled(cron = "0 0 2 * * *")
  public void preloadPopularAttractionImages() {
    log.info("인기 관광지 이미지 사전 로딩 시작");

    try {
      // 인기 관광지 100개 조회 (리뷰 수 기준)
      Pageable pageable = PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "attractionName"));
      Page<Attraction> popularAttractions = attractionRepository.findAll(pageable);

      int processedCount = 0;
      for (Attraction attraction : popularAttractions.getContent()) {
        try {
          // 다양한 사이즈로 이미지 사전 생성
          hybridImageService.getBestImageUrl(attraction, 80, 80);
          hybridImageService.getBestImageUrl(attraction, 150, 150);

          processedCount++;

          // API 호출 제한을 위한 딜레이
          Thread.sleep(100);
        } catch (Exception e) {
          log.error("이미지 사전 로딩 실패 - attractionId: {}", attraction.getAttractionId(), e);
        }
      }

      log.info("인기 관광지 이미지 사전 로딩 완료 - 처리된 관광지: {}", processedCount);
    } catch (Exception e) {
      log.error("이미지 사전 로딩 중 오류 발생", e);
    }
  }
}
