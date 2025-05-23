package com.ssafy.where2meow.plan.repository;

import com.ssafy.where2meow.plan.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PlanRepository extends JpaRepository<Plan, Integer> {

    // 사용자 ID로 여행 계획(공개 + 비공개) 조회
    List<Plan> findByUserId(int userId);

    // 공개된 모든 여행 계획 조회
    List<Plan> findByIsPublicTrue();

    // 특정 여행 계획 삭제
    @Modifying
    void deleteByPlanId(int planId);

    // 조회수 증가
    @Modifying
    @Query("UPDATE Plan p SET p.viewCount = p.viewCount + 1 WHERE p.planId = :planId")
    void increaseViewCount(int planId);

}
