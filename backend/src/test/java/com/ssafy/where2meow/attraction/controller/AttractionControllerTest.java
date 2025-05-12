package com.ssafy.where2meow.attraction.controller;

import com.ssafy.where2meow.attraction.entity.Attraction;
import com.ssafy.where2meow.attraction.repository.AttractionRepository;
import com.ssafy.where2meow.attraction.repository.specification.AttractionSpecification;
import com.ssafy.where2meow.review.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AttractionControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private AttractionRepository attractionRepository;

  @Autowired
  private ReviewRepository reviewRepository;

  private TestAttractionContext context;

  @BeforeEach
  void setUp() {
    context = new TestAttractionContext();
  }

  /**
   * 테스트 데이터 컨텍스트를 관리하는 내부 클래스
   */
  private class TestAttractionContext {
    Integer countryId;
    Integer stateId;
    Integer cityId;
    Integer categoryId;
    Integer attractionId;

    TestAttractionContext() {
      // DB에서 조회하여 실제 존재하는 데이터로 테스트 수행
      List<Attraction> attractions = attractionRepository.findAll();
      assumeTrue(!attractions.isEmpty(), "테스트 데이터가 없습니다.");

      // 첫 번째 여행지 정보 가져오기
      Attraction firstAttraction = attractions.get(0);
      countryId = firstAttraction.getCountryId();
      stateId = firstAttraction.getStateCode();
      cityId = firstAttraction.getCityCode();
      categoryId = firstAttraction.getAttractionCategoryId();
      attractionId = firstAttraction.getAttractionId();
    }
  }

  @Nested
  @DisplayName("여행지 목록 조회 테스트")
  class AttractionListTests {
    @Test
    @DisplayName("모든 파라미터로 여행지 목록 조회")
    void getAttractionListWithAllParams() throws Exception {
      // 실제 데이터를 기반으로 테스트
      Specification<Attraction> spec = Specification.where(AttractionSpecification.withCountryId(context.countryId))
          .and(AttractionSpecification.withStateCode(context.stateId))
          .and(AttractionSpecification.withCityCode(context.cityId))
          .and(AttractionSpecification.withCategoryCode(context.categoryId));

      List<Attraction> attractions = attractionRepository.findAll(spec);
      assumeTrue(!attractions.isEmpty(), "해당 조건에 맞는 테스트 데이터가 없습니다.");

      // 첫 번째 여행지의 리뷰 정보 가져오기
      Integer testAttractionId = attractions.get(0).getAttractionId();
      Long reviewCount = reviewRepository.countByAttractionId(testAttractionId);
      Double reviewAvgScore = reviewRepository.getAverageScoreByAttractionId(testAttractionId);

      // API 호출 및 검증
      mockMvc.perform(get("/api/attraction")
              .param("countryId", context.countryId.toString())
              .param("stateId", context.stateId.toString())
              .param("cityId", context.cityId.toString())
              .param("categoryId", context.categoryId.toString()))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
          .andExpect(jsonPath("$[0].attractionId", notNullValue()))
          .andExpect(jsonPath("$[0].attractionName", notNullValue()));
    }

    @Test
    @DisplayName("국가, 시도로 여행지 목록 조회")
    void getAttractionListWithPartialParams() throws Exception {
      Specification<Attraction> spec = Specification.where(AttractionSpecification.withCountryId(context.countryId))
          .and(AttractionSpecification.withStateCode(context.stateId));

      List<Attraction> attractions = attractionRepository.findAll(spec);
      assumeTrue(!attractions.isEmpty(), "해당 조건에 맞는 테스트 데이터가 없습니다.");

      // API 호출 및 검증
      mockMvc.perform(get("/api/attraction")
              .param("countryId", context.countryId.toString())
              .param("stateId", context.stateId.toString()))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
          .andExpect(jsonPath("$[0].attractionId", notNullValue()))
          .andExpect(jsonPath("$[0].attractionName", notNullValue()));
    }

    @Test
    @DisplayName("카테고리로 여행지 목록 조회")
    void getAttractionListByCategory() throws Exception {
      Specification<Attraction> spec = Specification.where(AttractionSpecification.withCountryId(context.countryId))
          .and(AttractionSpecification.withCategoryCode(context.categoryId));

      List<Attraction> attractions = attractionRepository.findAll(spec);
      assumeTrue(!attractions.isEmpty(), "해당 조건에 맞는 테스트 데이터가 없습니다.");

      // API 호출 및 검증
      mockMvc.perform(get("/api/attraction")
              .param("countryId", context.countryId.toString())
              .param("categoryId", context.categoryId.toString()))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
          .andExpect(jsonPath("$[0].attractionId", notNullValue()))
          .andExpect(jsonPath("$[0].attractionName", notNullValue()));
    }
  }

  @Nested
  @DisplayName("여행지 페이징 및 검색 테스트")
  class AttractionPagingTests {
    @Test
    @DisplayName("페이징 API 테스트")
    void getAttractionListPaging() throws Exception {
      mockMvc.perform(get("/api/attraction/page")
              .param("countryId", context.countryId.toString())
              .param("size", "5")
              .param("page", "0"))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.content", notNullValue()))
          .andExpect(jsonPath("$.pageable", notNullValue()))
          .andExpect(jsonPath("$.totalElements", greaterThanOrEqualTo(1)));
    }

    @Test
    @DisplayName("검색어와 페이징을 포함한 API 테스트")
    void getAttractionListWithKeyword() throws Exception {
      // 검색할 여행지 이름 가져오기
      Specification<Attraction> spec = Specification.where(AttractionSpecification.withCountryId(context.countryId));
      List<Attraction> attractions = attractionRepository.findAll(spec);
      assumeTrue(!attractions.isEmpty(), "테스트 데이터가 없습니다.");

      // 첫 번째 여행지 이름의 일부를 검색어로 사용
      String attractionName = attractions.get(0).getAttractionName();
      String keyword = attractionName.length() > 2 ? attractionName.substring(0, 2) : attractionName;

      // API 호출 및 검증
      mockMvc.perform(get("/api/attraction/page")
              .param("countryId", context.countryId.toString())
              .param("keyword", keyword)
              .param("size", "5")
              .param("page", "0"))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.content", notNullValue()))
          .andExpect(jsonPath("$.pageable", notNullValue()))
          .andExpect(jsonPath("$.totalElements", greaterThanOrEqualTo(0)));
    }
  }

  @Nested
  @DisplayName("여행지 상세 정보 조회 테스트")
  class AttractionDetailTests {
    @Test
    @DisplayName("여행지 상세 정보 조회")
    void getAttractionDetail() throws Exception {
      // API 호출 및 검증
      mockMvc.perform(get("/api/attraction/{attractionId}", context.attractionId))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.attractionId", equalTo(context.attractionId)))
          .andExpect(jsonPath("$.attractionName", notNullValue()))
          .andExpect(jsonPath("$.categoryName", notNullValue()))
          .andExpect(jsonPath("$.countryName", notNullValue()))
          .andExpect(jsonPath("$.stateName", notNullValue()))
          .andExpect(jsonPath("$.cityName", notNullValue()));
    }
  }
}