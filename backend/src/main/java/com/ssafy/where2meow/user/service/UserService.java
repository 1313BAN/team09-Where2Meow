package com.ssafy.where2meow.user.service;

import com.ssafy.where2meow.user.dto.UserResponse;
import com.ssafy.where2meow.user.dto.UserSignupRequest;
import com.ssafy.where2meow.user.dto.UserUpdateRequest;
import com.ssafy.where2meow.user.entity.User;
import com.ssafy.where2meow.exception.DuplicateUserException;
import com.ssafy.where2meow.exception.UserNotFoundException;
import com.ssafy.where2meow.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 사용자 관련 비즈니스 로직을 처리하는 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  /**
   * 회원 가입
   *
   * @param request 회원가입 요청 정보
   * @return 가입된 회원 정보
   */
  @Transactional
  public UserResponse signup(UserSignupRequest request) {
    // 중복 체크
    if (userRepository.existsByEmailAndIsActiveTrue(request.getEmail())) {
      throw new DuplicateUserException("이미 사용 중인 이메일입니다.");
    }
    if (userRepository.existsByNicknameAndIsActiveTrue(request.getNickname())) {
      throw new DuplicateUserException("이미 사용 중인 닉네임입니다.");
    }

    if (!request.getPassword().equals(request.getConfirmPassword())) {
      throw new IllegalArgumentException("비밀번호와 확인 비밀번호가 일치하지 않습니다.");
    }

    // 비밀번호 복잡도 검증
    PasswordComplexityUtil.validatePasswordComplexity(request.getPassword());

    // 비밀번호 암호화
    User user = request.toEntity();
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setIsActive(true);

    User savedUser = userRepository.save(user);

    return UserResponse.fromEntity(savedUser);
  }

  /**
   * 사용자 정보 조회
   *
   * @param uuid 사용자 UUID
   * @return 사용자 정보
   */
  public UserResponse getUserByUuid(UUID uuid) {
    User user = userRepository.findByUuidAndIsActiveTrue(uuid)
        .orElseThrow(() -> new UserNotFoundException(uuid));

    return UserResponse.fromEntity(user);
  }

  public int getUserIdByUuid(UUID uuid) {
    User user = userRepository.findByUuidAndIsActiveTrue(uuid)
        .orElseThrow(() -> new UserNotFoundException(uuid));

    return user.getUserId();
  }

  /**
   * 사용자 정보 수정
   *
   * @param uuid    사용자 UUID
   * @param request 수정 요청 정보
   * @return 수정된 사용자 정보
   */
  @Transactional
  public UserResponse updateUser(UUID uuid, UserUpdateRequest request) {
    User user = userRepository.findByUuidAndIsActiveTrue(uuid)
        .orElseThrow(() -> new UserNotFoundException(uuid));

    // 이메일 변경 시 중복 체크
    if (request.getEmail() != null && !request.getEmail().equals(user.getEmail()) &&
        userRepository.existsByEmailAndIsActiveTrue(request.getEmail())) {
      throw new DuplicateUserException("이미 사용 중인 이메일입니다.");
    }

    // 닉네임 변경 시 중복 체크
    if (request.getNickname() != null && !request.getNickname().equals(user.getNickname()) &&
        userRepository.existsByNicknameAndIsActiveTrue(request.getNickname())) {
      throw new DuplicateUserException("이미 사용 중인 닉네임입니다.");
    }

    request.updateEntity(user);

    User updatedUser = userRepository.save(user);

    return UserResponse.fromEntity(updatedUser);
  }

  /**
   * 사용자 삭제 (비활성화)
   *
   * @param uuid 사용자 UUID
   */
  @Transactional
  public void deleteUser(UUID uuid) {
    User user = userRepository.findByUuidAndIsActiveTrue(uuid)
        .orElseThrow(() -> new UserNotFoundException(uuid));

    // 완전 삭제 대신 비활성화 처리
    user.setIsActive(false);
    userRepository.save(user);
  }
}
