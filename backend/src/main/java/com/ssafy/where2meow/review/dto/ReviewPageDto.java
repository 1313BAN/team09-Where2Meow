package com.ssafy.where2meow.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewPageDto {
    private List<ReviewDto> content;
    private int pageNumber;
    private int size;
    private int totalPages;
    private long totalElements;
    private boolean first;
    private boolean last;
    
    /**
     * Page<Review>에서 ReviewPageDto를 생성
     * 
     * @param reviewPage 리뷰 페이지
     * @param reviewDtos 변환된 ReviewDto 리스트
     * @return ReviewPageDto 객체
     */
    public static ReviewPageDto fromPage(Page<?> reviewPage, List<ReviewDto> reviewDtos) {
        return ReviewPageDto.builder()
                .content(reviewDtos)
                .pageNumber(reviewPage.getNumber())
                .size(reviewPage.getSize())
                .totalPages(reviewPage.getTotalPages())
                .totalElements(reviewPage.getTotalElements())
                .first(reviewPage.isFirst())
                .last(reviewPage.isLast())
                .build();
    }
}
