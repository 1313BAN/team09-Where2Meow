package com.ssafy.where2meow.plan.controller;

import com.ssafy.where2meow.plan.dto.PlanDetailResponse;
import com.ssafy.where2meow.plan.dto.PlanRequest;
import com.ssafy.where2meow.plan.dto.PlanResponse;
import com.ssafy.where2meow.plan.service.PlanBookmarkService;
import com.ssafy.where2meow.plan.service.PlanLikeService;
import com.ssafy.where2meow.plan.service.PlanService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plan")
@RequiredArgsConstructor
@Tag(name = "Plan", description = "여행 계획 관리 API")
public class PlanController {

    private final PlanService planService;
    private final PlanLikeService planLikeService;
    private final PlanBookmarkService planBookmarkService;

    // 여행 계획 리스트 조회
    @GetMapping
    public ResponseEntity<List<PlanResponse>> getAllPlans(@RequestParam(required = false) Integer userId) {
        List<PlanResponse> plans = planService.getAllPlans(userId);
        return ResponseEntity.ok(plans);
    }

    // 여행 계획 리스트 조회(사용자)
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PlanResponse>> getUserPlans(@PathVariable int userId) {
        List<PlanResponse> plans = planService.getUserPlans(userId);
        return ResponseEntity.ok(plans);
    }

    // 여행 계획 상세 조회
    @GetMapping("/{planId}")
    public ResponseEntity<PlanDetailResponse> getPlanDetail(
            @PathVariable int planId,
            @RequestParam(required = false) Integer userId) {
        PlanDetailResponse planDetail = planService.getPlanDetail(planId, userId);
        return ResponseEntity.ok(planDetail);
    }

    // 여행 계획 추가
    @PostMapping
    public ResponseEntity<PlanResponse> createPlan(@RequestBody PlanRequest planRequest, @RequestParam int userId) {
        PlanResponse createdPlan = planService.createPlan(planRequest, userId);
        return ResponseEntity.ok(createdPlan);
    }

    // 여행 계획 수정
    @PutMapping("/{planId}")
    public ResponseEntity<PlanResponse> updatePlan(
            @PathVariable int planId,
            @RequestParam int userId,
            @RequestBody PlanRequest planRequest) {
        PlanResponse updatedPlan = planService.updatePlan(planId, planRequest, userId);
        return ResponseEntity.ok(updatedPlan);
    }

    // 여행 계획 삭제
    @DeleteMapping("/{planId}")
    public ResponseEntity<Void> deletePlan(@PathVariable int planId) {
        planService.deletePlan(planId);
        return ResponseEntity.noContent().build();
    }

    // 여행 계획 좋아요 추가
    @PostMapping("/{planId}/like")
    public ResponseEntity<Void> createLike(@PathVariable int planId, @RequestParam int userId) {
        planLikeService.createLike(planId, userId);
        return ResponseEntity.noContent().build();
    }

    // 여행 계획 좋아요 삭제
    @DeleteMapping("/{planId}/like")
    public ResponseEntity<Void> deleteLike(@PathVariable int planId, @RequestParam int userId) {
        planLikeService.deleteLike(planId, userId);
        return ResponseEntity.noContent().build();
    }

    // 여행 계획 북마크 추가
    @PostMapping("/{planId}/bookmark")
    public ResponseEntity<Void> createBookmark(@PathVariable int planId, @RequestParam int userId) {
        planBookmarkService.createBookmark(planId, userId);
        return ResponseEntity.noContent().build();
    }

    // 여행 계획 북마크 삭제
    @DeleteMapping("/{planId}/bookmark")
    public ResponseEntity<Void> deleteBookmark(@PathVariable int planId, @RequestParam int userId) {
        planBookmarkService.deleteBookmark(planId, userId);
        return ResponseEntity.noContent().build();
    }

}
