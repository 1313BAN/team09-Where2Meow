package com.ssafy.where2meow.attraction.service;

import com.ssafy.where2meow.Review.repository.ReviewRepository;
import com.ssafy.where2meow.attraction.dto.AttractionDetailResponse;
import com.ssafy.where2meow.attraction.dto.AttractionListResponse;
import com.ssafy.where2meow.attraction.entity.Attraction;
import com.ssafy.where2meow.attraction.repository.AttractionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class AttractionService {

    private final AttractionRepository attractionRepository;
    private final ReviewRepository reviewRepository;

    @Transactional(readOnly = true)
    public List<AttractionListResponse> getAttractionList(Integer countryId, Integer stateId, Integer cityId, Integer categoryId) {
        // 국가는 반드시 있어야 함
        if (countryId == null) {
            throw new IllegalArgumentException("Country ID is required");
        }
        
        // 매개변수 조합에 따라 적절한 검색 메소드 호출
        List<Attraction> attractions;
        if (stateId != null && cityId != null && categoryId != null) {
            // 국가, 시도, 시군구, 카테고리 모두 지정
            attractions = attractionRepository.findByCountryIdAndStateIdAndCityIdAndCategoryId(countryId, stateId, cityId, categoryId);
        } else if (stateId != null && cityId != null) {
            // 국가, 시도, 시군구만 지정
            attractions = attractionRepository.findByCountryIdAndStateIdAndCityId(countryId, stateId, cityId);
        } else if (stateId != null && categoryId != null) {
            // 국가, 시도, 카테고리만 지정
            attractions = attractionRepository.findByCountryIdAndStateIdAndCategoryId(countryId, stateId, categoryId);
        } else if (cityId != null && categoryId != null) {
            // 국가, 시군구, 카테고리만 지정
            attractions = attractionRepository.findByCountryIdAndCityIdAndCategoryId(countryId, cityId, categoryId);
        } else if (stateId != null) {
            // 국가, 시도만 지정
            attractions = attractionRepository.findByCountryIdAndStateId(countryId, stateId);
        } else if (cityId != null) {
            // 국가, 시군구만 지정
            attractions = attractionRepository.findByCountryIdAndCityId(countryId, cityId);
        } else if (categoryId != null) {
            // 국가, 카테고리만 지정
            attractions = attractionRepository.findByCountryIdAndCategoryId(countryId, categoryId);
        } else {
            // 국가만 지정
            attractions = attractionRepository.findByCountryId(countryId);
        }
        
        // Attraction 엔티티를 AttractionListResponse DTO로 변환
        List<AttractionListResponse> result = new ArrayList<>();
        
        for (Attraction attraction : attractions) {
            // 리뷰 정보 조회
            Long reviewCount = reviewRepository.countByAttractionId(attraction.getAttractionId());
            Double reviewAvgScore = reviewRepository.getAverageScoreByAttractionId(attraction.getAttractionId());
            
            result.add(AttractionListResponse.builder()
                    .attractionId(attraction.getAttractionId())
                    .attractionName(attraction.getAttractionName())
                    .image(attraction.getFirstImage1())
                    .reviewCount(reviewCount)
                    .reviewAvgScore(reviewAvgScore)
                    .stateName(attraction.getState().getStateName())
                    .cityName(attraction.getCity().getCityName())
                    .build());
        }
        
        return result;
    }

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
