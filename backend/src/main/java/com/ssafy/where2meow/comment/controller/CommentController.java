package com.ssafy.where2meow.comment.controller;

import com.ssafy.where2meow.comment.dto.CommentRequest;
import com.ssafy.where2meow.comment.entity.Comment;
import com.ssafy.where2meow.comment.service.CommentService;
import com.ssafy.where2meow.common.util.UuidUserUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
@Tag(name = "Comment", description = "댓글 관리 API")
public class CommentController {

    private final CommentService commentService;

    private final UuidUserUtil uuidUserUtil;

    // 내가 쓴 댓글 조회(/user)

    @PostMapping
    @Operation(
            summary = "댓글 추가",
            description = "게시글에 댓글을 추가합니다."
    )
    @ApiResponse(responseCode = "201", description = "댓글 추가 성공")
    public ResponseEntity<Comment> createComment(@RequestBody CommentRequest commentRequest) {
        UUID uuid = uuidUserUtil.getCurrentUserUuid();

        Comment createdComment = commentService.createComment(commentRequest, uuid);
        URI location = URI.create("/api/comment/" + createdComment.getCommentId());
        return ResponseEntity.created(location).body(createdComment);
    }

    @PutMapping("/{commentId}")
    @Operation(
            summary = "댓글 수정",
            description = "댓글을 수정합니다."
    )
    @ApiResponse(responseCode = "200", description = "댓글 수정 성공")
    @ApiResponse(responseCode = "403", description = "댓글 수정 권한 없음")
    @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음")
    public ResponseEntity<Comment> updateComment(
            @PathVariable int commentId,
            @RequestBody CommentRequest commentRequest
    ) {
        UUID uuid = uuidUserUtil.getCurrentUserUuid();

        Comment updatedComment = commentService.updateComment(commentId, commentRequest, uuid);
        return ResponseEntity.ok(updatedComment);
    }

    @DeleteMapping("/{commentId}")
    @Operation(
            summary = "댓글 삭제",
            description = "댓글을 삭제합니다."
    )
    @ApiResponse(responseCode = "204", description = "댓글 삭제 성공")
    @ApiResponse(responseCode = "403", description = "댓글 삭제 권한 없음")
    @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음")
    public ResponseEntity<Void> deleteComment(@PathVariable int commentId) {
        UUID uuid = uuidUserUtil.getCurrentUserUuid();

        commentService.deleteComment(commentId, uuid);
        return ResponseEntity.noContent().build();
    }

    // 댓글 좋아요 추가(/{commentId}/like)

    // 댓글 좋아요 삭제(/{commentId}/like)

}
