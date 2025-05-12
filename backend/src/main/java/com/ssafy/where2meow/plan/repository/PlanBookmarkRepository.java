package com.ssafy.where2meow.plan.repository;

import com.ssafy.where2meow.plan.entity.PlanBookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanBookmarkRepository extends JpaRepository<PlanBookmark, Integer> {

    // 사용자가 여행 계획에 북마크를 눌렀는지 확인
    boolean existsByPlanIdAndUserId(int planId, int userId);

    // 사용자가 누른 특정 여행 계획 북마크 삭제
    @Modifying
    void deleteByPlanIdAndUserId(int planId, int userId);

    // 특정 여행 계획의 북마크 수 카운트
    int countByPlanId(int planId);

    // 특정 여행 계획에 속한 북마크 삭제
    @Modifying
    @Query("DELETE FROM PlanBookmark pb WHERE pb.planId = :planId")
    void deleteByPlanId(int planId);

}
