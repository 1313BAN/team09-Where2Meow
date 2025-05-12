package com.ssafy.where2meow.attraction.dto;

import com.ssafy.where2meow.review.dto.ReviewPageDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttractionDetailResponse {
    private Integer attractionId;
    private Integer contentId;
    private String attractionName;
    private String firstImage1;
    private String firstImage2;
    private Integer mapLevel;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String tel;
    private String addr1;
    private String addr2;
    private String homepage;
    private String overview;
    private String categoryName;
    private String countryName;
    private String stateName;
    private String cityName;
    private ReviewPageDto reviews;
    private Double reviewAvgScore;
}
