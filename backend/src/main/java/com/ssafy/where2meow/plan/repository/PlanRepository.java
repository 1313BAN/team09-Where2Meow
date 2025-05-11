package com.ssafy.where2meow.plan.repository;

import com.ssafy.where2meow.plan.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PlanRepository extends JpaRepository<Plan, Integer> {

    // 사용자 ID로 여행 계획(공개 + 비공개) 조회
    List<Plan> findByUserId(int userId);

    // 공개된 모든 여행 계획 조회
    List<Plan> findByIsPublicTrue();
    
    // planId로 Plan 상세 정보 조회 (Plan 및 PlanAttraction 함께 조회)
    @Query("SELECT p FROM Plan p LEFT JOIN FETCH p.planAttractions WHERE p.planId = :planId")
    Plan findByIdWithAttractions(int planId);

    // 특정 여행 계획 삭제
    void deleteByPlanId(int planId);

}
