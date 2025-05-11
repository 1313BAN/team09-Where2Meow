package com.ssafy.where2meow.plan.service;

import com.ssafy.where2meow.plan.entity.PlanLike;
import com.ssafy.where2meow.plan.repository.PlanLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PlanLikeService {

    private final PlanLikeRepository planLikeRepository;

    // 사용자가 여행 계획에 좋아요를 눌렀는지 확인
    public boolean hasUserLiekd(int planId, int userId) {
        return planLikeRepository.existsByPlanIdAndUserId(planId, userId);
    }

    // 여행 계획 좋아요 추가
    public void addLike(int planId, int userId) {
        if (!hasUserLiekd(planId, userId)) {
            PlanLike planLike = new PlanLike();
            planLike.setPlanId(planId);
            planLike.setUserId(userId);
            planLikeRepository.save(planLike);
        }
    }

    // 여행 계획 좋아요 삭제
    public void removeLike(int planId, int userId) {
        planLikeRepository.deleteByPlanIdAndUserId(planId, userId);
    }

}
