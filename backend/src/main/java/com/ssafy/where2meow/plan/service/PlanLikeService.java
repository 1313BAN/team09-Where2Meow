package com.ssafy.where2meow.plan.service;

import com.ssafy.where2meow.common.util.UuidUserUtil;
import com.ssafy.where2meow.exception.EntityNotFoundException;
import com.ssafy.where2meow.plan.entity.Plan;
import com.ssafy.where2meow.plan.entity.PlanLike;
import com.ssafy.where2meow.plan.repository.PlanLikeRepository;
import com.ssafy.where2meow.plan.repository.PlanRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlanLikeService {

    private final PlanRepository planRepository;
    private final PlanLikeRepository planLikeRepository;

    private final UuidUserUtil uuidUserUtil;

    // 사용자가 여행 계획에 좋아요를 눌렀는지 확인
    public boolean hasUserLiked(int planId, int userId) {
        return planLikeRepository.existsByPlanIdAndUserId(planId, userId);
    }

    // 여행 계획 좋아요 추가
    @Transactional
    public void createLike(int planId, int userId) {
        if (!hasUserLiked(planId, userId)) {
            Plan plan = planRepository.findById(planId)
                    .orElseThrow(() -> new EntityNotFoundException("Plan", "planId", planId));
            PlanLike planLike = new PlanLike();
            planLike.setPlanId(planId);
            planLike.setUserId(userId);
            planLikeRepository.save(planLike);
        }
    }

    // uuid를 기반으로 여행 계획 좋아요 추가
    @Transactional
    public void createLikeByUuid(int planId, UUID uuid) {
        Integer userId = uuidUserUtil.getRequiredUserId(uuid);
        createLike(planId, userId);
    }

    // 여행 계획 좋아요 삭제
    @Transactional
    public void deleteLike(int planId, int userId) {
        planLikeRepository.deleteByPlanIdAndUserId(planId, userId);
    }

    // uuid를 기반으로 여행 계획 좋아요 삭제
    @Transactional
    public void deleteLikeByUuid(int planId, UUID uuid) {
        Integer userId = uuidUserUtil.getRequiredUserId(uuid);
        deleteLike(planId, userId);
    }

}
