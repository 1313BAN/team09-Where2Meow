package com.ssafy.where2meow.plan.repository;

import com.ssafy.where2meow.plan.entity.PlanBookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanBookmarkRepository extends JpaRepository<PlanBookmark, Integer> {
}
