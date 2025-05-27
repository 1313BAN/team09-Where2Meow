package com.ssafy.where2meow.ai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ssafy.where2meow.attraction.entity.Attraction;
import com.ssafy.where2meow.attraction.repository.AttractionRepository;
import com.ssafy.where2meow.common.service.HybridImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@Slf4j
public class AiService {

  @Value("${ai.service.base-url}")
  private String aiServiceBaseUrl;

  @Autowired
  private AttractionRepository attractionRepository;

  @Autowired
  private HybridImageService hybridImageService;

  private final ObjectMapper objectMapper = new ObjectMapper();

  /**
   * AI 여행 계획 생성 (이미지 URL 포함)
   */
  public String createPlanWithImages(String query) {
    try {
      // AI 서비스 호출
      String aiResponse = callAiService("/ai/create", createRequestBody(query));

      // 응답에 이미지 URL 추가
      return addImageUrlsToResponse(aiResponse);

    } catch (Exception e) {
      log.error("AI 여행 계획 생성 실패: {}", e.getMessage(), e);
      throw new RuntimeException("AI 서비스 호출 중 오류가 발생했습니다.", e);
    }
  }

  /**
   * AI 여행 계획 업데이트 (이미지 URL 포함)
   */
  public String updatePlanWithImages(String query, Object currentPlan) {
    try {
      // AI 서비스 호출
      String aiResponse = callAiService("/ai/update", createUpdateRequestBody(query, currentPlan));

      // 응답에 이미지 URL 추가
      return addImageUrlsToResponse(aiResponse);

    } catch (Exception e) {
      log.error("AI 여행 계획 업데이트 실패: {}", e.getMessage(), e);
      throw new RuntimeException("AI 서비스 호출 중 오류가 발생했습니다.", e);
    }
  }

  /**
   * AI 서비스 호출
   */
  private String callAiService(String endpoint, Object requestBody) {
    try {
      String url = aiServiceBaseUrl + endpoint;
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      HttpEntity<Object> entity = new HttpEntity<>(requestBody, headers);

      RestTemplate restTemplate = new RestTemplate();
      ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

      if (response.getStatusCode().is2xxSuccessful()) {
        return response.getBody();
      } else {
        throw new RuntimeException("AI 서비스 응답 오류: " + response.getStatusCode());
      }

    } catch (RestClientException e) {
      log.error("AI 서비스 호출 실패: {}", e.getMessage());
      throw new RuntimeException("AI 서비스에 연결할 수 없습니다.", e);
    }
  }

  /**
   * AI 응답에 이미지 URL 추가
   */
  private String addImageUrlsToResponse(String aiResponse) {
    try {
      JsonNode responseNode = objectMapper.readTree(aiResponse);

      // attractions 배열이 있는지 확인
      if (responseNode.has("attractions") && responseNode.get("attractions").isArray()) {
        ArrayNode attractionsArray = (ArrayNode) responseNode.get("attractions");

        // 각 attraction에 이미지 URL 추가
        for (JsonNode attractionNode : attractionsArray) {
          if (attractionNode.has("attractionId")) {
            int attractionId = attractionNode.get("attractionId").asInt();

            // 데이터베이스에서 관광지 정보 조회
            Optional<Attraction> attractionOpt = attractionRepository.findById(attractionId);
            if (attractionOpt.isPresent()) {
              Attraction attraction = attractionOpt.get();

              // 이미지 URL 생성
              String imageUrl = hybridImageService.getBestImageUrl(attraction);

              // JSON 노드에 이미지 URL 추가
              if (attractionNode instanceof ObjectNode) {
                ObjectNode attractionObject = (ObjectNode) attractionNode;
                attractionObject.put("imageUrl", imageUrl);

                // 추가 정보도 포함 (선택적)
                attractionObject.put("stateName", getStateName(attraction));
                attractionObject.put("cityName", getCityName(attraction));

                log.debug("관광지 {} 이미지 URL 추가: {}", attractionId, imageUrl);
              }
            } else {
              log.warn("관광지 ID {}를 찾을 수 없습니다.", attractionId);
            }
          }
        }
      }

      return objectMapper.writeValueAsString(responseNode);

    } catch (Exception e) {
      log.error("이미지 URL 추가 중 오류 발생: {}", e.getMessage(), e);
      // 원본 응답 반환 (이미지 URL 추가 실패해도 AI 응답은 유지)
      return aiResponse;
    }
  }

  /**
   * 생성 요청 본문 생성
   */
  private Object createRequestBody(String query) {
    return new CreateRequest(query);
  }

  /**
   * 업데이트 요청 본문 생성
   */
  private Object createUpdateRequestBody(String query, Object currentPlan) {
    return new UpdateRequest(query, currentPlan);
  }

  /**
   * 시도명 조회 (추후 구현 또는 기존 로직 활용)
   */
  private String getStateName(Attraction attraction) {
    // 기존 State 엔티티를 활용하거나 간단한 매핑 로직
    return ""; // TODO: 구현 필요시 추가
  }

  /**
   * 시군구명 조회 (추후 구현 또는 기존 로직 활용)
   */
  private String getCityName(Attraction attraction) {
    // 기존 City 엔티티를 활용하거나 간단한 매핑 로직
    return ""; // TODO: 구현 필요시 추가
  }

  // 내부 클래스들
  public static class CreateRequest {
    public String query;

    public CreateRequest(String query) {
      this.query = query;
    }
  }

  public static class UpdateRequest {
    public String query;
    public Object plan;

    public UpdateRequest(String query, Object plan) {
      this.query = query;
      this.plan = plan;
    }
  }
}
