package com.ssafy.where2meow.plan.service;

import com.ssafy.where2meow.plan.dto.PlanDetailResponse;
import com.ssafy.where2meow.plan.dto.PlanRequest;
import com.ssafy.where2meow.plan.dto.PlanResponse;
import com.ssafy.where2meow.plan.entity.Plan;
import com.ssafy.where2meow.plan.entity.PlanAttraction;
import com.ssafy.where2meow.plan.repository.PlanAttractionRepository;
import com.ssafy.where2meow.plan.repository.PlanBookmarkRepository;
import com.ssafy.where2meow.plan.repository.PlanLikeRepository;
import com.ssafy.where2meow.plan.repository.PlanRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
            Set<Plan> planSet = new HashSet<>(planRepository.findByUserId(userId));
            planSet.addAll(planRepository.findByIsPublicTrue());
            plans = new ArrayList<>(planSet);
        } else {
            // 사용자 ID가 제공되지 않은 경우
            plans = planRepository.findByIsPublicTrue();
        }

        return plans.stream().map(plan -> convertToDto(plan, userId)).toList();
    }

    // 사용자의 여행 계획 목록 조회
    public List<PlanResponse> getUserPlans(int userId) {
        List<Plan> plans = planRepository.findByUserId(userId);
        return plans.stream().map(plan -> convertToDto(plan, userId)).toList();
    }

    // 특정 여행 계획 상세 조회
    @Transactional
    public PlanDetailResponse getPlanDetail(int planId, Integer userId) {
        // planId로 Plan과 관련 PlanAttraction들을 함께 조회
        Plan plan = planRepository.findByIdWithAttractions(planId);
        if (plan == null) {
            throw new RuntimeException(planId + "에 해당하는 여행 계획이 없습니다.");
        }

        // 조회수 증가
        plan.setViewCount(plan.getViewCount() + 1);
        planRepository.save(plan);

        // 관련 정보 조회
        int likeCount = planLikeRepository.countByPlan_PlanId(planId);

        boolean isLiked = false;
        boolean isBookmarked = false;

        if (userId != null) {
            isLiked = planLikeRepository.existsByPlan_PlanIdAndUserId(planId, userId);
            isBookmarked = planBookmarkRepository.existsByPlan_PlanIdAndUserId(planId, userId);
        }

        // PlanDetailResponse 생성 및 반환
        return PlanDetailResponse.fromPlan(plan, likeCount, isLiked, isBookmarked);
    }

    // 여행 계획 생성
    @Transactional
    public PlanResponse createPlan(PlanRequest planRequest, Integer userId) {
        Plan plan = new Plan();
        plan.setUserId(userId);
        plan.setTitle(planRequest.getTitle());
        plan.setContent(planRequest.getContent());

        if (planRequest.getStartDate() != null) {
            plan.setStartDate(planRequest.getStartDate());
        }
        if (planRequest.getEndDate() != null) {
            plan.setEndDate(planRequest.getEndDate());
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

    // 여행 계획 수정
    @Transactional
    public PlanResponse updatePlan(int planId, PlanRequest planRequest, int userId) {
        // plan이 존재하는지 확인
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException(planId + "에 해당하는 여행 계획이 없습니다."));
        
        // plan 기본 정보 업데이트
        plan.setTitle(planRequest.getTitle());
        plan.setContent(planRequest.getContent());
        
        if (planRequest.getStartDate() != null) {
            plan.setStartDate(planRequest.getStartDate());
        }
        if (planRequest.getEndDate() != null) {
            plan.setEndDate(planRequest.getEndDate());
        }
        
        plan.setPublic(planRequest.isPublic());
        plan.setUpdatedAt(LocalDateTime.now());

        Plan savedPlan = planRepository.save(plan);
        
        // 기존 관광지 정보 삭제
        planAttractionRepository.deleteByPlanId(planId);
        
        // 새로운 관광지 정보 추가
        if (planRequest.getAttractions() != null && !planRequest.getAttractions().isEmpty()) {
            List<PlanAttraction> newAttractions = planRequest.getAttractions().stream()
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
            
            planAttractionRepository.saveAll(newAttractions);
        }
        
        // 업데이트된 Plan 정보 반환
        return convertToDto(savedPlan, userId);
    }

    // 여행 계획 삭제
    @Transactional
    public void deletePlan(int planId) {
        planRepository.deleteByPlanId(planId);
    }

    // Entity -> DTO 변환
    private PlanResponse convertToDto(Plan plan, Integer userId) {
        int likeCount = planLikeRepository.countByPlan_PlanId(plan.getPlanId());

        boolean isLiked = false;
        boolean isBookmarked = false;

        if (userId != null) {
            isLiked = planLikeRepository.existsByPlan_PlanIdAndUserId(plan.getPlanId(), userId);
            isBookmarked = planBookmarkRepository.existsByPlan_PlanIdAndUserId(plan.getPlanId(), userId);
        }

        return PlanResponse.fromPlan(plan, likeCount, isLiked, isBookmarked);
    }
}
