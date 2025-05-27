package com.ssafy.where2meow.ai.controller;

import com.ssafy.where2meow.ai.dto.CreateRequest;
import com.ssafy.where2meow.ai.dto.UpdateRequest;
import com.ssafy.where2meow.ai.service.AiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Slf4j
public class AiController {

  private final AiService aiService;

  @PostMapping("/create")
  public ResponseEntity<String> create(@RequestBody @Valid CreateRequest request) {
    log.info("plan create request: {}", request.getQuery());
    try {
      String response = aiService.createPlanWithImages(request.getQuery());
      return ResponseEntity.ok()
          .contentType(MediaType.APPLICATION_JSON)
          .body(response);
    } catch (Exception e) {
      log.error("AI 여행 계획 생성 실패: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
          .body("{\"error\":\"AI 서비스에 연결할 수 없습니다.\"}");
    }
  }

  @PostMapping("/update")
  public ResponseEntity<String> update(@RequestBody @Valid UpdateRequest request) {
    log.info("plan update request: {}", request.getQuery());
    try {
      String response = aiService.updatePlanWithImages(request.getQuery(), request.getPlan());
      return ResponseEntity.ok()
          .contentType(MediaType.APPLICATION_JSON)
          .body(response);
    } catch (Exception e) {
      log.error("AI 여행 계획 업데이트 실패: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
          .body("{\"error\":\"AI 서비스에 연결할 수 없습니다.\"}");
    }
  }
}
