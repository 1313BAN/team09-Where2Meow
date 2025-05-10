package com.ssafy.where2meow.plan.repository;

import com.ssafy.where2meow.plan.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlanRepository extends JpaRepository<Plan, Integer> {

    // 사용자 ID로 여행 계획(공개 + 비공개) 조회
    List<Plan> findByUserId(int userId);

    // 공개된 모든 여행 계획 조회
    List<Plan> findByIsPublicTrue();

}
