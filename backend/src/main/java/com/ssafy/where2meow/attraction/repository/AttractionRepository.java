package com.ssafy.where2meow.attraction.repository;

import com.ssafy.where2meow.attraction.entity.Attraction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AttractionRepository extends JpaRepository<Attraction, Integer>, JpaSpecificationExecutor<Attraction> {
    
    // 페이징 지원 메소드 추가
    @Query("SELECT a FROM Attraction a WHERE a.country.countryId = :countryId ORDER BY a.attractionName ASC")
    Page<Attraction> findByCountryIdOrderByAttractionNameAsc(@Param("countryId") Integer countryId, Pageable pageable);
    
    // 상세 조회를 위한 공통 연관 처리 (유지)
    @Query("SELECT a FROM Attraction a JOIN FETCH a.city JOIN FETCH a.state JOIN FETCH a.country JOIN FETCH a.attractionCategory WHERE a.attractionId = :attractionId")
    Optional<Attraction> findByIdWithDetails(@Param("attractionId") Integer attractionId);
}
