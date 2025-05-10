package com.ssafy.where2meow.attraction.repository;

import com.ssafy.where2meow.attraction.entity.Attraction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttractionRepository extends JpaRepository<Attraction, Integer> {
    
    // 국가, 시도, 시군구, 카테고리 모두 지정된 경우
    @Query("SELECT a FROM Attraction a WHERE a.country.countryId = :countryId AND a.state.stateId = :stateId AND a.city.cityId = :cityId AND a.attractionCategory.attractionCategoryId = :categoryId")
    List<Attraction> findByCountryIdAndStateIdAndCityIdAndCategoryId(
            @Param("countryId") Integer countryId, 
            @Param("stateId") Integer stateId, 
            @Param("cityId") Integer cityId, 
            @Param("categoryId") Integer categoryId);

    // 국가, 시도, 카테고리만 지정된 경우
    @Query("SELECT a FROM Attraction a WHERE a.country.countryId = :countryId AND a.state.stateId = :stateId AND a.attractionCategory.attractionCategoryId = :categoryId")
    List<Attraction> findByCountryIdAndStateIdAndCategoryId(
            @Param("countryId") Integer countryId, 
            @Param("stateId") Integer stateId, 
            @Param("categoryId") Integer categoryId);

    // 국가, 시군구, 카테고리만 지정된 경우
    @Query("SELECT a FROM Attraction a WHERE a.country.countryId = :countryId AND a.city.cityId = :cityId AND a.attractionCategory.attractionCategoryId = :categoryId")
    List<Attraction> findByCountryIdAndCityIdAndCategoryId(
            @Param("countryId") Integer countryId, 
            @Param("cityId") Integer cityId, 
            @Param("categoryId") Integer categoryId);

    // 국가, 카테고리만 지정된 경우
    @Query("SELECT a FROM Attraction a WHERE a.country.countryId = :countryId AND a.attractionCategory.attractionCategoryId = :categoryId")
    List<Attraction> findByCountryIdAndCategoryId(
            @Param("countryId") Integer countryId, 
            @Param("categoryId") Integer categoryId);

    // 국가, 시도, 시군구만 지정된 경우
    @Query("SELECT a FROM Attraction a WHERE a.country.countryId = :countryId AND a.state.stateId = :stateId AND a.city.cityId = :cityId")
    List<Attraction> findByCountryIdAndStateIdAndCityId(
            @Param("countryId") Integer countryId, 
            @Param("stateId") Integer stateId, 
            @Param("cityId") Integer cityId);

    // 국가, 시도만 지정된 경우
    @Query("SELECT a FROM Attraction a WHERE a.country.countryId = :countryId AND a.state.stateId = :stateId")
    List<Attraction> findByCountryIdAndStateId(
            @Param("countryId") Integer countryId, 
            @Param("stateId") Integer stateId);

    // 국가, 시군구만 지정된 경우
    @Query("SELECT a FROM Attraction a WHERE a.country.countryId = :countryId AND a.city.cityId = :cityId")
    List<Attraction> findByCountryIdAndCityId(
            @Param("countryId") Integer countryId, 
            @Param("cityId") Integer cityId);

    // 국가만 지정된 경우
    @Query("SELECT a FROM Attraction a WHERE a.country.countryId = :countryId")
    List<Attraction> findByCountryId(@Param("countryId") Integer countryId);
    
    // 상세 조회를 위한 공통 연관 처리
    @Query("SELECT a FROM Attraction a JOIN FETCH a.city JOIN FETCH a.state JOIN FETCH a.country JOIN FETCH a.attractionCategory WHERE a.attractionId = :attractionId")
    Optional<Attraction> findByIdWithDetails(@Param("attractionId") Integer attractionId);
}
