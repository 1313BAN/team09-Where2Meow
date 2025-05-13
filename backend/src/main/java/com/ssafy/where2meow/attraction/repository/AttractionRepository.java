package com.ssafy.where2meow.attraction.repository;

import com.ssafy.where2meow.attraction.entity.Attraction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AttractionRepository extends JpaRepository<Attraction, Integer>, JpaSpecificationExecutor<Attraction> {

  // 페이징 지원 메소드 추가
  @Query("SELECT a FROM Attraction a WHERE a.countryId = :countryId ORDER BY a.attractionName ASC")
  Page<Attraction> findByCountryIdOrderByAttractionNameAsc(@Param("countryId") Integer countryId, Pageable pageable);

}
