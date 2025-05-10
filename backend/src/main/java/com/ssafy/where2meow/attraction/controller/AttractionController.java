package com.ssafy.where2meow.attraction.controller;

import com.ssafy.where2meow.attraction.dto.AttractionDetailResponse;
import com.ssafy.where2meow.attraction.dto.AttractionListResponse;
import com.ssafy.where2meow.attraction.service.AttractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attrection")
@RequiredArgsConstructor
public class AttractionController {

    private final AttractionService attractionService;

    /**
     * 시군구/카테고리별 여행지 조회 API
     * 
     * @param countryId 국가 ID (필수)
     * @param stateId 시도 ID (선택)
     * @param cityId 시군구 ID (선택)
     * @param categoryId 카테고리 ID (선택)
     * @return 여행지 리스트 (여행지 이름, 이미지, 리뷰 수, 리뷰 평점 평균, 시도 이름, 시군구 이름 포함)
     */
    @GetMapping
    public ResponseEntity<List<AttractionListResponse>> getAttractionList(
            @RequestParam(required = true) Integer countryId,
            @RequestParam(required = false) Integer stateId,
            @RequestParam(required = false) Integer cityId,
            @RequestParam(required = false) Integer categoryId) {
        
        List<AttractionListResponse> attractions = attractionService.getAttractionList(countryId, stateId, cityId, categoryId);
        return ResponseEntity.ok(attractions);
    }

    /**
     * 여행지 상세 정보 조회 API
     * 
     * @param attractionId 여행지 ID
     * @return 여행지 상세 정보
     */
    @GetMapping("/{attractionId}")
    public ResponseEntity<AttractionDetailResponse> getAttractionDetail(
            @PathVariable Integer attractionId) {
        
        AttractionDetailResponse attraction = attractionService.getAttractionDetail(attractionId);
        return ResponseEntity.ok(attraction);
    }
}
