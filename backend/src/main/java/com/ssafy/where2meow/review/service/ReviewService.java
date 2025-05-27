package com.ssafy.where2meow.review.service;

import com.ssafy.where2meow.attraction.entity.Attraction;
import com.ssafy.where2meow.attraction.repository.AttractionRepository;
import com.ssafy.where2meow.common.util.UuidUserUtil;
import com.ssafy.where2meow.exception.EntityNotFoundException;
import com.ssafy.where2meow.exception.ForbiddenAccessException;
import com.ssafy.where2meow.review.dto.*;
import com.ssafy.where2meow.review.entity.Review;
import com.ssafy.where2meow.review.entity.ReviewLike;
import com.ssafy.where2meow.review.repository.ReviewLikeRepository;
import com.ssafy.where2meow.review.repository.ReviewRepository;
import com.ssafy.where2meow.user.entity.User;
import com.ssafy.where2meow.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReviewService {
    
    private final ReviewRepository reviewRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final UserRepository userRepository;
    private final AttractionRepository attractionRepository;
    private final UuidUserUtil uuidUserUtil;
    
    /**
     * 리뷰 작성
     */
    public ReviewDetailResponse createReview(ReviewCreateRequest reviewCreateRequest) {
        User user = getCurrentUser(reviewCreateRequest.getUuid());
        
        // 관광지 존재 여부 확인
        Attraction attraction = attractionRepository.findById(reviewCreateRequest.getAttractionId())
                .orElseThrow(() -> new EntityNotFoundException("해당 관광지를 찾을 수 없습니다."));
        
        Review review = Review.builder()
                .content(reviewCreateRequest.getContent())
                .score(reviewCreateRequest.getScore())
                .attractionId(reviewCreateRequest.getAttractionId())
                .userId(user.getUserId())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        Review savedReview = reviewRepository.save(review);
        
        return ReviewDetailResponse.fromEntity(
                savedReview,
                attraction.getAttractionName(),
                user.getNickname(),
                user.getImage(),
                0,
                false
        );
    }
    
    /**
     * 리뷰 상세 조회
     */
    @Transactional(readOnly = true)
    public ReviewDetailResponse getReviewDetail(Integer reviewId, UUID uuid) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("해당 리뷰를 찾을 수 없습니다."));
        
        User user = userRepository.findById(review.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("작성자 정보를 찾을 수 없습니다."));
        
        Attraction attraction = attractionRepository.findById(review.getAttractionId())
                .orElseThrow(() -> new EntityNotFoundException("해당 관광지를 찾을 수 없습니다."));
        
        // 좋아요 수 조회
        Integer likeCount = reviewLikeRepository.countByReviewId(reviewId);
        
        // 현재 사용자가 좋아요를 눌렀는지 여부 확인
        Boolean isLiked = false;
        try {
            User currentUser = getCurrentUser(uuid);
            isLiked = reviewLikeRepository.findByReviewAndUser(review, currentUser).isPresent();
        } catch (Exception e) {
            // 로그인하지 않은 경우 좋아요 여부는 false
            log.debug("사용자가 로그인하지 않았거나 정보를 찾을 수 없습니다.");
        }
        
        return ReviewDetailResponse.fromEntity(
                review,
                attraction.getAttractionName(),
                user.getNickname(),
                user.getImage(),
                likeCount,
                isLiked
        );
    }
    
    /**
     * 관광지별 리뷰 목록 조회
     */
    /**
     * 관광지별 페이징된 리뷰 목록 조회
     */
    @Transactional(readOnly = true)
    public PageResponse<ReviewListResponse> getReviewsByAttraction(Integer attractionId, Pageable pageable, UUID uuid) {
        // 관광지 존재 여부 확인
        attractionRepository.findById(attractionId)
                .orElseThrow(() -> new EntityNotFoundException("해당 관광지를 찾을 수 없습니다."));
        
        Page<Review> reviewPage = reviewRepository.findByAttractionId(attractionId, pageable);
        
        // 리뷰 ID 목록 추출
        List<Integer> reviewIds = reviewPage.getContent().stream()
                .map(Review::getReviewId)
                .collect(Collectors.toList());
        
        // 리뷰별 좋아요 수 조회
        Map<Integer, Integer> likeCountMap = new HashMap<>();
        reviewLikeRepository.countByReviewIdIn(reviewIds).forEach(
                count -> likeCountMap.put(count.getReviewId(), count.getCount())
        );
        
        // 사용자 정보 조회
        List<Integer> userIds = reviewPage.getContent().stream()
                .map(Review::getUserId)
                .collect(Collectors.toList());
        Map<Integer, User> userMap = new HashMap<>();
        userRepository.findAllById(userIds).forEach(
                user -> userMap.put(user.getUserId(), user)
        );
        
        // 현재 사용자 정보 (좋아요 여부 확인용)
        User currentUser = null;
        try {
            currentUser = getCurrentUser(uuid);
        } catch (Exception e) {
            log.debug("비로그인 사용자입니다.");
        }
        
        // 좋아요 여부 조회 (로그인한 사용자의 경우만)
        Map<Integer, Boolean> likedMap = new HashMap<>();
        if (currentUser != null) {
            List<ReviewLike> userLikes = reviewLikeRepository.findByUserAndReviewIn(
                currentUser, reviewPage.getContent()
            );
            userLikes.forEach(like -> 
                likedMap.put(like.getReview().getReviewId(), true)
            );
        }
        
        // 응답 DTO 변환
        List<ReviewListResponse> responseList = reviewPage.getContent().stream()
                .map(review -> {
                    User author = userMap.getOrDefault(review.getUserId(), User.builder().nickname("Unknown").image("default.jpg").build());
                    return ReviewListResponse.builder()
                            .reviewId(review.getReviewId())
                            .content(review.getContent())
                            .score(review.getScore())
                            .createdAt(review.getCreatedAt())
                            .updatedAt(review.getUpdatedAt())
                            .userNickname(author.getNickname())
                            .userImage(author.getImage())
                            .userUuid(author.getUuid())
                            .likeCount(likeCountMap.getOrDefault(review.getReviewId(), 0))
                            .isLiked(likedMap.getOrDefault(review.getReviewId(), false))
                            .build();
                })
                .collect(Collectors.toList());
        
        return PageResponse.<ReviewListResponse>builder()
                .content(responseList)
                .pageNumber(reviewPage.getNumber())
                .pageSize(reviewPage.getSize())
                .totalElements(reviewPage.getTotalElements())
                .totalPages(reviewPage.getTotalPages())
                .last(reviewPage.isLast())
                .build();
    }
    
    /**
     * 리뷰 수정
     */
    public ReviewDetailResponse updateReview(Integer reviewId, ReviewUpdateRequest request) {
        User currentUser = getCurrentUser(request.getUuid());
        
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("해당 리뷰를 찾을 수 없습니다."));
        
        // 리뷰 작성자만 수정 가능
        if (!review.getUserId().equals(currentUser.getUserId())) {
            throw new ForbiddenAccessException("리뷰 작성자만 수정할 수 있습니다.");
        }
        
        review.setContent(request.getContent());
        review.setScore(request.getScore());
        review.setUpdatedAt(LocalDateTime.now());
        
        Review updatedReview = reviewRepository.save(review);
        
        Attraction attraction = attractionRepository.findById(review.getAttractionId())
                .orElseThrow(() -> new EntityNotFoundException("해당 관광지를 찾을 수 없습니다."));
        
        // 좋아요 수 조회
        Integer likeCount = reviewLikeRepository.countByReviewId(reviewId);
        
        // 현재 사용자가 좋아요를 눌렀는지 여부 확인
        Boolean isLiked = reviewLikeRepository.findByReviewAndUser(updatedReview, currentUser).isPresent();
        
        return ReviewDetailResponse.fromEntity(
                updatedReview,
                attraction.getAttractionName(),
                currentUser.getNickname(),
                currentUser.getImage(),
                likeCount,
                isLiked
        );
    }
    
    /**
     * 리뷰 삭제
     */
    public void deleteReview(Integer reviewId, UUID uuid) {
        User currentUser = getCurrentUser(uuid);
        
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("해당 리뷰를 찾을 수 없습니다."));
        
        // 리뷰 작성자만 삭제 가능
        if (!review.getUserId().equals(currentUser.getUserId())) {
            throw new ForbiddenAccessException("리뷰 작성자만 삭제할 수 있습니다.");
        }
        
        // 리뷰에 대한 좋아요 먼저 삭제
        reviewLikeRepository.deleteByReview(review);
        
        // 리뷰 삭제
        reviewRepository.delete(review);
    }
    
    /**
     * 현재 로그인한 사용자의 리뷰 목록 조회
     */
    @Transactional(readOnly = true)
    public List<UserReviewResponse> getUserReviews(UUID uuid) {
        User user;
        
        // 사용자 ID가 제공된 경우 해당 사용자의 리뷰 조회
        if (uuid != null) {
            user = userRepository.findByUuidAndIsActiveTrue(uuid)
                    .orElseThrow(() -> new EntityNotFoundException("해당 사용자를 찾을 수 없습니다."));
        } else {
            // 제공되지 않은 경우 현재 사용자의 리뷰 조회
            throw new EntityNotFoundException("사용자 UUID를 제공해야 합니다.");
        }
        
        List<Review> reviewList = reviewRepository.findAllByUserId(user.getUserId());

        List<Integer> attractionIds = reviewList.stream()
                .map(Review::getAttractionId)
                .collect(Collectors.toList());

        Map<Integer, Attraction> attractionMap = new HashMap<>();
        attractionRepository.findAllById(attractionIds).forEach(
                attraction -> attractionMap.put(attraction.getAttractionId(), attraction)
        );

        return reviewList.stream()
                .map(review -> {
                    Attraction attraction = attractionMap.getOrDefault(
                            review.getAttractionId(),
                            Attraction.builder().attractionName("Unknown").build()
                    );

                    return UserReviewResponse.builder()
                            .reviewId(review.getReviewId())
                            .content(review.getContent())
                            .score(review.getScore())
                            .updateAt(review.getCreatedAt())
                            .attractionId(review.getAttractionId())
                            .attractionName(attraction.getAttractionName())
                            .likeCount(reviewLikeRepository.countByReviewId(review.getReviewId()))
                            .build();
                })
                .collect(Collectors.toList());
    }
    
    /**
     * 관광지별 전체 리뷰 조회
     */
    @Transactional(readOnly = true)
    public List<ReviewListResponse> getAllReviewsByAttraction(Integer attractionId, UUID uuid) {
        // 관광지 존재 여부 확인
        attractionRepository.findById(attractionId)
                .orElseThrow(() -> new EntityNotFoundException("해당 관광지를 찾을 수 없습니다."));
        
        List<Review> reviewList = reviewRepository.findByAttractionId(attractionId);
        
        // 리뷰 ID 목록 추출
        List<Integer> reviewIds = reviewList.stream()
                .map(Review::getReviewId)
                .collect(Collectors.toList());
        
        // 리뷰별 좋아요 수 조회
        Map<Integer, Integer> likeCountMap = new HashMap<>();
        if (!reviewIds.isEmpty()) {
            reviewLikeRepository.countByReviewIdIn(reviewIds).forEach(
                    count -> likeCountMap.put(count.getReviewId(), count.getCount())
            );
        }
        
        // 작성자 정보 조회
        List<Integer> userIds = reviewList.stream()
                .map(Review::getUserId)
                .collect(Collectors.toList());
        Map<Integer, User> userMap = new HashMap<>();
        if (!userIds.isEmpty()) {
            userRepository.findAllById(userIds).forEach(
                    user -> userMap.put(user.getUserId(), user)
            );
        }
        
        // 응답 DTO 변환
        return reviewList.stream()
                .map(review -> {
                    User author = userMap.getOrDefault(review.getUserId(), User.builder().nickname("Unknown").image("default.jpg").build());
                    return ReviewListResponse.builder()
                            .reviewId(review.getReviewId())
                            .content(review.getContent())
                            .score(review.getScore())
                            .createdAt(review.getCreatedAt())
                            .userNickname(author.getNickname())
                            .userImage(author.getImage())
                            .likeCount(likeCountMap.getOrDefault(review.getReviewId(), 0))
                            .build();
                })
                .collect(Collectors.toList());
    }
    
    /**
     * 리뷰 좋아요 토글
     */
    public ReviewLikeResponse toggleReviewLike(ReviewLikeRequest request) {
        User currentUser = getCurrentUser(request.getUuid());
        
        Review review = reviewRepository.findById(request.getReviewId())
                .orElseThrow(() -> new EntityNotFoundException("해당 리뷰를 찾을 수 없습니다."));
        
        // 이미 좋아요를 눌렀는지 확인
        ReviewLike existingLike = reviewLikeRepository.findByReviewAndUser(review, currentUser)
                .orElse(null);
        
        boolean isLiked;
        
        if (existingLike != null) {
            // 이미 좋아요를 누른 경우 -> 좋아요 취소
            reviewLikeRepository.delete(existingLike);
            isLiked = false;
        } else {
            // 좋아요를 누르지 않은 경우 -> 좋아요 추가
            ReviewLike newLike = ReviewLike.builder()
                    .review(review)
                    .user(currentUser)
                    .likedAt(LocalDateTime.now())
                    .build();
            reviewLikeRepository.save(newLike);
            isLiked = true;
        }
        
        // 좋아요 수 조회
        Integer likeCount = reviewLikeRepository.countByReviewId(request.getReviewId());
        
        return ReviewLikeResponse.builder()
                .reviewId(request.getReviewId())
                .isLiked(isLiked)
                .likeCount(likeCount)
                .build();
    }
    
    /**
     * 현재 로그인한 사용자 조회
     */
    private User getCurrentUser(UUID uuid) {
        Integer userId = uuidUserUtil.getRequiredUserId(uuid);
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("로그인 정보를 찾을 수 없습니다."));
    }
}
