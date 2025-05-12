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
import com.ssafy.where2meow.review.dto.ReviewDto;
import com.ssafy.where2meow.review.dto.ReviewPageDto;
import com.ssafy.where2meow.review.entity.Review;
import com.ssafy.where2meow.review.repository.ReviewRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
   * 검색 로직:
   * 1. country, state, city 순서의 계층 구조를 따름
   * 2. country 없이 state나 city만으로는 검색 불가
   * 3. state 없이 city만으로는 검색 불가
   * 4. 카테고리 단독 또는 키워드 단독으로는 검색 가능
   *
   * @param countryId     국가 ID (계층구조 검색 시 필요)
   * @param stateId       시도 ID (계층구조 검색 시 city와 함께 사용할 경우 필요)
   * @param cityId        시군구 ID (계층구조 검색 시 반드시 country, state와 함께 사용 필요)
   * @param categoryId    카테고리 ID (단독 검색 가능)
   * @param searchKeyword 검색어 (단독 검색 가능)
   * @param pageable      페이징 정보
   * @return 검색 조건에 맞는 여행지 Page 객체
   */
  @Transactional(readOnly = true)
  public Page<AttractionListResponse> getAttractionList(
      Integer countryId, Integer stateId, Integer cityId, Integer categoryId,
      String searchKeyword, Pageable pageable) {

    // 계층 구조 유효성 검사
    // 1. city가 있는데 state나 country가 없으면 오류
    if (cityId != null && (stateId == null || countryId == null)) {
      throw new IllegalArgumentException("City search requires both State and Country");
    }
    
    // 2. state가 있는데 country가 없으면 오류
    if (stateId != null && countryId == null) {
      throw new IllegalArgumentException("State search requires Country");
    }
    
    // 3. 아무 조건도 없는 경우 오류
    if (countryId == null && categoryId == null && (searchKeyword == null || searchKeyword.isEmpty())) {
      throw new IllegalArgumentException("At least one search condition is required");
    }

    // 동적 쿼리 조건 생성
    Specification<Attraction> spec = Specification.where(null);
    
    // country 조건이 있으면 추가
    if (countryId != null) {
      spec = spec.and(AttractionSpecification.withCountryId(countryId));
    }
    
    // state 조건이 있으면 추가
    if (stateId != null) {
      spec = spec.and(AttractionSpecification.withStateCode(stateId));
    }
    
    // city 조건이 있으면 추가
    if (cityId != null) {
      spec = spec.and(AttractionSpecification.withCityCode(cityId));
    }
    
    // 카테고리 조건이 있으면 추가
    if (categoryId != null) {
      spec = spec.and(AttractionSpecification.withCategoryCode(categoryId));
    }

    // 검색어가 있는 경우 이름 검색 조건 추가
    if (searchKeyword != null && !searchKeyword.isEmpty()) {
      spec = spec.and(AttractionSpecification.withNameContains(searchKeyword));
    }

    // 페이징된 데이터 조회
    Page<Attraction> attractions = attractionRepository.findAll(spec, pageable);
    
    // 필요한 ID 목록 추출
    List<Integer> attractionIds = attractions.getContent().stream()
        .map(Attraction::getAttractionId)
        .collect(Collectors.toList());
    
    List<Integer> stateCodes = attractions.getContent().stream()
        .map(Attraction::getStateCode)
        .distinct()
        .collect(Collectors.toList());
    
    // 빈 리스트인 경우 빈 페이지 반환
    if (attractionIds.isEmpty()) {
        return Page.empty(pageable);
    }
    
    // 벌크 조회
    Map<Integer, Long> reviewCountMap = reviewRepository.countByAttractionIdIn(attractionIds).stream()
        .collect(Collectors.toMap(
            result -> result.getAttractionId(), 
            result -> result.getCount(),
            (existing, replacement) -> existing
        ));
    
    Map<Integer, Double> reviewScoreMap = reviewRepository.getAverageScoreByAttractionIdIn(attractionIds).stream()
        .collect(Collectors.toMap(
            result -> result.getAttractionId(), 
            result -> result.getScore(),
            (existing, replacement) -> existing
        ));
    
    Map<Integer, State> stateMap = stateRepository.findByStateCodeIn(stateCodes).stream()
        .collect(Collectors.toMap(State::getStateCode, state -> state));
    
    // 도시 코드 목록 추출 및 조회
    Map<String, City> cityMap = new HashMap<>();
    attractions.getContent().forEach(attraction -> {
        Integer stateCode = attraction.getStateCode();
        Integer cityCode = attraction.getCityCode();
        String key = stateCode + "_" + cityCode;
        if (!cityMap.containsKey(key)) {
            cityRepository.findByCityCodeAndStateCode(cityCode, stateCode)
                .ifPresent(city -> cityMap.put(key, city));
        }
    });

    // DTO로 변환하여 반환
    return attractions.map(attraction -> {
      String cityKey = attraction.getStateCode() + "_" + attraction.getCityCode();
      
      return AttractionListResponse.builder()
          .attractionId(attraction.getAttractionId())
          .attractionName(attraction.getAttractionName())
          .image(attraction.getFirstImage1())
          .reviewCount(reviewCountMap.getOrDefault(attraction.getAttractionId(), 0L))
          .reviewAvgScore(reviewScoreMap.getOrDefault(attraction.getAttractionId(), 0.0))
          .stateName(stateMap.getOrDefault(attraction.getStateCode(), new State()).getStateName())
          .cityName(cityMap.getOrDefault(cityKey, new City()).getCityName())
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

    // 리뷰 정보 조회
    Double reviewAvgScore = reviewRepository.getAverageScoreByAttractionId(attraction.getAttractionId());
    
    // 페이징된 리뷰 조회
    Pageable reviewPageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
    Page<Review> reviewPage = reviewRepository.findByAttractionId(attraction.getAttractionId(), reviewPageable);
    List<Review> reviews = reviewPage.getContent();

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
        .reviews(ReviewPageDto.fromPage(reviewPage, 
                reviews.stream()
                .map(review -> ReviewDto.convertDto(review))
                .collect(Collectors.toList())))
        .reviewAvgScore(reviewAvgScore != null ? reviewAvgScore : 0.0)
        .build();
  }
}
