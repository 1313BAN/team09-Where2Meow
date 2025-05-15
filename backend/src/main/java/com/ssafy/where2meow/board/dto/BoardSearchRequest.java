package com.ssafy.where2meow.board.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardSearchRequest {
    private String keyword;
    private Integer categoryId;
    private Integer page;
    private Integer size;
}
