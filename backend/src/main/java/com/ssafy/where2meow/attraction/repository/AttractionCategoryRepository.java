package com.ssafy.where2meow.attraction.repository;

import com.ssafy.where2meow.attraction.entity.AttractionCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttractionCategoryRepository extends JpaRepository<AttractionCategory, Integer> {
}
