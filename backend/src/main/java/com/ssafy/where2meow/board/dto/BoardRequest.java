package com.ssafy.where2meow.board.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardRequest {
    private String title;
    private String content;
    private int categoryId;
}
