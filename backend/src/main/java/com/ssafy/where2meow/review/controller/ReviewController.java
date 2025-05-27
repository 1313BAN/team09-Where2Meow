package com.ssafy.where2meow.review.controller;

import com.ssafy.where2meow.review.dto.*;
import com.ssafy.where2meow.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Review", description = "리뷰 관련 API")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/user")
    public ResponseEntity<List<UserReviewResponse>> getUserReviews(@RequestParam UUID uuid) {
        List<UserReviewResponse> reviewList = reviewService.getUserReviews(uuid);
        return ResponseEntity.ok(reviewList);
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewDetailResponse> getReviewDetail(@PathVariable Integer reviewId, @RequestParam UUID uuid) {
        ReviewDetailResponse review = reviewService.getReviewDetail(reviewId, uuid);
        return ResponseEntity.ok(review);
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewDetailResponse> updateReview(@PathVariable Integer reviewId, @Valid @RequestBody ReviewUpdateRequest reviewUpdateRequest) {
        ReviewDetailResponse updatedReview = reviewService.updateReview(reviewId, reviewUpdateRequest);
        return ResponseEntity.ok(updatedReview);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Integer reviewId, @RequestParam UUID uuid) {
        reviewService.deleteReview(reviewId, uuid);
        return ResponseEntity.ok().build();
    }

//    @GetMapping
//    public ResponseEntity<List<ReviewListResponse>> getAllReviewsByAttraction(@RequestParam Integer attractionId, @RequestBody UuidRequest uuid) {
//        List<ReviewListResponse> reviewList = reviewService.getAllReviewsByAttraction(attractionId, uuid.getUuid());
//        return ResponseEntity.ok(reviewList);
//    }

    @GetMapping
    public ResponseEntity<PageResponse<ReviewListResponse>> getReviewsByAttraction(
            @RequestParam Integer attractionId, 
            @RequestParam UUID uuid, 
            @PageableDefault(size = 10, sort = "createdAt", direction = Direction.DESC) Pageable pageable) {
        PageResponse<ReviewListResponse> pageResponse = reviewService.getReviewsByAttraction(attractionId, pageable, uuid);
        return ResponseEntity.ok(pageResponse);
    }

    @PostMapping
    public ResponseEntity<ReviewDetailResponse> createReview(@RequestBody @Valid ReviewCreateRequest request) {
        ReviewDetailResponse createdReview = reviewService.createReview(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReview);
    }

    @PostMapping("/like")
    public ResponseEntity<ReviewLikeResponse> toggleReviewLike(@RequestBody @Valid ReviewLikeRequest request) {
        ReviewLikeResponse response = reviewService.toggleReviewLike(request);
        return ResponseEntity.ok(response);
    }
}
