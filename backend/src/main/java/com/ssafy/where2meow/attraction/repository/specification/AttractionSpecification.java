package com.ssafy.where2meow.attraction.repository.specification;

import com.ssafy.where2meow.attraction.entity.Attraction;
import org.springframework.data.jpa.domain.Specification;

/**
 * 동적 쿼리 생성을 위한 Specification 클래스
 */
public class AttractionSpecification {

  /**
   * country ID 기준 검색 조건
   */
  public static Specification<Attraction> withCountryId(Integer countryId) {
    return (root, query, cb) -> {
      if (countryId == null) {
        return cb.conjunction();
      }
      return cb.equal(root.get("countryId"), countryId);
    };
  }

  /**
   * state code 기준 검색 조건
   */
  public static Specification<Attraction> withStateCode(Integer stateCode) {
    return (root, query, cb) -> {
      if (stateCode == null) {
        return cb.conjunction();
      }
      return cb.equal(root.get("stateCode"), stateCode);
    };
  }

  /**
   * city code 기준 검색 조건
   */
  public static Specification<Attraction> withCityCode(Integer cityCode) {
    return (root, query, cb) -> {
      if (cityCode == null) {
        return cb.conjunction();
      }
      return cb.equal(root.get("cityCode"), cityCode);
    };
  }

  /**
   * 카테고리 ID 기준 검색 조건
   */
  public static Specification<Attraction> withCategoryCode(Integer categoryCode) {
    return (root, query, cb) -> {
      if (categoryCode == null) {
        return cb.conjunction();
      }
      return cb.equal(root.get("attractionCategoryId"), categoryCode);
    };
  }

  /**
   * 여행지 이름 검색 조건
   */
  public static Specification<Attraction> withNameContains(String name) {
    return (root, query, cb) -> {
      if (name == null || name.isEmpty()) {
        return cb.conjunction();
      }
      return cb.like(cb.lower(root.get("attractionName")), "%" + name.toLowerCase() + "%");
    };
  }
}
