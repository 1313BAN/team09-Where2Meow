package com.ssafy.where2meow.attraction.controller;

import com.ssafy.where2meow.Review.repository.ReviewRepository;
import com.ssafy.where2meow.attraction.repository.AttractionRepository;
import com.ssafy.where2meow.attraction.entity.Attraction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
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

    @MockBean
    private ReviewRepository reviewRepository;

    @Test
    @DisplayName("모든 파라미터(국가, 시도, 시군구, 카테고리)로 여행지 목록 조회 테스트")
    void getAttractionListWithAllParamsTest() throws Exception {
        // 리뷰 정보 모킹
        when(reviewRepository.countByAttractionId(any())).thenReturn(10L);
        when(reviewRepository.getAverageScoreByAttractionId(any())).thenReturn(4.5);
        
        // 실제 DB에서 데이터 찾기
        List<Attraction> attractions = attractionRepository.findByCountryIdAndStateIdAndCityIdAndCategoryId(1, 1, 1, 12);
        
        // 테스트할 데이터가 있는지 확인
        if (attractions.isEmpty()) {
            // 테스트 데이터가 없으면 테스트 스킵
            System.out.println("테스트 데이터가 없어서 테스트를 건너뜁니다.");
            return;
        }
        
        // 첫 번째 여행지 정보 가져오기
        Attraction testAttraction = attractions.get(0);
        Integer countryId = testAttraction.getCountry().getCountryId();
        Integer stateId = testAttraction.getState().getStateId();
        Integer cityId = testAttraction.getCity().getCityId();
        Integer categoryId = testAttraction.getAttractionCategory().getAttractionCategoryId();
        
        // API 호출 및 검증
        mockMvc.perform(get("/api/attrection")
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
                .andExpect(jsonPath("$[0].reviewCount", equalTo(10)))
                .andExpect(jsonPath("$[0].reviewAvgScore", equalTo(4.5)))
                .andExpect(jsonPath("$[0].stateName", notNullValue()))
                .andExpect(jsonPath("$[0].cityName", notNullValue()));
    }

    @Test
    @DisplayName("일부 파라미터(국가, 시도)로 여행지 목록 조회 테스트")
    void getAttractionListWithPartialParamsTest() throws Exception {
        // 리뷰 정보 모킹
        when(reviewRepository.countByAttractionId(any())).thenReturn(10L);
        when(reviewRepository.getAverageScoreByAttractionId(any())).thenReturn(4.5);
        
        // 실제 DB에서 데이터 찾기
        List<Attraction> attractions = attractionRepository.findByCountryIdAndStateId(1, 1);
        
        // 테스트할 데이터가 있는지 확인
        if (attractions.isEmpty()) {
            // 테스트 데이터가 없으면 테스트 스킵
            System.out.println("테스트 데이터가 없어서 테스트를 건너뜁니다.");
            return;
        }
        
        // 첫 번째 여행지 정보 가져오기
        Attraction testAttraction = attractions.get(0);
        Integer countryId = testAttraction.getCountry().getCountryId();
        Integer stateId = testAttraction.getState().getStateId();
        
        // API 호출 및 검증
        mockMvc.perform(get("/api/attrection")
                        .param("countryId", countryId.toString())
                        .param("stateId", stateId.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].attractionId", notNullValue()))
                .andExpect(jsonPath("$[0].attractionName", notNullValue()))
                .andExpect(jsonPath("$[0].reviewCount", equalTo(10)))
                .andExpect(jsonPath("$[0].reviewAvgScore", equalTo(4.5)))
                .andExpect(jsonPath("$[0].stateName", equalTo(testAttraction.getState().getStateName())));
    }

    @Test
    @DisplayName("카테고리 파라미터로 여행지 목록 조회 테스트")
    void getAttractionListByCategoryTest() throws Exception {
        // 리뷰 정보 모킹
        when(reviewRepository.countByAttractionId(any())).thenReturn(10L);
        when(reviewRepository.getAverageScoreByAttractionId(any())).thenReturn(4.5);
        
        // 첫 번째 카테고리 ID 찾기 (예: 자연관광지)
        List<Attraction> attractionsByCategory = attractionRepository.findByCountryIdAndCategoryId(1, 15);
        
        // 테스트할 데이터가 있는지 확인
        if (attractionsByCategory.isEmpty()) {
            // 테스트 데이터가 없으면 다른 카테고리 시도
            attractionsByCategory = attractionRepository.findByCountryIdAndCategoryId(1, 12);
            if (attractionsByCategory.isEmpty()) {
                System.out.println("테스트 데이터가 없어서 테스트를 건너뜁니다.");
                return;
            }
        }
        
        Attraction testAttraction = attractionsByCategory.get(0);
        Integer countryId = testAttraction.getCountry().getCountryId();
        Integer categoryId = testAttraction.getAttractionCategory().getAttractionCategoryId();
        
        // API 호출 및 검증
        mockMvc.perform(get("/api/attrection")
                        .param("countryId", countryId.toString())
                        .param("categoryId", categoryId.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].attractionId", notNullValue()))
                .andExpect(jsonPath("$[0].attractionName", notNullValue()))
                .andExpect(jsonPath("$[0].reviewCount", equalTo(10)));
    }

    @Test
    @DisplayName("여행지 상세 정보 조회 테스트")
    void getAttractionDetailTest() throws Exception {
        // 첫 번째 여행지 ID 찾기
        List<Attraction> attractions = attractionRepository.findByCountryId(1);
        
        if (attractions.isEmpty()) {
            System.out.println("테스트 데이터가 없어서 테스트를 건너뜁니다.");
            return;
        }
        
        Integer attractionId = attractions.get(0).getAttractionId();
        
        // 해당 여행지 상세 정보 가져오기
        Optional<Attraction> attractionOptional = attractionRepository.findByIdWithDetails(attractionId);
        
        if (attractionOptional.isEmpty()) {
            System.out.println("상세 정보를 가져올 수 없어서 테스트를 건너뜁니다.");
            return;
        }
        
        Attraction attraction = attractionOptional.get();
        
        // API 호출 및 검증
        mockMvc.perform(get("/api/attrection/{attractionId}", attractionId))
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
