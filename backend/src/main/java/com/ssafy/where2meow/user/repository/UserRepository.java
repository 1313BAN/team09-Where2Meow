package com.ssafy.where2meow.user.repository;

import com.ssafy.where2meow.user.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Mapper
@Repository
public interface UserRepository {
    Optional<User> findByEmail(String email);
    Optional<User> findById(int userId);
    
    // 더미 데이터 추가를 위한 메서드
    int insertUser(User user);
    int deleteAllUsers();
    List<User> findAllUsers();
    int countUsers();
}
