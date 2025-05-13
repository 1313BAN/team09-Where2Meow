package com.ssafy.where2meow.comment.service;

import com.ssafy.where2meow.comment.dto.CommentRequest;
import com.ssafy.where2meow.comment.entity.Comment;
import com.ssafy.where2meow.comment.repository.CommentLikeRepository;
import com.ssafy.where2meow.comment.repository.CommentRepository;
import com.ssafy.where2meow.common.util.UuidUserUtil;
import com.ssafy.where2meow.exception.EntityNotFoundException;
import com.ssafy.where2meow.exception.ForbiddenAccessException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;

    private final UuidUserUtil uuidUserUtil;

    // 댓글 추가
    @Transactional
    public Comment createComment(CommentRequest commentRequest, UUID uuid) {
        Integer userId = uuidUserUtil.getRequiredUserId(uuid);
        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setBoardId(commentRequest.getBoardId());
        comment.setContent(commentRequest.getContent());
        comment.setUpdatedAt(LocalDateTime.now());

        return commentRepository.save(comment);
    }

    // 댓글 수정
    @Transactional
    public Comment updateComment(int commentId, CommentRequest commentRequest, UUID uuid) {
        Integer userId = uuidUserUtil.getRequiredUserId(uuid);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment", "commentId", commentId));

        checkCommentOwnership(comment, userId);

        comment.setContent(commentRequest.getContent());
        comment.setUpdatedAt(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(int commentId, UUID uuid) {
        Integer userId = uuidUserUtil.getRequiredUserId(uuid);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment", "commentId", commentId));

        checkCommentOwnership(comment, userId);

        commentLikeRepository.deleteByCommentId(commentId);
        commentRepository.delete(comment);
    }

    private void checkCommentOwnership(Comment comment, Integer userId) {
        if (comment.getUserId() != userId) {
            throw new ForbiddenAccessException("이 댓글에 대한 권한이 없습니다");
        }
    }

}
