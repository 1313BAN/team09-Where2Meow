package com.ssafy.where2meow.review.repository;

import com.ssafy.where2meow.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

  /**
   * 특정 여행지에 대한 리뷰 수 조회
   */
  @Query("SELECT COUNT(r) FROM Review r WHERE r.attractionId = :attractionId")
  Long countByAttractionId(@Param("attractionId") Integer attractionId);

  /**
   * 특정 여행지에 대한 평균 평점 조회
   */
  @Query("SELECT COALESCE(AVG(r.score), 0) FROM Review r WHERE r.attractionId = :attractionId")
  Double getAverageScoreByAttractionId(@Param("attractionId") Integer attractionId);

  List<Review> findByAttractionId(Integer attractionId);
}
