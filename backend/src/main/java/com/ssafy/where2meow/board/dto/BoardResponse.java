package com.ssafy.where2meow.board.dto;

import com.ssafy.where2meow.board.entity.Board;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardResponse {
    private int boardId;
    private int userId;
    private int categoryId;
    private String categoryName;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int viewCount;
    
    private int likeCount;
    private boolean isLiked;
    private boolean isBookmarked;
    
    public static BoardResponse fromBoard(Board board, String categoryName, int likeCount, boolean isLiked, boolean isBookmarked) {
        return BoardResponse.builder()
                .boardId(board.getBoardId())
                .userId(board.getUserId())
                .categoryId(board.getCategoryId())
                .categoryName(categoryName)
                .title(board.getTitle())
                .content(board.getContent())
                .createdAt(board.getCreatedAt())
                .updatedAt(board.getUpdatedAt())
                .viewCount(board.getViewCount())
                .likeCount(likeCount)
                .isLiked(isLiked)
                .isBookmarked(isBookmarked)
                .build();
    }
}
