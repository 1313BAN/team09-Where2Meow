package com.ssafy.where2meow.test.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class ImageTestController {

  @Value("${app.base-url}")
  private String baseUrl;

  private static final String IMAGE_CACHE_DIR = "src/main/resources/static/images/attractions/";
  private static final String IMAGE_URL_PREFIX = "/images/attractions/";

  /**
   * 이미지 상태 테스트
   */
  @GetMapping("/image/{attractionId}")
  public ResponseEntity<Map<String, Object>> testImage(@PathVariable Integer attractionId) {
    Map<String, Object> result = new HashMap<>();
    
    String fileName = String.format("attraction_%d.jpg", attractionId);
    File imageFile = new File(IMAGE_CACHE_DIR + fileName);
    
    result.put("attractionId", attractionId);
    result.put("fileName", fileName);
    result.put("filePath", imageFile.getAbsolutePath());
    result.put("fileExists", imageFile.exists());
    result.put("fileSize", imageFile.exists() ? imageFile.length() : 0);
    result.put("expectedUrl", baseUrl + IMAGE_URL_PREFIX + fileName);
    result.put("relativeUrl", IMAGE_URL_PREFIX + fileName);
    result.put("baseUrl", baseUrl);
    
    return ResponseEntity.ok(result);
  }

  /**
   * 모든 캐시된 이미지 목록
   */
  @GetMapping("/images")
  public ResponseEntity<Map<String, Object>> listImages() {
    Map<String, Object> result = new HashMap<>();
    
    File cacheDir = new File(IMAGE_CACHE_DIR);
    if (cacheDir.exists() && cacheDir.isDirectory()) {
      File[] files = cacheDir.listFiles((dir, name) -> name.endsWith(".jpg"));
      
      result.put("cacheDirectory", cacheDir.getAbsolutePath());
      result.put("directoryExists", true);
      result.put("imageCount", files != null ? files.length : 0);
      
      if (files != null) {
        Map<String, Object> imageInfo = new HashMap<>();
        for (File file : files) {
          Map<String, Object> info = new HashMap<>();
          info.put("size", file.length());
          info.put("url", baseUrl + IMAGE_URL_PREFIX + file.getName());
          imageInfo.put(file.getName(), info);
        }
        result.put("images", imageInfo);
      }
    } else {
      result.put("cacheDirectory", cacheDir.getAbsolutePath());
      result.put("directoryExists", false);
      result.put("imageCount", 0);
    }
    
    return ResponseEntity.ok(result);
  }
}
