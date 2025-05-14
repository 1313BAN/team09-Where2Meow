package com.ssafy.where2meow.user.controller;

import com.ssafy.where2meow.user.dto.UserResponse;
import com.ssafy.where2meow.user.dto.UserSignupRequest;
import com.ssafy.where2meow.user.dto.UserUpdateRequest;
import com.ssafy.where2meow.user.dto.UuidRequest;
import com.ssafy.where2meow.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping("/user/signup")
  public ResponseEntity<UserResponse> signup(@RequestBody @Valid UserSignupRequest request) {
    UserResponse userResponse = userService.signup(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
  }

  @GetMapping("/user")
  public ResponseEntity<UserResponse> getUser(@RequestBody @Valid UuidRequest request) {
    UserResponse userResponse = userService.getUserByUuid(request.getUuid());
    return ResponseEntity.ok(userResponse);
  }

  @PutMapping("/user")
  public ResponseEntity<UserResponse> updateUser(@RequestBody @Valid UserUpdateRequest request) {
    UserResponse updatedUser = userService.updateUser(request.getUuid(), request);
    return ResponseEntity.ok(updatedUser);
  }

  @DeleteMapping("/user")
  public ResponseEntity<Void> deleteUser(@RequestBody @Valid UuidRequest request) {
    userService.deleteUser(request.getUuid());
    return ResponseEntity.noContent().build();
  }

}
