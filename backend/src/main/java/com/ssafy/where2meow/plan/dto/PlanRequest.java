package com.ssafy.where2meow.plan.dto;

import lombok.*;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlanRequest {
    private String title;
    private String content;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean isPublic;

    private List<PlanAttractionRequest> attractions;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlanAttractionRequest {
        private int attractionId;
        private String content;
        private LocalDate date;
        private int attractionOrder;
    }
}
