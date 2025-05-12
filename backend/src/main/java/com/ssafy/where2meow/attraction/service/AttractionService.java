package com.ssafy.where2meow.attraction.service;

import com.ssafy.where2meow.address.entity.City;
import com.ssafy.where2meow.address.entity.Country;
import com.ssafy.where2meow.address.entity.State;
import com.ssafy.where2meow.address.repository.CityRepository;
import com.ssafy.where2meow.address.repository.CountryRepository;
import com.ssafy.where2meow.address.repository.StateRepository;
import com.ssafy.where2meow.attraction.dto.AttractionDetailResponse;
import com.ssafy.where2meow.attraction.dto.AttractionListResponse;
import com.ssafy.where2meow.attraction.entity.Attraction;
import com.ssafy.where2meow.attraction.entity.AttractionCategory;
import com.ssafy.where2meow.attraction.repository.AttractionCategoryRepository;
import com.ssafy.where2meow.attraction.repository.AttractionRepository;
import com.ssafy.where2meow.attraction.repository.specification.AttractionSpecification;
import com.ssafy.where2meow.review.entity.Review;
import com.ssafy.where2meow.review.repository.ReviewRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttractionService {

  private final AttractionRepository attractionRepository;
  private final ReviewRepository reviewRepository;
  private final CountryRepository countryRepository;
  private final StateRepository stateRepository;
  private final CityRepository cityRepository;
  private final AttractionCategoryRepository attractionCategoryRepository;

  /**
   * 조건별 여행지 검색 - 페이징 적용
   *
   * @param countryId     국가 ID
   * @param stateId       시도 ID
   * @param cityId        시군구 ID
   * @param categoryId    카테고리 ID
   * @param searchKeyword 검색어
   * @param pageable      페이징 정보
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
        .and(AttractionSpecification.withStateCode(stateId))
        .and(AttractionSpecification.withCityCode(cityId))
        .and(AttractionSpecification.withCategoryCode(categoryId));

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

      State state = stateRepository.findByStateCode(attraction.getStateCode()).orElseThrow(EntityNotFoundException::new);
      City city = cityRepository.findByCityCodeAndStateCode(attraction.getStateCode(), attraction.getCityCode()).orElseThrow(EntityNotFoundException::new);

      return AttractionListResponse.builder()
          .attractionId(attraction.getAttractionId())
          .attractionName(attraction.getAttractionName())
          .image(attraction.getFirstImage1())
          .reviewCount(reviewCount)
          .reviewAvgScore(reviewAvgScore)
          .stateName(state.getStateName())
          .cityName(city.getCityName())
          .build();
    });
  }

  /**
   * 여행지 상세정보 조회
   *
   * @param attractionId 여행지 ID
   * @return 여행지 상세 정보
   */
  @Transactional(readOnly = true)
  public AttractionDetailResponse getAttractionDetail(Integer attractionId) {
    Attraction attraction = attractionRepository.findById(attractionId)
        .orElseThrow(() -> new EntityNotFoundException("Attraction not found with id: " + attractionId));
    AttractionCategory attractionCategory = attractionCategoryRepository.findById(attraction.getAttractionCategoryId())
        .orElseThrow(EntityNotFoundException::new);
    Country country = countryRepository.findById(attraction.getCountryId())
        .orElseThrow(EntityNotFoundException::new);
    State state = stateRepository.findByStateCode(attraction.getStateCode())
        .orElseThrow(EntityNotFoundException::new);
    City city = cityRepository.findByCityCodeAndStateCode(attraction.getCityCode(), attraction.getStateCode())
        .orElseThrow(EntityNotFoundException::new);

    Double reviewAvgScore = reviewRepository.getAverageScoreByAttractionId(attraction.getAttractionId());
    List<Review> reviews = reviewRepository.findByAttractionId(attraction.getAttractionId());

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
        .categoryName(attractionCategory.getAttractionCategoryName())
        .countryName(country.getCountryName())
        .stateName(state.getStateName())
        .cityName(city.getCityName())
        .reviews(reviews)
        .reviewAvgScore(reviewAvgScore)
        .build();
  }
}
