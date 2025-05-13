package com.ssafy.where2meow.board.dto;

import com.ssafy.where2meow.board.entity.Board;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardDetailResponse {
    private int boardId;
    private int userId;
    private String username; // 작성자 이름
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
    
    private List<CommentResponse> comments;
    
    public static BoardDetailResponse fromBoard(Board board, String username, String categoryName, 
                                              int likeCount, boolean isLiked, boolean isBookmarked, 
                                              List<CommentResponse> comments) {
        return BoardDetailResponse.builder()
                .boardId(board.getBoardId())
                .userId(board.getUserId())
                .username(username)
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
                .comments(comments)
                .build();
    }
}
