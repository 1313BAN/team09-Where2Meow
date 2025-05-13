package com.ssafy.where2meow.review.repository;

import com.ssafy.where2meow.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
   * 여러 여행지에 대한 리뷰 수 벌크 조회
   */
  @Query("SELECT r.attractionId as attractionId, COUNT(r) as count FROM Review r WHERE r.attractionId IN :attractionIds GROUP BY r.attractionId")
  List<ReviewCount> countByAttractionIdIn(@Param("attractionIds") List<Integer> attractionIds);

  /**
   * 특정 여행지에 대한 평균 평점 조회
   */
  @Query("SELECT COALESCE(AVG(r.score), 0) FROM Review r WHERE r.attractionId = :attractionId")
  Double getAverageScoreByAttractionId(@Param("attractionId") Integer attractionId);
  
  /**
   * 여러 여행지에 대한 평균 평점 벌크 조회
   */
  @Query("SELECT r.attractionId as attractionId, COALESCE(AVG(r.score), 0) as score FROM Review r WHERE r.attractionId IN :attractionIds GROUP BY r.attractionId")
  List<ReviewScore> getAverageScoreByAttractionIdIn(@Param("attractionIds") List<Integer> attractionIds);

  List<Review> findByAttractionId(Integer attractionId);
  
  /**
   * 특정 여행지에 대한 페이징된 리뷰 조회
   */
  Page<Review> findByAttractionId(Integer attractionId, Pageable pageable);
  
  /**
   * 리뷰 수를 반환하는 인터페이스
   */
  interface ReviewCount {
    Integer getAttractionId();
    Long getCount();
  }
  
  /**
   * 평균 평점을 반환하는 인터페이스
   */
  interface ReviewScore {
    Integer getAttractionId();
    Double getScore();
  }
}
