package com.ssafy.where2meow.board.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardListResponse {
    private List<BoardResponse> boards;
    private int totalPages;
    private long totalElements;
    private int currentPage;
    private boolean isFirst;
    private boolean isLast;
}
