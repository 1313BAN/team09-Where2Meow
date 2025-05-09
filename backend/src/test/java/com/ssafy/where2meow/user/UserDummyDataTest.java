package com.ssafy.where2meow.user;

import com.ssafy.where2meow.user.entity.Role;
import com.ssafy.where2meow.user.entity.User;
import com.ssafy.where2meow.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

@SpringBootTest
@ActiveProfiles("test")
public class UserDummyDataTest {

    @Autowired
    private UserRepository userRepository;
    
    private BCryptPasswordEncoder passwordEncoder;
    
    @BeforeEach
    public void setup() {
        passwordEncoder = new BCryptPasswordEncoder();
    }
    
    /**
     * 더미 사용자 데이터를 생성하고 데이터베이스에 삽입합니다.
     * 관리자 1명과 일반 사용자 10명을 생성합니다.
     */
    @Test
    //@Transactional // 실제 데이터를 유지하려면 이 어노테이션을 제거하세요
    public void insertDummyUsers() {
        User admin = new User();
        admin.setUuid(UUID.randomUUID());
        admin.setName("admin");
        admin.setNickname("admin nickname");
        admin.setEmail("admin@admin.com");
        admin.setPassword(passwordEncoder.encode("admin"));
        admin.setPhone("010-0000-0000");
        admin.setRole(Role.ADMIN);

        User user = new User();
        user.setUuid(UUID.randomUUID());
        user.setName("user");
        user.setNickname("nickname");
        user.setEmail("user@user.com");
        user.setPassword(passwordEncoder.encode("user"));
        user.setPhone("010-0000-0000");
        user.setRole(Role.USER);

        userRepository.insertUser(admin);
        userRepository.insertUser(user);
    }
}