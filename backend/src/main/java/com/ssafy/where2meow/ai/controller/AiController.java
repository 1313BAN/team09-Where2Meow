package com.ssafy.where2meow.ai.controller;

import com.ssafy.where2meow.ai.dto.CreateRequest;
import com.ssafy.where2meow.ai.dto.UpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Slf4j
public class AiController {
  @PostMapping("/create")
  public ResponseEntity<String> create(@RequestBody CreateRequest request) {
    log.info("create request: {}", request);
    String url = "http://localhost:8000/ai/create";

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<CreateRequest> entity = new HttpEntity<>(request, headers);

    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

    // AI 서버의 status, header, body를 그대로 전달하려면 아래처럼 처리
    return ResponseEntity
        .status(response.getStatusCode())
        .headers(response.getHeaders())
        .body(response.getBody());
  }

  @PostMapping("/update")
  public ResponseEntity<String> update(@RequestBody UpdateRequest request) {
    String url = "http://localhost:8000/ai/update";

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<UpdateRequest> entity = new HttpEntity<>(request, headers);

    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

    // AI 서버의 status, header, body를 그대로 전달하려면 아래처럼 처리
    return ResponseEntity
        .status(response.getStatusCode())
        .headers(response.getHeaders())
        .body(response.getBody());
  }
}
