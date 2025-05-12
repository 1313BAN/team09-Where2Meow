package com.ssafy.where2meow.user.security;

import com.ssafy.where2meow.user.entity.User;
import com.ssafy.where2meow.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user;

    try {
      // UUID로 사용자 찾기 시도
      UUID uuid = UUID.fromString(username);
      user = userRepository.findByUuidAndIsActiveTrue(uuid)
              .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));
    } catch (IllegalArgumentException e) {
      // UUID 형식이 아니면 이메일로 시도
      user = userRepository.findByEmailAndIsActiveTrue(username)
              .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));
    }

    return org.springframework.security.core.userdetails.User.builder()
            .username(user.getUuid().toString())
            .password(user.getPassword())
            .authorities(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
            .build();
  }
}
