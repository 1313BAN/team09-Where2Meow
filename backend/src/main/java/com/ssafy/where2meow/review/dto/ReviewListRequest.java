package com.ssafy.where2meow.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewListRequest {
    private Integer attractionId;
    private Integer page;
    private Integer size;
    private String sortBy;
    private String sortOrder;
}
