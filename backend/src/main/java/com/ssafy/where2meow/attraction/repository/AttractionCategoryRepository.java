package com.ssafy.where2meow.attraction.repository;

import com.ssafy.where2meow.attraction.entity.AttractionCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AttractionCategoryRepository extends JpaRepository<AttractionCategory, Integer> {

  // 카테고리 ID 목록으로 조회
  @Query("SELECT ac FROM AttractionCategory ac WHERE ac.attractionCategoryId IN :categoryIds")
  List<AttractionCategory> findByAttractionCategoryIdIn(@Param("categoryIds") List<Integer> categoryIds);

  // 모든 카테고리 조회 (프론트에서 카테고리 목록 표시용)
  @Query("SELECT ac FROM AttractionCategory ac ORDER BY ac.attractionCategoryName ASC")
  List<AttractionCategory> findAllOrderByName();
}
