package com.ssafy.where2meow.plan.controller;

import com.ssafy.where2meow.plan.dto.PlanDetailResponse;
import com.ssafy.where2meow.plan.dto.PlanRequest;
import com.ssafy.where2meow.plan.dto.PlanResponse;
import com.ssafy.where2meow.plan.service.PlanBookmarkService;
import com.ssafy.where2meow.plan.service.PlanLikeService;
import com.ssafy.where2meow.plan.service.PlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/plan")
@RequiredArgsConstructor
@Tag(name = "Plan", description = "여행 계획 관리 API")
public class PlanController {

    private final PlanService planService;
    private final PlanLikeService planLikeService;
    private final PlanBookmarkService planBookmarkService;

    @GetMapping
    @Operation(
            summary = "여행 계획 리스트 조회",
            description = "여행 계획 리스트를 조회합니다. (로그인되어 있다면 본인이 생성한 private 여행 계획도 포함)"
    )
    @ApiResponse(responseCode = "200", description = "여행 계획 리스트 조회 성공")
    public ResponseEntity<List<PlanResponse>> getAllPlans() {
        // 인증 정보 추출
        UUID uuid = getCurrentUserUuid();

        List<PlanResponse> plans = planService.getAllPlansByUuid(uuid);
        return ResponseEntity.ok(plans);
    }

    // 여행 계획 리스트 조회(사용자)
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PlanResponse>> getUserPlans(@PathVariable int userId) {
        List<PlanResponse> plans = planService.getUserPlans(userId);
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/{planId}")
    @Operation(
            summary = "여행 계획 상세 조회",
            description = "여행 계획의 상세 정보를 조회합니다."
    )
    @ApiResponse(responseCode = "200", description = "여행 계획 상세 조회 성공")
    public ResponseEntity<PlanDetailResponse> getPlanDetail(@PathVariable int planId) {
        // 인증 정보 추출
        UUID uuid = getCurrentUserUuid();

        PlanDetailResponse planDetail = planService.getPlanDetailByUuid(planId, uuid);
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

    // 인증 정보에서 UUID 추출
    private UUID getCurrentUserUuid() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && !auth.getPrincipal().equals("anonymousUser")) {
            try {
                String uuidString = auth.getName();
                return UUID.fromString(uuidString);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
        return null;
    }

}
