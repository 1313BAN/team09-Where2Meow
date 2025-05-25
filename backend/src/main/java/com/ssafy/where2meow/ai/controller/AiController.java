package com.ssafy.where2meow.ai.controller;

import com.ssafy.where2meow.ai.dto.CreateRequest;
import com.ssafy.where2meow.ai.dto.UpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Slf4j
public class AiController {

  @Value("${ai.service.base-url:http://localhost:8000}")
  private String aiServiceBaseUrl;

  @PostMapping("/create")
  public ResponseEntity<String> create(@RequestBody @Valid CreateRequest request) {
    log.info("plan create request: {}", request.getQuery());
    try {
      String url = aiServiceBaseUrl + "/ai/create";

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      HttpEntity<CreateRequest> entity = new HttpEntity<>(request, headers);
      RestTemplate restTemplate = new RestTemplate();
      ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

      return ResponseEntity
          .status(response.getStatusCode())
          .headers(response.getHeaders())
          .body(response.getBody());
    } catch (RestClientException e) {
      log.error("AI 서비스 호출 실패: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
          .body("{\"error\":\"AI 서비스에 연결할 수 없습니다.\"}");
    }
  }

  @PostMapping("/update")
  public ResponseEntity<String> update(@RequestBody @Valid UpdateRequest request) {
    log.info("plan update request: {}", request.getQuery());
    try {
      String url = aiServiceBaseUrl + "/ai/update";

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      HttpEntity<UpdateRequest> entity = new HttpEntity<>(request, headers);
      RestTemplate restTemplate = new RestTemplate();
      ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

      return ResponseEntity
          .status(response.getStatusCode())
          .headers(response.getHeaders())
          .body(response.getBody());

    } catch (RestClientException e) {
      log.error("AI 서비스 호출 실패: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
          .body("{\"error\":\"AI 서비스에 연결할 수 없습니다.\"}");
    }
  }
}
