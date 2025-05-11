package com.ssafy.where2meow.plan.service;

import com.ssafy.where2meow.plan.entity.Plan;
import com.ssafy.where2meow.plan.entity.PlanLike;
import com.ssafy.where2meow.plan.repository.PlanLikeRepository;
import com.ssafy.where2meow.plan.repository.PlanRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlanLikeService {

    private final PlanRepository planRepository;
    private final PlanLikeRepository planLikeRepository;

    // 사용자가 여행 계획에 좋아요를 눌렀는지 확인
    public boolean hasUserLiked(int planId, int userId) {
        return planLikeRepository.existsByPlan_PlanIdAndUserId(planId, userId);
    }

    // 여행 계획 좋아요 추가
    @Transactional
    public void createLike(int planId, int userId) {
        if (!hasUserLiked(planId, userId)) {
            Plan plan = planRepository.findById(planId)
                    .orElseThrow(() -> new RuntimeException(planId + "에 해당하는 여행 계획이 없습니다."));
            PlanLike planLike = new PlanLike();
            planLike.setPlan(plan);
            planLike.setUserId(userId);
            planLikeRepository.save(planLike);
        }
    }

    // 여행 계획 좋아요 삭제
    @Transactional
    public void deleteLike(int planId, int userId) {
        planLikeRepository.deleteByPlan_PlanIdAndUserId(planId, userId);
    }

}
