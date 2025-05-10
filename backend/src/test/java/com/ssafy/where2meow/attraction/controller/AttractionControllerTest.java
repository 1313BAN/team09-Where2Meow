package com.ssafy.where2meow.attraction.controller;

import com.ssafy.where2meow.attraction.entity.Attraction;
import com.ssafy.where2meow.attraction.repository.AttractionRepository;
import com.ssafy.where2meow.attraction.repository.specification.AttractionSpecification;
import com.ssafy.where2meow.review.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
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
    
    // 테스트에 사용할 파라미터 값
    private Integer countryId;
    private Integer stateId;
    private Integer cityId;
    private Integer categoryId;
    private Integer attractionId;

    @BeforeEach
    void setup() {
        // DB에서 조회하여 실제 존재하는 데이터로 테스트 수행
        List<Attraction> attractions = attractionRepository.findAll();
        assumeTrue(!attractions.isEmpty(), "테스트 데이터가 없습니다.");

        // 첫 번째 여행지 정보 가져오기
        Attraction firstAttraction = attractions.get(0);
        countryId = firstAttraction.getCountry().getCountryId();
        stateId = firstAttraction.getState().getStateId();
        cityId = firstAttraction.getCity().getCityId();
        categoryId = firstAttraction.getAttractionCategory().getAttractionCategoryId();
        attractionId = firstAttraction.getAttractionId();
    }

    @Test
    @DisplayName("모든 파라미터(국가, 시도, 시군구, 카테고리)로 여행지 목록 조회 테스트")
    void getAttractionListWithAllParamsTest() throws Exception {
        // 해당 조건에 맞는 여행지 확인
        Specification<Attraction> spec = Specification.where(AttractionSpecification.withCountryId(countryId))
            .and(AttractionSpecification.withStateId(stateId))
            .and(AttractionSpecification.withCityId(cityId))
            .and(AttractionSpecification.withCategoryId(categoryId));
        
        List<Attraction> attractions = attractionRepository.findAll(spec);
        
        // 테스트할 데이터가 있는지 확인
        assumeTrue(!attractions.isEmpty(), "해당 조건에 맞는 테스트 데이터가 없습니다.");
        
        // 첫 번째 여행지의 리뷰 정보 가져오기
        Integer testAttractionId = attractions.get(0).getAttractionId();
        Long reviewCount = reviewRepository.countByAttractionId(testAttractionId);
        Double reviewAvgScore = reviewRepository.getAverageScoreByAttractionId(testAttractionId);
        
        // API 호출 및 검증
        mockMvc.perform(get("/api/attraction")
                        .param("countryId", countryId.toString())
                        .param("stateId", stateId.toString())
                        .param("cityId", cityId.toString())
                        .param("categoryId", categoryId.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].attractionId", notNullValue()))
                .andExpect(jsonPath("$[0].attractionName", notNullValue()))
                .andExpect(jsonPath("$[0].reviewCount", equalTo(reviewCount.intValue())))
                .andExpect(jsonPath("$[0].reviewAvgScore", anyOf(
                        equalTo(reviewAvgScore),
                        equalTo(reviewAvgScore == null ? null : reviewAvgScore.doubleValue())
                )))
                .andExpect(jsonPath("$[0].stateName", notNullValue()))
                .andExpect(jsonPath("$[0].cityName", notNullValue()));
    }

    @Test
    @DisplayName("일부 파라미터(국가, 시도)로 여행지 목록 조회 테스트")
    void getAttractionListWithPartialParamsTest() throws Exception {
        // 국가, 시도 조건에 맞는 여행지 확인
        Specification<Attraction> spec = Specification.where(AttractionSpecification.withCountryId(countryId))
            .and(AttractionSpecification.withStateId(stateId));
        
        List<Attraction> attractions = attractionRepository.findAll(spec);
        
        // 테스트할 데이터가 있는지 확인
        assumeTrue(!attractions.isEmpty(), "해당 조건에 맞는 테스트 데이터가 없습니다.");
        
        // 첫 번째 여행지의 리뷰 정보 가져오기
        Integer testAttractionId = attractions.get(0).getAttractionId();
        Long reviewCount = reviewRepository.countByAttractionId(testAttractionId);
        Double reviewAvgScore = reviewRepository.getAverageScoreByAttractionId(testAttractionId);
        
        // API 호출 및 검증
        mockMvc.perform(get("/api/attraction")
                        .param("countryId", countryId.toString())
                        .param("stateId", stateId.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].attractionId", notNullValue()))
                .andExpect(jsonPath("$[0].attractionName", notNullValue()))
                .andExpect(jsonPath("$[0].reviewCount", notNullValue()))
                .andExpect(jsonPath("$[0].reviewAvgScore", anyOf(nullValue(), notNullValue())))
                .andExpect(jsonPath("$[0].stateName", notNullValue()));
    }

    @Test
    @DisplayName("카테고리 파라미터로 여행지 목록 조회 테스트")
    void getAttractionListByCategoryTest() throws Exception {
        // 카테고리 조건에 맞는 여행지 확인
        Specification<Attraction> spec = Specification.where(AttractionSpecification.withCountryId(countryId))
            .and(AttractionSpecification.withCategoryId(categoryId));
        
        List<Attraction> attractions = attractionRepository.findAll(spec);
        
        // 테스트할 데이터가 있는지 확인
        assumeTrue(!attractions.isEmpty(), "해당 조건에 맞는 테스트 데이터가 없습니다.");
        
        // 첫 번째 여행지의 리뷰 정보 가져오기
        Integer testAttractionId = attractions.get(0).getAttractionId();
        Long reviewCount = reviewRepository.countByAttractionId(testAttractionId);
        
        // API 호출 및 검증
        mockMvc.perform(get("/api/attraction")
                        .param("countryId", countryId.toString())
                        .param("categoryId", categoryId.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].attractionId", notNullValue()))
                .andExpect(jsonPath("$[0].attractionName", notNullValue()))
                .andExpect(jsonPath("$[0].reviewCount", notNullValue()));
    }

    @Test
    @DisplayName("페이징 API 테스트")
    void getAttractionListPagingTest() throws Exception {
        // 페이징 API 호출 및 검증
        mockMvc.perform(get("/api/attraction/page")
                        .param("countryId", countryId.toString())
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
    void getAttractionListWithKeywordTest() throws Exception {
        // 검색할 여행지 이름 가져오기
        List<Attraction> attractions = attractionRepository.findAll(Specification.where(AttractionSpecification.withCountryId(countryId)));
        assumeTrue(!attractions.isEmpty(), "테스트 데이터가 없습니다.");
        
        // 첫 번째 여행지 이름의 일부를 검색어로 사용
        String attractionName = attractions.get(0).getAttractionName();
        String keyword = attractionName.length() > 2 ? attractionName.substring(0, 2) : attractionName;
        
        // API 호출 및 검증
        mockMvc.perform(get("/api/attraction/page")
                        .param("countryId", countryId.toString())
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

    @Test
    @DisplayName("여행지 상세 정보 조회 테스트")
    void getAttractionDetailTest() throws Exception {
        // 여행지 상세 정보 가져오기
        Optional<Attraction> attractionOptional = attractionRepository.findByIdWithDetails(attractionId);
        assumeTrue(attractionOptional.isPresent(), "상세 정보를 가져올 수 없습니다.");
        
        Attraction attraction = attractionOptional.get();
        
        // API 호출 및 검증
        mockMvc.perform(get("/api/attraction/{attractionId}", attractionId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.attractionId", equalTo(attractionId)))
                .andExpect(jsonPath("$.attractionName", equalTo(attraction.getAttractionName())))
                .andExpect(jsonPath("$.categoryName", equalTo(attraction.getAttractionCategory().getAttractionCategoryName())))
                .andExpect(jsonPath("$.countryName", equalTo(attraction.getCountry().getCountryName())))
                .andExpect(jsonPath("$.stateName", equalTo(attraction.getState().getStateName())))
                .andExpect(jsonPath("$.cityName", equalTo(attraction.getCity().getCityName())));
    }
}
