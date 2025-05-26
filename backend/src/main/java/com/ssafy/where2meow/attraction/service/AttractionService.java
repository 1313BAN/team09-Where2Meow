package com.ssafy.where2meow.attraction.service;

import com.ssafy.where2meow.attraction.dto.AttractionCategoryResponse;
import com.ssafy.where2meow.attraction.dto.AttractionListResponse;
import com.ssafy.where2meow.attraction.entity.Attraction;
import com.ssafy.where2meow.attraction.entity.AttractionCategory;
import com.ssafy.where2meow.attraction.repository.AttractionRepository;
import com.ssafy.where2meow.attraction.repository.AttractionCategoryRepository;
import com.ssafy.where2meow.address.entity.State;
import com.ssafy.where2meow.address.entity.City;
import com.ssafy.where2meow.address.repository.StateRepository;
import com.ssafy.where2meow.address.repository.CityRepository;
import com.ssafy.where2meow.review.repository.ReviewRepository;
import com.ssafy.where2meow.common.service.HybridImageService;
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

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AttractionService {

  private final AttractionRepository attractionRepository;
  private final AttractionCategoryRepository attractionCategoryRepository;
  private final StateRepository stateRepository;
  private final CityRepository cityRepository;
  private final ReviewRepository reviewRepository;
  private final HybridImageService hybridImageService;

  /**
   * 여행지 페이징 조회 (고화질 이미지 적용)
   */
  public Page<AttractionListResponse> getAttractionList(
      Integer countryId, Integer stateId, Integer cityId, Integer categoryId,
      String searchKeyword, Pageable pageable) {

    // 계층구조 검증 로직
    if (cityId != null && (countryId == null || stateId == null)) {
      throw new IllegalArgumentException("도시 검색 시 국가와 시도 정보가 필요합니다.");
    }
    if (stateId != null && countryId == null) {
      throw new IllegalArgumentException("시도 검색 시 국가 정보가 필요합니다.");
    }

    // 검색 조건이 하나도 없는 경우
    if (countryId == null && stateId == null && cityId == null &&
        categoryId == null &&
        (searchKeyword == null || searchKeyword.trim().isEmpty())) {
      return Page.empty(pageable);
    }

    // Specification 생성
    Specification<Attraction> spec = Specification.where(null);

    if (countryId != null) {
      spec = spec.and((root, query, criteriaBuilder) ->
          criteriaBuilder.equal(root.get("countryCode"), countryId));
    }

    if (stateId != null) {
      spec = spec.and((root, query, criteriaBuilder) ->
          criteriaBuilder.equal(root.get("stateCode"), stateId));
    }

    if (cityId != null) {
      spec = spec.and((root, query, criteriaBuilder) ->
          criteriaBuilder.equal(root.get("cityCode"), cityId));
    }

    if (categoryId != null) {
      spec = spec.and((root, query, criteriaBuilder) ->
          criteriaBuilder.equal(root.get("attractionCategoryId"), categoryId));
    }

    if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
      String keyword = "%" + searchKeyword.trim() + "%";
      spec = spec.and((root, query, criteriaBuilder) ->
          criteriaBuilder.like(root.get("attractionName"), keyword));
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

    List<Integer> categoryIds = attractions.getContent().stream()
        .map(Attraction::getAttractionCategoryId)
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

    Map<Integer, AttractionCategory> categoryMap = attractionCategoryRepository.findAllById(categoryIds).stream()
        .collect(Collectors.toMap(AttractionCategory::getAttractionCategoryId, category -> category));

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

    // DTO로 변환하여 반환 (고화질 이미지 적용)
    return attractions.map(attraction -> {
      String cityKey = attraction.getStateCode() + "_" + attraction.getCityCode();
      AttractionCategory category = categoryMap.get(attraction.getAttractionCategoryId());
      String highQualityImageUrl = hybridImageService.getBestImageUrl(attraction);

      return AttractionListResponse.builder()
          .attractionId(attraction.getAttractionId())
          .attractionName(attraction.getAttractionName())
          .image(highQualityImageUrl)
          .reviewCount(reviewCountMap.getOrDefault(attraction.getAttractionId(), 0L))
          .reviewAvgScore(reviewScoreMap.getOrDefault(attraction.getAttractionId(), 0.0))
          .stateName(stateMap.getOrDefault(attraction.getStateCode(), new State()).getStateName())
          .cityName(cityMap.getOrDefault(cityKey, new City()).getCityName())
          .categoryId(attraction.getAttractionCategoryId())
          .categoryName(category != null ? category.getAttractionCategoryName() : "기타")
          // ✅ 위도/경도 추가
          .latitude(attraction.getLatitude())
          .longitude(attraction.getLongitude())
          .build();
    });

  }

  /**
   * 관광지 상세 조회 (고화질 이미지 적용)
   */
  public AttractionListResponse getAttractionDetail(Integer attractionId) {
    Attraction attraction = attractionRepository.findById(attractionId)
        .orElseThrow(() -> new IllegalArgumentException("해당 관광지를 찾을 수 없습니다."));

    // 고화질 이미지 URL (크기 제한 없음)
    String highQualityImageUrl = hybridImageService.getBestImageUrl(attraction);

    // State, City, Category 정보 조회
    State state = stateRepository.findByStateCode(attraction.getStateCode()).orElse(new State());
    City city = cityRepository.findByCityCodeAndStateCode(attraction.getCityCode(), attraction.getStateCode())
        .orElse(new City());
    AttractionCategory category = attractionCategoryRepository.findById(attraction.getAttractionCategoryId())
        .orElse(null);

    // 리뷰 정보 조회
    Long reviewCount = reviewRepository.countByAttractionId(attractionId);
    Double reviewAvgScore = reviewRepository.getAverageScoreByAttractionId(attractionId);

    return AttractionListResponse.builder()
        .attractionId(attraction.getAttractionId())
        .attractionName(attraction.getAttractionName())
        .image(highQualityImageUrl)
        .reviewCount(reviewCount != null ? reviewCount : 0L)
        .reviewAvgScore(reviewAvgScore != null ? reviewAvgScore : 0.0)
        .stateName(state.getStateName())
        .cityName(city.getCityName())
        .categoryId(attraction.getAttractionCategoryId())
        .categoryName(category != null ? category.getAttractionCategoryName() : "기타")
        // ✅ 위도/경도 추가
        .latitude(attraction.getLatitude())
        .longitude(attraction.getLongitude())
        .build();

  }

  /**
   * 모든 카테고리 목록 조회
   */
  public List<AttractionCategoryResponse> getAllCategories() {
    List<AttractionCategory> categories = attractionCategoryRepository.findAllOrderByName();
    return categories.stream()
        .map(AttractionCategoryResponse::fromEntity)
        .collect(Collectors.toList());
  }

  /**
   * 인기 관광지 조회 (고화질 이미지 사전 로딩용)
   */
  public List<Attraction> getPopularAttractions(int limit) {
    Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "attractionName"));
    return attractionRepository.findAll(pageable).getContent();
  }

  /**
   * 관광지 이미지 URL만 조회 (고화질 버전)
   */
  public String getAttractionImageUrl(Integer attractionId) {
    Attraction attraction = attractionRepository.findById(attractionId)
        .orElseThrow(() -> new IllegalArgumentException("해당 관광지를 찾을 수 없습니다."));

    return hybridImageService.getBestImageUrl(attraction);
  }

  /**
   * 여러 관광지의 고화질 이미지 URL 일괄 조회
   */
  public Map<Integer, String> getAttractionImageUrls(List<Integer> attractionIds) {
    List<Attraction> attractions = attractionRepository.findAllById(attractionIds);

    return attractions.stream()
        .collect(Collectors.toMap(
            Attraction::getAttractionId,
            attraction -> hybridImageService.getBestImageUrl(attraction)
        ));
  }

  /**
   * 관광지 이미지 캐시 새로고침 (관리자용)
   */
  @Transactional
  public String refreshAttractionImageCache(Integer attractionId) {
    Attraction attraction = attractionRepository.findById(attractionId)
        .orElseThrow(() -> new IllegalArgumentException("해당 관광지를 찾을 수 없습니다."));

    return hybridImageService.refreshImageCache(attraction);
  }

  /**
   * 하위 호환성을 위한 메서드 (크기 파라미터는 무시)
   *
   * @deprecated 고화질 이미지를 사용하므로 크기 파라미터가 불필요합니다. getAttractionImageUrl(Integer)을 사용하세요.
   */
  @Deprecated
  public String getAttractionImageUrl(Integer attractionId, int width, int height) {
    log.warn("크기 파라미터가 있는 getAttractionImageUrl은 deprecated되었습니다. 고화질 이미지를 반환합니다.");
    return getAttractionImageUrl(attractionId);
  }

  /**
   * 하위 호환성을 위한 메서드 (크기 파라미터는 무시)
   *
   * @deprecated 고화질 이미지를 사용하므로 크기 파라미터가 불필요합니다. getAttractionImageUrls(List)을 사용하세요.
   */
  @Deprecated
  public Map<Integer, String> getAttractionImageUrls(List<Integer> attractionIds, int width, int height) {
    log.warn("크기 파라미터가 있는 getAttractionImageUrls는 deprecated되었습니다. 고화질 이미지를 반환합니다.");
    return getAttractionImageUrls(attractionIds);
  }
}
