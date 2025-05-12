package com.ssafy.where2meow.plan.repository;

import com.ssafy.where2meow.plan.entity.PlanAttraction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanAttractionRepository extends JpaRepository<PlanAttraction, Integer> {
    
    // 특정 여행 계획에 속한 관광지 정보 삭제
    @Modifying
    @Query("DELETE FROM PlanAttraction pa WHERE pa.planId = :planId")
    void deleteByPlanId(int planId);

    // 특정 여행 계획에 속한 관광지 정보 조회
    List<PlanAttraction> findByPlanId(int planId);

}
