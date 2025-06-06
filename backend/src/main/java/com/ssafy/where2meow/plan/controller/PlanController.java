package com.ssafy.where2meow.plan.controller;

import com.ssafy.where2meow.common.util.UuidUserUtil;
import com.ssafy.where2meow.plan.dto.PlanDetailResponse;
import com.ssafy.where2meow.plan.dto.PlanRequest;
import com.ssafy.where2meow.plan.dto.PlanResponse;
import com.ssafy.where2meow.plan.service.PlanBookmarkService;
import com.ssafy.where2meow.plan.service.PlanLikeService;
import com.ssafy.where2meow.plan.service.PlanService;
import com.ssafy.where2meow.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/plan")
@RequiredArgsConstructor
@Tag(name = "Plan", description = "여행 계획 관리 API")
public class PlanController {

  private final PlanService planService;
  private final PlanLikeService planLikeService;
  private final PlanBookmarkService planBookmarkService;
  private final UserService userService;

  private final UuidUserUtil uuidUserUtil;

  @GetMapping
  @Operation(
      summary = "여행 계획 리스트 조회",
      description = "여행 계획 리스트를 조회합니다. (로그인되어 있다면 본인이 생성한 private 여행 계획도 포함)"
  )
  @ApiResponse(responseCode = "200", description = "여행 계획 리스트 조회 성공")
  public ResponseEntity<List<PlanResponse>> getAllPlans() {
    // 인증 정보 추출
    UUID uuid = uuidUserUtil.getCurrentUserUuid();

    List<PlanResponse> plans = planService.getAllPlansByUuid(uuid);
    return ResponseEntity.ok(plans);
  }

  @GetMapping("/user")
  @Operation(
      summary = "여행 계획 리스트 조회 (사용자)",
      description = "사용자가 생성한 여행 계획 리스트를 조회합니다."
  )
  @ApiResponse(responseCode = "200", description = "여행 계획 리스트 조회 성공")
  public ResponseEntity<List<PlanResponse>> getUserPlans() {
    // 인증 정보 추출
    UUID uuid = uuidUserUtil.getCurrentUserUuid();

    List<PlanResponse> plans = planService.getUserPlansByUuid(uuid);
    return ResponseEntity.ok(plans);
  }

  @GetMapping("/{planId}")
  @Operation(
      summary = "여행 계획 상세 조회",
      description = "여행 계획의 상세 정보를 조회합니다."
  )
  @ApiResponse(responseCode = "200", description = "여행 계획 상세 조회 성공")
  @ApiResponse(responseCode = "404", description = "여행 계획을 찾을 수 없음")
  public ResponseEntity<PlanDetailResponse> getPlanDetail(@PathVariable int planId) {
    // 인증 정보 추출
    UUID uuid = uuidUserUtil.getCurrentUserUuid();

    PlanDetailResponse planDetail = planService.getPlanDetailByUuid(planId, uuid);
    return ResponseEntity.ok(planDetail);
  }

  @PostMapping
  @Operation(
      summary = "여행 계획 추가",
      description = "여행 계획을 추가합니다."
  )
  @ApiResponse(responseCode = "201", description = "여행 계획 추가 성공")
  public ResponseEntity<PlanResponse> createPlan(@RequestBody PlanRequest planRequest) {
    // 인증 정보 추출
    UUID uuid = uuidUserUtil.getCurrentUserUuid();

    PlanResponse createdPlan = planService.createPlanByUuid(planRequest, uuid);
    URI location = URI.create("/api/plan/" + createdPlan.getPlanId());
    return ResponseEntity.created(location).body(createdPlan);
  }

  @PutMapping("/{planId}")
  @Operation(
      summary = "여행 계획 수정",
      description = "여행 계획을 수정합니다."
  )
  @ApiResponse(responseCode = "200", description = "여행 계획 수정 성공")
  @ApiResponse(responseCode = "403", description = "여행 계획 수정 권한 없음")
  @ApiResponse(responseCode = "404", description = "여행 계획을 찾을 수 없음")
  public ResponseEntity<PlanResponse> updatePlan(
      @PathVariable int planId, @RequestBody PlanRequest planRequest) {
    // 인증 정보 추출
    UUID uuid = uuidUserUtil.getCurrentUserUuid();

    PlanResponse updatedPlan = planService.updatePlanByUuid(planId, planRequest, uuid);
    return ResponseEntity.ok(updatedPlan);
  }

  @DeleteMapping("/{planId}")
  @Operation(
      summary = "여행 계획 삭제",
      description = "여행 계획을 삭제합니다."
  )
  @ApiResponse(responseCode = "204", description = "여행 계획 삭제 성공")
  @ApiResponse(responseCode = "403", description = "여행 계획 삭제 권한 없음")
  @ApiResponse(responseCode = "404", description = "여행 계획을 찾을 수 없음")
  public ResponseEntity<Void> deletePlan(@PathVariable int planId) {
    // 인증된 사용자 확인
    UUID uuid = uuidUserUtil.getCurrentUserUuid();

    planService.deletePlanByUuid(planId, uuid);
    return ResponseEntity.noContent().build();
  }

  // 여행 계획 좋아요 추가
  @PostMapping("/{planId}/like")
  @Operation(
      summary = "여행 계획 좋아요 추가",
      description = "로그인한 사용자가 특정 여행 계획에 좋아요를 추가합니다."
  )
  @ApiResponse(responseCode = "204", description = "여행 계획 좋아요 추가 성공")
  public ResponseEntity<Void> createLike(@PathVariable int planId) {
    // 인증된 사용자 확인
    UUID uuid = uuidUserUtil.getCurrentUserUuid();

    planLikeService.createLikeByUuid(planId, uuid);
    return ResponseEntity.noContent().build();
  }

  // 여행 계획 좋아요 삭제
  @DeleteMapping("/{planId}/like")
  @Operation(
      summary = "여행 계획 좋아요 삭제",
      description = "로그인한 사용자가 특정 여행 계획의 좋아요를 삭제합니다."
  )
  @ApiResponse(responseCode = "204", description = "여행 계획 좋아요 삭제 성공")
  public ResponseEntity<Void> deleteLike(@PathVariable int planId) {
    // 인증된 사용자 확인
    UUID uuid = uuidUserUtil.getCurrentUserUuid();

    planLikeService.deleteLikeByUuid(planId, uuid);
    return ResponseEntity.noContent().build();
  }

  // 여행 계획 북마크 추가
  @PostMapping("/{planId}/bookmark")
  @Operation(
      summary = "여행 계획 북마크 추가",
      description = "로그인한 사용자가 특정 여행 계획에 북마크를 추가합니다."
  )
  @ApiResponse(responseCode = "204", description = "여행 계획 북마크 추가 성공")
  public ResponseEntity<Void> createBookmark(@PathVariable int planId) {
    // 인증된 사용자 확인
    UUID uuid = uuidUserUtil.getCurrentUserUuid();

    planBookmarkService.createBookmarkByUuid(planId, uuid);
    return ResponseEntity.noContent().build();
  }

  // 여행 계획 북마크 삭제
  @DeleteMapping("/{planId}/bookmark")
  @Operation(
      summary = "여행 계획 북마크 삭제",
      description = "로그인한 사용자가 특정 여행 계획의 북마크를 삭제합니다."
  )
  @ApiResponse(responseCode = "204", description = "여행 계획 북마크 삭제 성공")
  public ResponseEntity<Void> deleteBookmark(@PathVariable int planId) {
    // 인증된 사용자 확인
    UUID uuid = uuidUserUtil.getCurrentUserUuid();

    planBookmarkService.deleteBookmarkByUuid(planId, uuid);
    return ResponseEntity.noContent().build();
  }

  @Operation(
      summary = "북마크한 여행 계획 목록 조회",
      description = "로그인한 사용자가 북마크한 여행 계획 목록을 조회합니다."
  )
  @ApiResponse(responseCode = "200", description = "북마크한 여행 계획 목록 조회 성공")
  @GetMapping("/bookmarks")
  public ResponseEntity<List<PlanResponse>> getBookmarkedPlans() {
    UUID uuid = uuidUserUtil.getCurrentUserUuid();
    int userId = userService.getUserIdByUuid(uuid);
    List<PlanResponse> bookmarkedPlans = planService.getBookmarkedPlans(userId);
    return ResponseEntity.ok(bookmarkedPlans);
  }

}
