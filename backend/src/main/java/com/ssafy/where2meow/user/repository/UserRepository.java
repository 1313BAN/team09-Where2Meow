package com.ssafy.where2meow.user.repository;

import com.ssafy.where2meow.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    // is_active ture인 사용자만 조회
    // 이메일 기반
    Optional<User> findByEmailAndIsActiveTrue(String email);

    // uuid 기반
    Optional<User> findByUuidAndIsActiveTrue(UUID uuid);

    // 이름과 휴대폰 번호 기반
    Optional<User> findByNameAndPhoneAndIsActiveTrue(String name, String phone);

    // 이메일이 중복인지 조회
    boolean existsByEmailAndIsActiveTrue(String email);

    // 닉네임이 중복인지 조회
    boolean existsByNicknameAndIsActiveTrue(String nickname);
}
