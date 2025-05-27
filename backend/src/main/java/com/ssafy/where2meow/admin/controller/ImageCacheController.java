package com.ssafy.where2meow.admin.controller;

import com.ssafy.where2meow.attraction.service.AttractionService;
import com.ssafy.where2meow.common.service.ImageCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/image")
@RequiredArgsConstructor
@Slf4j
public class ImageCacheController {

  private final AttractionService attractionService;
  private final ImageCacheService imageCacheService;

  /**
   * 특정 관광지 이미지 캐시 새로고침
   */
  @PostMapping("/refresh/{attractionId}")
  public ResponseEntity<Map<String, Object>> refreshImageCache(@PathVariable Integer attractionId) {
    try {
      log.info("이미지 캐시 새로고침 요청: attractionId={}", attractionId);
      
      // 기존 캐시 삭제
      boolean deleted = imageCacheService.deleteCachedImage(attractionId);
      
      // 새로운 고화질 이미지 캐싱
      String newImageUrl = attractionService.refreshAttractionImageCache(attractionId);
      
      Map<String, Object> response = new HashMap<>();
      response.put("success", true);
      response.put("attractionId", attractionId);
      response.put("deleted", deleted);
      response.put("newImageUrl", newImageUrl);
      response.put("message", "이미지 캐시가 성공적으로 새로고침되었습니다.");
      
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      log.error("이미지 캐시 새로고침 실패: attractionId={}", attractionId, e);
      
      Map<String, Object> response = new HashMap<>();
      response.put("success", false);
      response.put("attractionId", attractionId);
      response.put("message", "이미지 캐시 새로고침에 실패했습니다: " + e.getMessage());
      
      return ResponseEntity.badRequest().body(response);
    }
  }

  /**
   * 특정 관광지 이미지 캐시 삭제
   */
  @DeleteMapping("/cache/{attractionId}")
  public ResponseEntity<Map<String, Object>> deleteCachedImage(@PathVariable Integer attractionId) {
    try {
      log.info("이미지 캐시 삭제 요청: attractionId={}", attractionId);
      
      boolean deleted = imageCacheService.deleteCachedImage(attractionId);
      
      Map<String, Object> response = new HashMap<>();
      response.put("success", true);
      response.put("attractionId", attractionId);
      response.put("deleted", deleted);
      response.put("message", deleted ? "이미지 캐시가 삭제되었습니다." : "삭제할 캐시 이미지가 없습니다.");
      
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      log.error("이미지 캐시 삭제 실패: attractionId={}", attractionId, e);
      
      Map<String, Object> response = new HashMap<>();
      response.put("success", false);
      response.put("attractionId", attractionId);
      response.put("message", "이미지 캐시 삭제에 실패했습니다: " + e.getMessage());
      
      return ResponseEntity.badRequest().body(response);
    }
  }

  /**
   * 관광지 이미지 URL 조회 (캐시 상태 확인용)
   */
  @GetMapping("/status/{attractionId}")
  public ResponseEntity<Map<String, Object>> getImageCacheStatus(@PathVariable Integer attractionId) {
    try {
      String cachedUrl = imageCacheService.getCachedImageUrl(attractionId);
      String currentUrl = attractionService.getAttractionImageUrl(attractionId);
      
      Map<String, Object> response = new HashMap<>();
      response.put("attractionId", attractionId);
      response.put("hasCachedImage", cachedUrl != null);
      response.put("cachedImageUrl", cachedUrl);
      response.put("currentImageUrl", currentUrl);
      
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      log.error("이미지 캐시 상태 조회 실패: attractionId={}", attractionId, e);
      
      Map<String, Object> response = new HashMap<>();
      response.put("success", false);
      response.put("attractionId", attractionId);
      response.put("message", "이미지 캐시 상태 조회에 실패했습니다: " + e.getMessage());
      
      return ResponseEntity.badRequest().body(response);
    }
  }
}
