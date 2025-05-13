package com.ssafy.where2meow.review.repository;

import com.ssafy.where2meow.review.entity.Review;
import com.ssafy.where2meow.review.entity.ReviewLike;
import com.ssafy.where2meow.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Integer> {

    /**
     * 특정 사용자가 특정 리뷰에 좋아요를 눌렀는지 확인
     */
    Optional<ReviewLike> findByReviewAndUser(Review review, User user);
    
    /**
     * 리뷰별 좋아요 수 조회
     */
    @Query("SELECT COUNT(rl) FROM ReviewLike rl WHERE rl.review.reviewId = :reviewId")
    Integer countByReviewId(@Param("reviewId") Integer reviewId);
    
    /**
     * 여러 리뷰의 좋아요 수 벌크 조회
     */
    @Query("SELECT rl.review.reviewId as reviewId, COUNT(rl) as count FROM ReviewLike rl WHERE rl.review.reviewId IN :reviewIds GROUP BY rl.review.reviewId")
    List<ReviewLikeCount> countByReviewIdIn(@Param("reviewIds") List<Integer> reviewIds);
    
    /**
     * 특정 사용자가 좋아요한 리뷰 목록 조회
     */
    List<ReviewLike> findByUser(User user);
    
    /**
     * 특정 리뷰에 대한 모든 좋아요 삭제
     */
    void deleteByReview(Review review);
    
    /**
     * 좋아요 수를 반환하는 인터페이스
     */
    interface ReviewLikeCount {
        Integer getReviewId();
        Integer getCount();
    }
}
