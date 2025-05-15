package com.ssafy.where2meow.exception;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {

  public UserNotFoundException(String message) {
    super(message);
  }

  public UserNotFoundException(UUID uuid) {
    super("사용자를 찾을 수 없습니다. ID: " + uuid);
  }
}
