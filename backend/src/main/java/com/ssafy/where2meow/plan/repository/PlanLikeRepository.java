package com.ssafy.where2meow.plan.repository;

import com.ssafy.where2meow.plan.entity.PlanLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface PlanLikeRepository extends JpaRepository<PlanLike, Integer> {

    // 사용자가 여행 계획에 좋아요를 눌렀는지 확인
    boolean existsByPlanIdAndUserId(int planId, int userId);

    // 사용자가 누른 특정 여행 계획 좋아요 삭제
    @Modifying
    void deleteByPlanIdAndUserId(int planId, int userId);

    // 특정 여행 계획의 좋아요 수 카운트
    int countByPlanId(int planId);

    // 여러 여행 계획에 대한 좋아요 수 한 번에 조회
    @Query("SELECT pl.planId, COUNT(*) FROM PlanLike pl WHERE pl.planId IN :planIds GROUP BY pl.planId")
    Map<Integer, Integer> countByPlanIdIn(List<Integer> planIds);

    // 특정 사용자의 여러 Plan에 대한 좋아요 여부 조회
    List<PlanLike> findByPlanIdInAndUserId(List<Integer> planIds, int userId);

    // 특정 여행 계획에 속한 좋아요 삭제
    @Modifying
    @Query("DELETE FROM PlanLike pl WHERE pl.planId = :planId")
    void deleteByPlanId(int planId);

}