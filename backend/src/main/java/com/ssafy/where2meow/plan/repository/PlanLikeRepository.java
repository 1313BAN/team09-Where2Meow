package com.ssafy.where2meow.plan.repository;

import com.ssafy.where2meow.plan.entity.PlanLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanLikeRepository extends JpaRepository<PlanLike, Integer> {

    // 사용자가 여행 계획에 좋아요를 눌렀는지 확인
    boolean existsByPlan_PlanIdAndUserId(int planId, int userId);

    // 사용자가 누른 특정 여행 계획 좋아요 삭제
    @Modifying
    void deleteByPlan_PlanIdAndUserId(int planId, int userId);

    // 특정 여행 계획의 좋아요 수 카운트
    int countByPlan_PlanId(int planId);

}