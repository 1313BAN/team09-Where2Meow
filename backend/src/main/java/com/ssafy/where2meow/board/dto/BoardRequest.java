package com.ssafy.where2meow.board.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @NotNull
    private int categoryId;

}
