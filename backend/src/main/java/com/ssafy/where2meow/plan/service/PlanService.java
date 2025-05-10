package com.ssafy.where2meow.plan.service;

import com.ssafy.where2meow.plan.dto.PlanReqeust;
import com.ssafy.where2meow.plan.dto.PlanResponse;
import com.ssafy.where2meow.plan.entity.Plan;
import com.ssafy.where2meow.plan.entity.PlanAttraction;
import com.ssafy.where2meow.plan.repository.PlanAttractionRepository;
import com.ssafy.where2meow.plan.repository.PlanBookmarkRepository;
import com.ssafy.where2meow.plan.repository.PlanLikeRepository;
import com.ssafy.where2meow.plan.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlanService {

    private final PlanRepository planRepository;
    private final PlanLikeRepository planLikeRepository;
    private final PlanBookmarkRepository planBookmarkRepository;
    private final PlanAttractionRepository planAttractionRepository;

    // 모든 여행 계획 목록 조회
    // 사용자 ID가 제공된 경우 : 본인 여행 계획 전부 + 다른 사용자의 공개 여행 계획 조회
    // 사용자 ID가 제공되지 않은 경우 : 공개된 여행 계획 조회
    public List<PlanResponse> getAllPlans(Integer userId) {
        List<Plan> plans;

        if (userId != null) {
            // 사용자 ID가 제공된 경우
            plans = planRepository.findByUserId(userId);
            List<Plan> publicPlans = planRepository.findByIsPublicTrue();

            for(Plan plan : publicPlans) {
                if (!plans.contains(plan)) {
                    plans.add(plan);
                }
            }
        } else {
            // 사용자 ID가 제공되지 않은 경우
            plans = planRepository.findByIsPublicTrue();
        }

        return plans.stream().map(plan -> convertToDto(plan, userId)).toList();
    }

    // 여행 계획 생성
    public PlanResponse createPlan(PlanReqeust planRequest, Integer userId) {
        Plan plan = new Plan();
        plan.setUserId(userId);
        plan.setTitle(planRequest.getTitle());
        plan.setContent(planRequest.getContent());

        if (planRequest.getStartDate() != null) {
            plan.setStartDate(LocalDate.parse(planRequest.getStartDate()));
        }
        if (planRequest.getEndDate() != null) {
            plan.setEndDate(LocalDate.parse(planRequest.getEndDate()));
        }
        
        plan.setPublic(planRequest.isPublic());
        plan.setUpdatedAt(LocalDateTime.now());

        Plan savedPlan = planRepository.save(plan);

        if (planRequest.getAttractions() != null && !planRequest.getAttractions().isEmpty()) {
            List<PlanAttraction> attractions = planRequest.getAttractions().stream()
                .map(attractionRequest -> {
                    PlanAttraction attraction = new PlanAttraction();
                    attraction.setPlan(savedPlan);
                    attraction.setAttractionId(attractionRequest.getAttractionId());
                    attraction.setContent(attractionRequest.getContent());
                    attraction.setDate(attractionRequest.getDate());
                    attraction.setAttractionOrder(attractionRequest.getAttractionOrder());
                    return attraction;
                })
                .collect(Collectors.toList());
            
            planAttractionRepository.saveAll(attractions);
        }

        return convertToDto(savedPlan, userId);
    }

    // Entity -> DTO 변환
    private PlanResponse convertToDto(Plan plan, Integer userId) {
        int likeCount = planLikeRepository.countByPlanId(plan.getPlanId());

        boolean isLiked = false;
        boolean isBookmarked = false;

        if (userId != null) {
            isLiked = planLikeRepository.existsByPlanIdAndUserId(plan.getPlanId(), userId);
            isBookmarked = planBookmarkRepository.existsByPlanIdAndUserId(plan.getPlanId(), userId);
        }

        return PlanResponse.fromPlan(plan, likeCount, isLiked, isBookmarked);
    }

}
