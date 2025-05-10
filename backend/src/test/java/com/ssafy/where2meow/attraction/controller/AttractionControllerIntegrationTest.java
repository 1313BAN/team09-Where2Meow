package com.ssafy.where2meow.attraction.controller;

import com.ssafy.where2meow.attraction.entity.Attraction;
import com.ssafy.where2meow.attraction.repository.AttractionRepository;
import com.ssafy.where2meow.address.entity.Country;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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

    // 테스트에 사용할 파라미터 값
    private Integer countryId;
    private Integer stateId;
    private Integer cityId;
    private Integer attractionId;
    private Integer categoryId;
    private Map<Integer, String> categoryNames;

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
        // when & then
        mockMvc.perform(get("/api/attrection")
                        .param("countryId", countryId.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].attractionId", notNullValue()))
                .andExpect(jsonPath("$[0].attractionName", notNullValue()))
                .andExpect(jsonPath("$[0].reviewCount", notNullValue()))
                .andExpect(jsonPath("$[0].reviewAvgScore", anyOf(nullValue(), notNullValue())))  // 리뷰가 없을 수도 있음
                .andExpect(jsonPath("$[0].stateName", notNullValue()))
                .andExpect(jsonPath("$[0].cityName", notNullValue()));
    }

    @Test
    @DisplayName("국가, 시도 ID로 여행지 목록 조회 테스트")
    void getAttractionListByCountryAndStateIdTest() throws Exception {
        // 해당 시도에 여행지가 있는지 확인
        List<Attraction> stateAttractions = attractionRepository.findByCountryIdAndStateId(countryId, stateId);
        assumeTrue(!stateAttractions.isEmpty(), "해당 시도에 여행지가 없습니다.");

        // when & then
        mockMvc.perform(get("/api/attrection")
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
        // 해당 카테고리에 여행지가 있는지 확인
        List<Attraction> categoryAttractions = attractionRepository.findByCountryIdAndCategoryId(countryId, categoryId);
        assumeTrue(!categoryAttractions.isEmpty(), "해당 카테고리에 여행지가 없습니다.");

        String expectedCategoryName = categoryNames.get(categoryId);

        // when & then
        mockMvc.perform(get("/api/attrection")
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
        // 해당 조건에 맞는 여행지가 있는지 확인
        List<Attraction> fullFilteredAttractions = attractionRepository.findByCountryIdAndStateIdAndCityIdAndCategoryId(
                countryId, stateId, cityId, categoryId);
        
        // 데이터가 없으면 테스트를 건너뜀
        assumeTrue(!fullFilteredAttractions.isEmpty(), "해당 조건에 맞는 여행지가 없습니다.");

        // when & then
        mockMvc.perform(get("/api/attrection")
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
    @DisplayName("여행지 상세 정보 조회 테스트")
    void getAttractionDetailTest() throws Exception {
        // 여행지 상세 정보 가져오기
        Attraction attraction = attractionRepository.findByIdWithDetails(attractionId)
                .orElseThrow(() -> new RuntimeException("여행지 상세 정보를 찾을 수 없습니다."));

        // when & then
        mockMvc.perform(get("/api/attrection/{attractionId}", attractionId))
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
