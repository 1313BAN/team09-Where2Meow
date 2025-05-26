package com.ssafy.where2meow.attraction.controller;

import com.ssafy.where2meow.attraction.dto.AttractionCategoryResponse;
import com.ssafy.where2meow.attraction.dto.AttractionDetailResponse;
import com.ssafy.where2meow.attraction.dto.AttractionListResponse;
import com.ssafy.where2meow.attraction.service.AttractionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
   * 검색 로직:
   * 1. country, state, city 순서의 계층 구조를 따름
   * 2. country 없이 state나 city만으로는 검색 불가
   * 3. state 없이 city만으로는 검색 불가
   * 4. 카테고리 단독 또는 키워드 단독으로는 검색 가능
   *
   * @param countryId  국가 ID (계층구조 검색 시 필요)
   * @param stateId    시도 ID (계층구조 검색 시 city와 함께 사용할 경우 필요)
   * @param cityId     시군구 ID (계층구조 검색 시 반드시 country, state와 함께 사용 필요)
   * @param categoryId 카테고리 ID (단독 검색 가능)
   * @param keyword    검색어 (단독 검색 가능)
   * @param pageable   페이징 정보
   * @return 여행지 페이징 정보 (전체 개수, 페이지 정보, 여행지 목록 포함)
   */
  @GetMapping
  public ResponseEntity<Page<AttractionListResponse>> getAttractionListPaging(
      @RequestParam(required = false) Integer countryId,
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
   * 여행지 상세 정보 조회 API
   *
   * @param attractionId 여행지 ID
   * @return 여행지 상세 정보
   */
  @GetMapping("/detail/{attractionId}")
  public ResponseEntity<AttractionListResponse> getAttractionDetail(
      @PathVariable Integer attractionId) {
    AttractionListResponse attraction = attractionService.getAttractionDetail(attractionId);
    return ResponseEntity.ok(attraction);
  }

  /**
   * 모든 카테고리 목록 조회 API
   * @return 카테고리 목록
   */
  @GetMapping("/categories")
  public ResponseEntity<List<
      AttractionCategoryResponse>> getAllCategories() {
    List<AttractionCategoryResponse> categories = attractionService.getAllCategories();
    return ResponseEntity.ok(categories);
  }

}
