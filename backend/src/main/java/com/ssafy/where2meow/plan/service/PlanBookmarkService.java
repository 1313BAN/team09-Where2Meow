package com.ssafy.where2meow.plan.service;

import com.ssafy.where2meow.plan.entity.Plan;
import com.ssafy.where2meow.plan.entity.PlanBookmark;
import com.ssafy.where2meow.plan.repository.PlanBookmarkRepository;
import com.ssafy.where2meow.plan.repository.PlanRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlanBookmarkService {

    private final PlanRepository planRepository;
    private final PlanBookmarkRepository planBookmarkRepository;

    // 사용자가 여행 계획에 북마크를 눌렀는지 확인
    public boolean hasUserBookmarked(int planId, int userId) {
        return planBookmarkRepository.existsByPlanIdAndUserId(planId, userId);
    }

    // 여행 계획 북마크 추가
    @Transactional
    public void createBookmark(int planId, int userId) {
        if (!hasUserBookmarked(planId, userId)) {
            Plan plan = planRepository.findById(planId)
                    .orElseThrow(() -> new RuntimeException(planId + "에 해당하는 여행 계획이 없습니다."));
            PlanBookmark planBookmark = new PlanBookmark();
            planBookmark.setPlanId(planId);
            planBookmark.setUserId(userId);
            planBookmarkRepository.save(planBookmark);
        }
    }

    // 여행 계획 북마크 취소
    @Transactional
    public void deleteBookmark(int planId, int userId) {
        planBookmarkRepository.deleteByPlanIdAndUserId(planId, userId);
    }

}
