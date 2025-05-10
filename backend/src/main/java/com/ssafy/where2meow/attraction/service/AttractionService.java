package com.ssafy.where2meow.attraction.service;

import com.ssafy.where2meow.attraction.dto.AttractionDetailResponse;
import com.ssafy.where2meow.attraction.dto.AttractionListResponse;
import com.ssafy.where2meow.attraction.entity.Attraction;
import com.ssafy.where2meow.attraction.repository.AttractionRepository;
import com.ssafy.where2meow.attraction.repository.specification.AttractionSpecification;
import com.ssafy.where2meow.review.repository.ReviewRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttractionService {

    private final AttractionRepository attractionRepository;
    private final ReviewRepository reviewRepository;

    /**
     * 조건별 여행지 검색 - 페이징 적용
     * 
     * @param countryId 국가 ID
     * @param stateId 시도 ID
     * @param cityId 시군구 ID
     * @param categoryId 카테고리 ID
     * @param searchKeyword 검색어
     * @param pageable 페이징 정보
     * @return 검색 조건에 맞는 여행지 Page 객체
     */
    @Transactional(readOnly = true)
    public Page<AttractionListResponse> getAttractionList(
            Integer countryId, Integer stateId, Integer cityId, Integer categoryId, 
            String searchKeyword, Pageable pageable) {
        
        // 국가는 반드시 있어야 함
        if (countryId == null) {
            throw new IllegalArgumentException("Country ID is required");
        }
        
        // 동적 쿼리 조건 생성
        Specification<Attraction> spec = Specification.where(AttractionSpecification.withCountryId(countryId))
                .and(AttractionSpecification.withStateId(stateId))
                .and(AttractionSpecification.withCityId(cityId))
                .and(AttractionSpecification.withCategoryId(categoryId));
        
        // 검색어가 있는 경우 이름 검색 조건 추가
        if (searchKeyword != null && !searchKeyword.isEmpty()) {
            spec = spec.and(AttractionSpecification.withNameContains(searchKeyword));
        }
        
        // 페이징된 데이터 조회
        Page<Attraction> attractions = attractionRepository.findAll(spec, pageable);
        
        // DTO로 변환하여 반환
        return attractions.map(attraction -> {
            // 리뷰 정보 조회
            Long reviewCount = reviewRepository.countByAttractionId(attraction.getAttractionId());
            Double reviewAvgScore = reviewRepository.getAverageScoreByAttractionId(attraction.getAttractionId());
            
            return AttractionListResponse.builder()
                    .attractionId(attraction.getAttractionId())
                    .attractionName(attraction.getAttractionName())
                    .image(attraction.getFirstImage1())
                    .reviewCount(reviewCount)
                    .reviewAvgScore(reviewAvgScore)
                    .stateName(attraction.getState().getStateName())
                    .cityName(attraction.getCity().getCityName())
                    .build();
        });
    }

    /**
     * 레거시 호환성을 위한 메서드 - 페이징되지 않은 리스트 반환
     * 
     * @param countryId 국가 ID
     * @param stateId 시도 ID
     * @param cityId 시군구 ID
     * @param categoryId 카테고리 ID
     * @return 검색 조건에 맞는 여행지 목록
     */
    @Transactional(readOnly = true)
    public List<AttractionListResponse> getAttractionList(Integer countryId, Integer stateId, Integer cityId, Integer categoryId) {
        // 검색어 없이 페이징 없이 호출하는 경우 (기존 인터페이스 유지)
        Specification<Attraction> spec = Specification.where(AttractionSpecification.withCountryId(countryId))
                .and(AttractionSpecification.withStateId(stateId))
                .and(AttractionSpecification.withCityId(cityId))
                .and(AttractionSpecification.withCategoryId(categoryId));
        
        List<Attraction> attractions = attractionRepository.findAll(spec);
        
        return attractions.stream().map(attraction -> {
            Long reviewCount = reviewRepository.countByAttractionId(attraction.getAttractionId());
            Double reviewAvgScore = reviewRepository.getAverageScoreByAttractionId(attraction.getAttractionId());
            
            return AttractionListResponse.builder()
                    .attractionId(attraction.getAttractionId())
                    .attractionName(attraction.getAttractionName())
                    .image(attraction.getFirstImage1())
                    .reviewCount(reviewCount)
                    .reviewAvgScore(reviewAvgScore)
                    .stateName(attraction.getState().getStateName())
                    .cityName(attraction.getCity().getCityName())
                    .build();
        }).collect(Collectors.toList());
    }

    /**
     * 여행지 상세정보 조회
     * 
     * @param attractionId 여행지 ID
     * @return 여행지 상세 정보
     */
    @Transactional(readOnly = true)
    public AttractionDetailResponse getAttractionDetail(Integer attractionId) {
        Attraction attraction = attractionRepository.findByIdWithDetails(attractionId)
                .orElseThrow(() -> new EntityNotFoundException("Attraction not found with id: " + attractionId));
        
        return AttractionDetailResponse.builder()
                .attractionId(attraction.getAttractionId())
                .contentId(attraction.getContentId())
                .attractionName(attraction.getAttractionName())
                .firstImage1(attraction.getFirstImage1())
                .firstImage2(attraction.getFirstImage2())
                .mapLevel(attraction.getMapLevel())
                .latitude(attraction.getLatitude())
                .longitude(attraction.getLongitude())
                .tel(attraction.getTel())
                .addr1(attraction.getAddr1())
                .addr2(attraction.getAddr2())
                .homepage(attraction.getHomepage())
                .overview(attraction.getOverview())
                .categoryName(attraction.getAttractionCategory().getAttractionCategoryName())
                .countryName(attraction.getCountry().getCountryName())
                .stateName(attraction.getState().getStateName())
                .cityName(attraction.getCity().getCityName())
                .build();
    }
}
