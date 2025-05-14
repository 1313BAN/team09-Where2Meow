package com.ssafy.where2meow.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException e) {
    Map<String, String> errorResponse = new HashMap<>();
    errorResponse.put("message", e.getMessage());
    return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<Map<String, String>> handleBadCredentialsException(BadCredentialsException e) {
    Map<String, String> errorResponse = new HashMap<>();
    errorResponse.put("message", "아이디 또는 비밀번호가 일치하지 않습니다.");
    return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(ForbiddenAccessException.class)
  public ResponseEntity<Map<String, String>> handleUnauthorizedAccessException(ForbiddenAccessException e) {
    Map<String, String> errorResponse = new HashMap<>();
    errorResponse.put("message", e.getMessage());
    return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<Map<String, Object>> handleEntityNotFoundException(EntityNotFoundException e) {
    Map<String, Object> errorResponse = new HashMap<>();
    errorResponse.put("message", "존재하지 않는 " + e.getEntityName() + "입니다.");

    if (e.getEntityName() != null) {
      errorResponse.put("entityName", e.getEntityName());
      errorResponse.put("fieldName", e.getFieldName());
      errorResponse.put("fieldValue", e.getFieldValue());
    }

    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(LogoutException.class)
  public ResponseEntity<Map<String, String>> handleLogoutException(LogoutException e) {
    Map<String, String> errorResponse = new HashMap<>();
    errorResponse.put("message", "로그아웃 처리 중 오류가 발생했습니다: " + e.getMessage());
    log.error("[로그아웃] 오류 발생: {}", e.getMessage());
    return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException e) {
    Map<String, String> errorResponse = new HashMap<>();
    errorResponse.put("message", e.getMessage());
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ForbiddenAccessException.class)
  public ResponseEntity<Map<String, String>> handleForbiddenAccessException(ForbiddenAccessException e) {
    Map<String, String> errorResponse = new HashMap<>();
    errorResponse.put("message", e.getMessage());
    return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, String>> handleGlobalException(Exception e) {
    Map<String, String> errorResponse = new HashMap<>();
    errorResponse.put("message", "서버 오류가 발생했습니다: " + e.getMessage());
    return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
