package com.ssafy.where2meow.attraction.repository.specification;

import com.ssafy.where2meow.attraction.entity.Attraction;
import org.springframework.data.jpa.domain.Specification;

/**
 * 동적 쿼리 생성을 위한 Specification 클래스
 */
public class AttractionSpecification {

    /**
     * 국가 ID 기준 검색 조건
     */
    public static Specification<Attraction> withCountryId(Integer countryId) {
        return (root, query, cb) -> {
            if (countryId == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("country").get("countryId"), countryId);
        };
    }
    
    /**
     * 시도 ID 기준 검색 조건
     */
    public static Specification<Attraction> withStateId(Integer stateId) {
        return (root, query, cb) -> {
            if (stateId == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("state").get("stateId"), stateId);
        };
    }
    
    /**
     * 시군구 ID 기준 검색 조건
     */
    public static Specification<Attraction> withCityId(Integer cityId) {
        return (root, query, cb) -> {
            if (cityId == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("city").get("cityId"), cityId);
        };
    }
    
    /**
     * 카테고리 ID 기준 검색 조건
     */
    public static Specification<Attraction> withCategoryId(Integer categoryId) {
        return (root, query, cb) -> {
            if (categoryId == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("attractionCategory").get("attractionCategoryId"), categoryId);
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
