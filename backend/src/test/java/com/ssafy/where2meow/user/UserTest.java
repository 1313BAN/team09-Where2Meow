package com.ssafy.where2meow.user;

import com.ssafy.where2meow.user.entity.Role;
import com.ssafy.where2meow.user.entity.User;
import com.ssafy.where2meow.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class UserTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder; // PasswordEncoder 타입으로 주입 받음

  /**
   * 더미 사용자 데이터를 생성하고 데이터베이스에 삽입합니다.
   * 관리자 1명과 일반 사용자 1명을 생성합니다.
   */
  @Test
  //@Transactional // 실제 데이터를 유지하려면 이 어노테이션을 제거하세요
  public void insertDummyUsers() {

    User admin = new User();
    admin.setName("admin");
    admin.setNickname("admin nickname");
    admin.setEmail("admin@admin.com");
    admin.setPassword(passwordEncoder.encode("123123123"));
    admin.setPhone("010-0000-0000");
    admin.setRole(Role.ADMIN);
    admin.setIsActive(true);

    User user = new User();
    user.setName("user");
    user.setNickname("nickname");
    user.setEmail("user@user.com");
    user.setPassword(passwordEncoder.encode("123123123"));
    user.setPhone("010-0000-0000");
    user.setRole(Role.USER);
    user.setIsActive(true);

    userRepository.save(admin);
    userRepository.save(user);
  }
}