package com.ssafy.where2meow.plan.service;

import com.ssafy.where2meow.exception.EntityNotFoundException;
import com.ssafy.where2meow.exception.ForbiddenAccessException;
import com.ssafy.where2meow.plan.dto.PlanDetailResponse;
import com.ssafy.where2meow.plan.dto.PlanRequest;
import com.ssafy.where2meow.plan.dto.PlanResponse;
import com.ssafy.where2meow.plan.entity.Plan;
import com.ssafy.where2meow.plan.entity.PlanAttraction;
import com.ssafy.where2meow.plan.entity.PlanBookmark;
import com.ssafy.where2meow.plan.entity.PlanLike;
import com.ssafy.where2meow.plan.repository.PlanAttractionRepository;
import com.ssafy.where2meow.plan.repository.PlanBookmarkRepository;
import com.ssafy.where2meow.plan.repository.PlanLikeRepository;
import com.ssafy.where2meow.plan.repository.PlanRepository;
import com.ssafy.where2meow.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import com.ssafy.where2meow.user.entity.User;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlanService {

    private final PlanRepository planRepository;
    private final PlanLikeRepository planLikeRepository;
    private final PlanBookmarkRepository planBookmarkRepository;
    private final PlanAttractionRepository planAttractionRepository;
    private final UserRepository userRepository;

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

        return convertToDtoList(plans, userId);
    }

    // uuid를 기반으로 여행 계획 목록 조회
    public List<PlanResponse> getAllPlansByUuid(UUID uuid) {
        Integer userId = null;
        
        if (uuid != null) {
            User user = userRepository.findByUuidAndIsActiveTrue(uuid).orElse(null);
            if (user != null) {
                userId = user.getUserId();
            }
        }

        return getAllPlans(userId);
    }

    // 사용자의 여행 계획 목록 조회
    public List<PlanResponse> getUserPlans(int userId) {
        List<Plan> plans = planRepository.findByUserId(userId);
        return convertToDtoList(plans, userId);
    }

    // uuid를 기반으로 사용자의 여행 계획 목록 조회
    public List<PlanResponse> getUserPlansByUuid(UUID uuid) {
        Integer userId = null;

        if (uuid != null) {
            User user = userRepository.findByUuidAndIsActiveTrue(uuid).orElse(null);
            if (user != null) {
                userId = user.getUserId();
            }
        }

        return getUserPlans(userId);
    }

    // 특정 여행 계획 상세 조회
    @Transactional
    public PlanDetailResponse getPlanDetail(int planId, Integer userId) {
        // planId로 Plan과 관련 PlanAttraction들을 함께 조회
        Plan plan = planRepository.findById(planId).orElseThrow(() -> new EntityNotFoundException("Plan", "planId", planId));

        // 조회수 증가
        planRepository.increaseViewCount(planId);
        plan.setViewCount(plan.getViewCount() + 1);

        // 관련 관광지 정보 조회
        List<PlanAttraction> planAttractions = planAttractionRepository.findByPlanId(planId);

        // 관련 정보 조회
        int likeCount = planLikeRepository.countByPlanId(planId);

        boolean isLiked = false;
        boolean isBookmarked = false;

        if (userId != null) {
            isLiked = planLikeRepository.existsByPlanIdAndUserId(planId, userId);
            isBookmarked = planBookmarkRepository.existsByPlanIdAndUserId(planId, userId);
        }

        // PlanDetailResponse 생성 및 반환
        return PlanDetailResponse.fromPlan(plan, planAttractions, likeCount, isLiked, isBookmarked);
    }

    // uuid를 기반으로 특정 여행 계획 상세 조회
    @Transactional
    public PlanDetailResponse getPlanDetailByUuid(int planId, UUID uuid) {
        Integer userId = null;

        if (uuid != null) {
            User user = userRepository.findByUuidAndIsActiveTrue(uuid).orElse(null);
            if (user != null) {
                userId = user.getUserId();
            }
        }

        return getPlanDetail(planId, userId);
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
                    attraction.setPlanId(savedPlan.getPlanId());
                    attraction.setAttractionId(attractionRequest.getAttractionId());
                    attraction.setContent(attractionRequest.getContent());
                    attraction.setDate(attractionRequest.getDate());
                    attraction.setAttractionOrder(attractionRequest.getAttractionOrder());
                    return attraction;
                })
                .toList();
            
            planAttractionRepository.saveAll(attractions);
        }

        return convertToDto(savedPlan, userId);
    }

    // 여행 계획 수정
    @Transactional
    public PlanResponse updatePlan(int planId, PlanRequest planRequest, int userId) {
        // plan이 존재하는지 확인
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new EntityNotFoundException("Plan", "planId", planId));

        // plan이 작성자가 맞는지 확인
        if (plan.getUserId() != userId) {
            throw new ForbiddenAccessException("이 여행 계획을 수정할 권한이 없습니다.");
        }
        
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
                    attraction.setPlanId(savedPlan.getPlanId());
                    attraction.setAttractionId(attractionRequest.getAttractionId());
                    attraction.setContent(attractionRequest.getContent());
                    attraction.setDate(attractionRequest.getDate());
                    attraction.setAttractionOrder(attractionRequest.getAttractionOrder());
                    return attraction;
                })
                .toList();
            
            planAttractionRepository.saveAll(newAttractions);
        }
        
        // 업데이트된 Plan 정보 반환
        return convertToDto(savedPlan, userId);
    }

    // 여행 계획 삭제
    @Transactional
    public void deletePlan(int planId) {
        planLikeRepository.deleteByPlanId(planId);
        planBookmarkRepository.deleteByPlanId(planId);
        planAttractionRepository.deleteByPlanId(planId);
        planRepository.deleteByPlanId(planId);
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

    private List<PlanResponse> convertToDtoList(List<Plan> plans, Integer userId) {
        if(plans == null || plans.isEmpty()) {
            return new ArrayList<>();
        }

        // planId 리스트 생성
        List<Integer> planIds = plans.stream()
                .map(Plan::getPlanId)
                .toList();

        // 좋아요 수 조회
        Map<Integer, Integer> likeCounts = planLikeRepository.countByPlanIdIn(planIds);

        // 사용자별 좋아요/북마크 상태 set 초기화
        Set<Integer> likedPlanIds;
        Set<Integer> bookmarkedPlanIds;

        if(userId != null) {
            likedPlanIds = planLikeRepository.findByPlanIdInAndUserId(planIds, userId).stream().
                    map(PlanLike::getPlanId)
                    .collect(Collectors.toSet());
            bookmarkedPlanIds = planBookmarkRepository.findByPlanIdInAndUserId(planIds, userId).stream().
                    map(PlanBookmark::getPlanId)
                    .collect(Collectors.toSet());
        } else {
            likedPlanIds = new HashSet<>();
            bookmarkedPlanIds = new HashSet<>();
        }

        return plans.stream().map(plan -> {
            int planId = plan.getPlanId();
            int likeCount = likeCounts.getOrDefault(planId, 0);
            boolean isLiked = likedPlanIds.contains(planId);
            boolean isBookmarked = bookmarkedPlanIds.contains(planId);

            return PlanResponse.fromPlan(plan, likeCount, isLiked, isBookmarked);
        }).toList();
    }

}
