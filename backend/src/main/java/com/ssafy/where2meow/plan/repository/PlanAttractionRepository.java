package com.ssafy.where2meow.plan.repository;

import com.ssafy.where2meow.plan.entity.PlanAttraction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanAttractionRepository extends JpaRepository<PlanAttraction, Integer> {
}
