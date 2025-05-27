package com.ssafy.where2meow.common.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
@Slf4j
public class ImageCacheService {

  @Value("${app.image.cache.directory}")
  private String IMAGE_CACHE_DIR;

  @Value("${app.image.cache.url-prefix}")
  private String IMAGE_URL_PREFIX;

  // 고화질 저장을 위한 기본 크기 설정
  private static final int DEFAULT_WIDTH = 400;
  private static final int DEFAULT_HEIGHT = 300;

  @PostConstruct
  public void init() {
    // 현재 작업 디렉토리 확인
    String currentDir = System.getProperty("user.dir");
    log.info("현재 작업 디렉토리: {}", currentDir);

    // 캐시 디렉토리 생성
    File cacheDir = new File(IMAGE_CACHE_DIR);
    log.info("이미지 캐시 디렉토리 절대 경로: {}", cacheDir.getAbsolutePath());

    if (!cacheDir.exists()) {
      cacheDir.mkdirs();
    }
    log.info("이미지 캐시 디렉토리 초기화 완료: {}", IMAGE_CACHE_DIR);
  }

  /**
   * 캐시된 이미지 URL 반환 (고화질 버전)
   */
  public String getCachedImageUrl(Integer attractionId) {
    if (attractionId == null || attractionId <= 0) {
      log.warn("유효하지 않은 attractionId: {}", attractionId);
      return null;
    }

    String fileName = generateFileName(attractionId);
    File cachedFile = new File(IMAGE_CACHE_DIR + fileName);

    log.debug("캐시 이미지 확인: attractionId={}, fileName={}, exists={}", attractionId, fileName, cachedFile.exists());

    if (cachedFile.exists()) {
      log.debug("캐시된 이미지 파일 발견: {}", fileName);
      return IMAGE_URL_PREFIX + fileName;
    }

    return null;
  }

  /**
   * 이미지 다운로드 및 고화질 캐싱
   */
  public String downloadAndCacheImage(String originalUrl, Integer attractionId) {
    try {
      String fileName = generateFileName(attractionId);
      File cachedFile = new File(IMAGE_CACHE_DIR + fileName);

      // 이미 캐시된 파일이 있으면 URL 반환
      if (cachedFile.exists()) {
        log.debug("이미 캐시된 이미지 반환: {}", fileName);
        return IMAGE_URL_PREFIX + fileName;
      }

      log.info("이미지 다운로드 시작: attractionId={}, url={}", attractionId, originalUrl);

      // 원본 이미지 다운로드
      URL url = new URL(originalUrl);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setConnectTimeout(10000); // 10초 타임아웃
      connection.setReadTimeout(30000); // 30초 타임아웃
      connection.setRequestProperty("User-Agent",
          "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");

      try (InputStream inputStream = connection.getInputStream()) {
        BufferedImage originalImage = ImageIO.read(inputStream);

        if (originalImage != null) {
          // 고화질 이미지 처리 및 저장
          BufferedImage processedImage = processImageForHighQuality(originalImage);

          // 파일 저장 (JPEG 고품질)
          saveHighQualityImage(processedImage, cachedFile);

          log.info("고화질 이미지 캐싱 완료: {}", fileName);
          return IMAGE_URL_PREFIX + fileName;
        } else {
          log.warn("이미지 읽기 실패 - 유효하지 않은 이미지: {}", originalUrl);
        }
      }
    } catch (Exception e) {
      log.error("이미지 캐싱 실패 - attractionId: {}, url: {}", attractionId, originalUrl, e);
    }

    return null;
  }

  /**
   * 고화질 이미지 처리
   */
  private BufferedImage processImageForHighQuality(BufferedImage originalImage) {
    int originalWidth = originalImage.getWidth();
    int originalHeight = originalImage.getHeight();

    // 원본이 작은 경우 원본 크기 유지, 큰 경우 적절한 크기로 조정
    int targetWidth, targetHeight;

    if (originalWidth <= DEFAULT_WIDTH && originalHeight <= DEFAULT_HEIGHT) {
      // 원본이 작으면 그대로 사용
      return originalImage;
    } else {
      // 비율을 유지하면서 리사이징
      double aspectRatio = (double) originalWidth / originalHeight;

      if (aspectRatio > 1) {
        // 가로가 더 긴 경우
        targetWidth = Math.min(originalWidth, DEFAULT_WIDTH * 2); // 더 큰 크기로 설정
        targetHeight = (int) (targetWidth / aspectRatio);
      } else {
        // 세로가 더 긴 경우
        targetHeight = Math.min(originalHeight, DEFAULT_HEIGHT * 2);
        targetWidth = (int) (targetHeight * aspectRatio);
      }

      return resizeImageHighQuality(originalImage, targetWidth, targetHeight);
    }
  }

  /**
   * 고품질 이미지 리사이징
   */
  private BufferedImage resizeImageHighQuality(BufferedImage originalImage, int width, int height) {
    BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    Graphics2D g2d = resizedImage.createGraphics();

    // 최고 품질 설정
    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
    g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
    g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);

    g2d.drawImage(originalImage, 0, 0, width, height, null);
    g2d.dispose();

    return resizedImage;
  }

  /**
   * 고품질 JPEG 저장
   */
  private void saveHighQualityImage(BufferedImage image, File file) throws Exception {
    try (javax.imageio.stream.ImageOutputStream outputStream = ImageIO.createImageOutputStream(file)) {
      javax.imageio.ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();
      try {
        javax.imageio.ImageWriteParam param = writer.getDefaultWriteParam();

        // 압축 품질 설정 (0.9 = 90% 품질)
        param.setCompressionMode(javax.imageio.ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(0.9f);

        writer.setOutput(outputStream);
        writer.write(null, new javax.imageio.IIOImage(image, null, null), param);
      } finally {
        writer.dispose();
      }
    }
  }

  /**
   * 파일명 생성 (크기 정보 제거)
   */
  private String generateFileName(Integer attractionId) {
    return String.format("attraction_%d.jpg", attractionId);
  }

  /**
   * 캐시된 이미지 삭제 (필요시)
   */
  public boolean deleteCachedImage(Integer attractionId) {
    String fileName = generateFileName(attractionId);
    File cachedFile = new File(IMAGE_CACHE_DIR + fileName);

    if (cachedFile.exists()) {
      boolean deleted = cachedFile.delete();
      if (deleted) {
        log.info("캐시된 이미지 삭제 완료: {}", fileName);
      }
      return deleted;
    }

    return false;
  }
}
