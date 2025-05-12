package com.ssafy.where2meow.attraction.controller;

import com.ssafy.where2meow.attraction.dto.AttractionDetailResponse;
import com.ssafy.where2meow.attraction.dto.AttractionListResponse;
import com.ssafy.where2meow.attraction.service.AttractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attraction")
@RequiredArgsConstructor
public class AttractionController {

    private final AttractionService attractionService;

    /**
     * 여행지 페이징 조회 API
     * 
     * @param countryId 국가 ID (필수)
     * @param stateId 시도 ID (선택)
     * @param cityId 시군구 ID (선택)
     * @param categoryId 카테고리 ID (선택)
     * @param keyword 검색어 (선택)
     * @param pageable 페이징 정보
     * @return 여행지 페이징 정보 (전체 개수, 페이지 정보, 여행지 목록 포함)
     */
    @GetMapping
    public ResponseEntity<Page<AttractionListResponse>> getAttractionListPaging(
            @RequestParam(required = true) Integer countryId,
            @RequestParam(required = false) Integer stateId,
            @RequestParam(required = false) Integer cityId,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 10, sort = "attractionName", direction = Sort.Direction.ASC) Pageable pageable) {
        
        Page<AttractionListResponse> attractions = attractionService.getAttractionList(
                countryId, stateId, cityId, categoryId, keyword, pageable);
        return ResponseEntity.ok(attractions);
    }

    /**
     * 시군구/카테고리별 여행지 조회 API - 레거시 호환용
     * 
     * @param countryId 국가 ID (필수)
     * @param stateId 시도 ID (선택)
     * @param cityId 시군구 ID (선택)
     * @param categoryId 카테고리 ID (선택)
     * @return 여행지 리스트 (여행지 이름, 이미지, 리뷰 수, 리뷰 평점 평균, 시도 이름, 시군구 이름 포함)
     */
//    @GetMapping
//    public ResponseEntity<List<AttractionListResponse>> getAttractionList(
//            @RequestParam(required = true) Integer countryId,
//            @RequestParam(required = false) Integer stateId,
//            @RequestParam(required = false) Integer cityId,
//            @RequestParam(required = false) Integer categoryId) {
//
//        List<AttractionListResponse> attractions = attractionService.getAttractionList(countryId, stateId, cityId, categoryId);
//        return ResponseEntity.ok(attractions);
//    }

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
