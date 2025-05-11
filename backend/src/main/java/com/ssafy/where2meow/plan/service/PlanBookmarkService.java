package com.ssafy.where2meow.plan.service;

import com.ssafy.where2meow.plan.entity.PlanBookmark;
import com.ssafy.where2meow.plan.entity.PlanLike;
import com.ssafy.where2meow.plan.repository.PlanBookmarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlanBookmarkService {

    private final PlanBookmarkRepository planBookmarkRepository;

    // 사용자가 여행 계획에 북마크를 눌렀는지 확인
    public boolean hasUserBookmared(int planId, int userId) {
        return planBookmarkRepository.existsByPlanIdAndUserId(planId, userId);
    }

    // 여행 계획 북마크 추가
    public void addLike(int planId, int userId) {
        if (!hasUserBookmared(planId, userId)) {
            PlanBookmark planBookmark = new PlanBookmark();
            planBookmark.setPlanId(planId);
            planBookmark.setUserId(userId);
            planBookmarkRepository.save(planBookmark);
        }
    }

    // 여행 계획 북마크 취소
    public void removeBookmark(int planId, int userId) {
        planBookmarkRepository.deletePlanBookmarkByPlanIdAndUserId(planId, userId);
    }

}
