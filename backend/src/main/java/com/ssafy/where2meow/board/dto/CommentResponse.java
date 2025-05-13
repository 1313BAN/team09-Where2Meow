package com.ssafy.where2meow.board.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponse {
    private int commentId;
    private int boardId;
    private int userId;
    private String username; // 사용자 이름
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer parentCommentId;
    private List<CommentResponse> childComments; // 대댓글 목록
}
