package com.ssafy.where2meow.comment.service;

import com.ssafy.where2meow.comment.entity.Comment;
import com.ssafy.where2meow.comment.entity.CommentLike;
import com.ssafy.where2meow.comment.repository.CommentLikeRepository;
import com.ssafy.where2meow.comment.repository.CommentRepository;
import com.ssafy.where2meow.common.util.UuidUserUtil;
import com.ssafy.where2meow.exception.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentLikeService {

    private final CommentService commentService;
    private final CommentLikeRepository commentLikeRepository;

    private final UuidUserUtil uuidUserUtil;
    private final CommentRepository commentRepository;

    // 사용자가 댓글에 좋아요를 눌렀는지 확인
    public boolean hasUserLiked(int commentId, int userId) {
        return commentLikeRepository.existsByCommentIdAndUserId(commentId, userId);
    }

    // 댓글 좋아요 추가
    @Transactional
    public void createLike(int commentId, UUID uuid) {
        Integer userId = uuidUserUtil.getRequiredUserId(uuid);
        if (!hasUserLiked(commentId, userId)) {
            Comment comment = commentRepository.findById(commentId)
                    .orElseThrow(() -> new EntityNotFoundException("Comment", "commentId", commentId));
            CommentLike commentLike = new CommentLike();
            commentLike.setCommentId(commentId);
            commentLike.setUserId(userId);
            commentLikeRepository.save(commentLike);
        }
    }

    // 댓글 좋아요 삭제
    @Transactional
    public void deleteLike(int commentId, UUID uuid) {
        Integer userId = uuidUserUtil.getRequiredUserId(uuid);
        if (!hasUserLiked(commentId, userId)) {
            throw new EntityNotFoundException("CommentLike", "commentId", commentId);
        }
        commentLikeRepository.deleteByCommentIdAndUserId(commentId, userId);
    }

}
