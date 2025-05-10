package com.ssafy.where2meow.attraction.controller;

import com.ssafy.where2meow.attraction.entity.Attraction;
import com.ssafy.where2meow.attraction.repository.AttractionRepository;
import com.ssafy.where2meow.attraction.repository.specification.AttractionSpecification;
import com.ssafy.where2meow.review.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AttractionControllerIntegrationTest {

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
    private Integer attractionId;
    private Integer categoryId;
    private Map<Integer, String> categoryNames;
    private String searchKeyword;

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
        attractionId = firstAttraction.getAttractionId();
        categoryId = firstAttraction.getAttractionCategory().getAttractionCategoryId();
        
        // 검색어 생성 (첫 번째 여행지 이름에서 첫 두 글자 사용)
        String attractionName = firstAttraction.getAttractionName();
        searchKeyword = attractionName.length() > 2 ? attractionName.substring(0, 2) : attractionName;

        // 카테고리별 매핑 생성
        categoryNames = attractions.stream()
                .collect(Collectors.toMap(
                        a -> a.getAttractionCategory().getAttractionCategoryId(),
                        a -> a.getAttractionCategory().getAttractionCategoryName(),
                        (existing, replacement) -> existing
                ));
    }

    @Test
    @DisplayName("국가 ID로 여행지 목록 조회 테스트")
    void getAttractionListByCountryIdTest() throws Exception {
        // 국가별 여행지 확인
        Specification<Attraction> spec = Specification.where(AttractionSpecification.withCountryId(countryId));
        List<Attraction> attractions = attractionRepository.findAll(spec);
        assumeTrue(!attractions.isEmpty(), "해당 국가에 여행지가 없습니다.");
        
        // 첫 번째 여행지의 리뷰 정보 가져오기
        Integer testAttractionId = attractions.get(0).getAttractionId();
        Long reviewCount = reviewRepository.countByAttractionId(testAttractionId);
        Double reviewAvgScore = reviewRepository.getAverageScoreByAttractionId(testAttractionId);
        
        mockMvc.perform(get("/api/attraction")
                        .param("countryId", countryId.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].attractionId", notNullValue()))
                .andExpect(jsonPath("$[0].attractionName", notNullValue()))
                .andExpect(jsonPath("$[0].reviewCount", notNullValue()))
                .andExpect(jsonPath("$[0].reviewAvgScore", anyOf(nullValue(), notNullValue())))
                .andExpect(jsonPath("$[0].stateName", notNullValue()))
                .andExpect(jsonPath("$[0].cityName", notNullValue()));
    }

    @Test
    @DisplayName("국가, 시도 ID로 여행지 목록 조회 테스트")
    void getAttractionListByCountryAndStateIdTest() throws Exception {
        // 국가, 시도 조건 여행지 확인
        Specification<Attraction> spec = Specification.where(AttractionSpecification.withCountryId(countryId))
            .and(AttractionSpecification.withStateId(stateId));
        
        List<Attraction> stateAttractions = attractionRepository.findAll(spec);
        assumeTrue(!stateAttractions.isEmpty(), "해당 시도에 여행지가 없습니다.");

        // 첫 번째 여행지의 리뷰 정보
        Integer testAttractionId = stateAttractions.get(0).getAttractionId();
        Long reviewCount = reviewRepository.countByAttractionId(testAttractionId);
        
        mockMvc.perform(get("/api/attraction")
                        .param("countryId", countryId.toString())
                        .param("stateId", stateId.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].stateName", equalTo(stateAttractions.get(0).getState().getStateName())));
    }

    @Test
    @DisplayName("국가, 카테고리 ID로 여행지 목록 조회 테스트")
    void getAttractionListByCountryAndCategoryIdTest() throws Exception {
        // 국가, 카테고리 조건 여행지 확인
        Specification<Attraction> spec = Specification.where(AttractionSpecification.withCountryId(countryId))
            .and(AttractionSpecification.withCategoryId(categoryId));
        
        List<Attraction> categoryAttractions = attractionRepository.findAll(spec);
        assumeTrue(!categoryAttractions.isEmpty(), "해당 카테고리에 여행지가 없습니다.");

        // 첫 번째 여행지의 리뷰 정보
        Integer testAttractionId = categoryAttractions.get(0).getAttractionId();
        Long reviewCount = reviewRepository.countByAttractionId(testAttractionId);
        
        mockMvc.perform(get("/api/attraction")
                        .param("countryId", countryId.toString())
                        .param("categoryId", categoryId.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].attractionId", notNullValue()));
    }

    @Test
    @DisplayName("모든 파라미터로 여행지 목록 조회 테스트")
    void getAttractionListWithAllParamsTest() throws Exception {
        // 모든 조건 여행지 확인
        Specification<Attraction> spec = Specification.where(AttractionSpecification.withCountryId(countryId))
            .and(AttractionSpecification.withStateId(stateId))
            .and(AttractionSpecification.withCityId(cityId))
            .and(AttractionSpecification.withCategoryId(categoryId));
        
        List<Attraction> fullFilteredAttractions = attractionRepository.findAll(spec);
        
        // 데이터가 없으면 테스트를 건너뜀
        assumeTrue(!fullFilteredAttractions.isEmpty(), "해당 조건에 맞는 여행지가 없습니다.");

        // 첫 번째 여행지의 리뷰 정보
        Integer testAttractionId = fullFilteredAttractions.get(0).getAttractionId();
        Long reviewCount = reviewRepository.countByAttractionId(testAttractionId);
        Double reviewAvgScore = reviewRepository.getAverageScoreByAttractionId(testAttractionId);
        
        mockMvc.perform(get("/api/attraction")
                        .param("countryId", countryId.toString())
                        .param("stateId", stateId.toString())
                        .param("cityId", cityId.toString())
                        .param("categoryId", categoryId.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].attractionId", equalTo(fullFilteredAttractions.get(0).getAttractionId())));
    }
    
    @Test
    @DisplayName("페이징 여행지 목록 조회 테스트")
    void getAttractionListPagingTest() throws Exception {
        // 국가별 여행지 확인
        Specification<Attraction> spec = Specification.where(AttractionSpecification.withCountryId(countryId));
        List<Attraction> attractions = attractionRepository.findAll(spec);
        assumeTrue(!attractions.isEmpty(), "해당 국가에 여행지가 없습니다.");
        
        // 페이징된 결과 테스트
        mockMvc.perform(get("/api/attraction/page")
                        .param("countryId", countryId.toString())
                        .param("size", "5")
                        .param("page", "0"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", isA(java.util.List.class)))
                .andExpect(jsonPath("$.pageable", notNullValue()))
                .andExpect(jsonPath("$.totalPages", notNullValue()))
                .andExpect(jsonPath("$.totalElements", notNullValue()))
                .andExpect(jsonPath("$.size", equalTo(5)))
                .andExpect(jsonPath("$.number", equalTo(0)));
    }
    
    @Test
    @DisplayName("검색어 필터와 페이징 테스트")
    void getAttractionListWithKeywordAndPagingTest() throws Exception {
        // 검색어와 페이징 테스트
        mockMvc.perform(get("/api/attraction/page")
                        .param("countryId", countryId.toString())
                        .param("keyword", searchKeyword)
                        .param("size", "5")
                        .param("page", "0"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", isA(java.util.List.class)))
                .andExpect(jsonPath("$.pageable", notNullValue()));
    }

    @Test
    @DisplayName("여행지 상세 정보 조회 테스트")
    void getAttractionDetailTest() throws Exception {
        // 여행지 상세 정보 가져오기
        Attraction attraction = attractionRepository.findByIdWithDetails(attractionId)
                .orElseThrow(() -> new RuntimeException("여행지 상세 정보를 찾을 수 없습니다."));

        mockMvc.perform(get("/api/attraction/{attractionId}", attractionId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.attractionId", equalTo(attractionId)))
                .andExpect(jsonPath("$.attractionName", equalTo(attraction.getAttractionName())))
                .andExpect(jsonPath("$.firstImage1", equalTo(attraction.getFirstImage1())))
                .andExpect(jsonPath("$.latitude", notNullValue()))
                .andExpect(jsonPath("$.longitude", notNullValue()))
                .andExpect(jsonPath("$.categoryName", equalTo(attraction.getAttractionCategory().getAttractionCategoryName())))
                .andExpect(jsonPath("$.countryName", equalTo(attraction.getCountry().getCountryName())))
                .andExpect(jsonPath("$.stateName", equalTo(attraction.getState().getStateName())))
                .andExpect(jsonPath("$.cityName", equalTo(attraction.getCity().getCityName())));
    }
}
