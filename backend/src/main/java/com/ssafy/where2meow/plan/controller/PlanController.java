package com.ssafy.where2meow.plan.controller;

import com.ssafy.where2meow.plan.dto.PlanReqeust;
import com.ssafy.where2meow.plan.dto.PlanResponse;
import com.ssafy.where2meow.plan.service.PlanLikeService;
import com.ssafy.where2meow.plan.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plan")
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;
    private final PlanLikeService planLikeService;

    @GetMapping
    public ResponseEntity<List<PlanResponse>> getAllPlans(@RequestParam(required = false) Integer userId) {
        List<PlanResponse> plans = planService.getAllPlans(userId);
        return ResponseEntity.ok(plans);
    }

    @PostMapping
    public ResponseEntity<PlanResponse> createPlan(@RequestBody PlanReqeust planRequest, @RequestParam int userId) {
        PlanResponse createdPlan = planService.createPlan(planRequest, userId);
        return ResponseEntity.ok(createdPlan);
    }

    @DeleteMapping("/{planId}/like")
    public ResponseEntity<Void> deleteLike(@PathVariable int planId, @RequestParam int userId) {
        planLikeService.removeLike(planId, userId);
        return ResponseEntity.noContent().build();
    }

}
