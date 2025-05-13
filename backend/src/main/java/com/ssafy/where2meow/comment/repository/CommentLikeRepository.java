package com.ssafy.where2meow.comment.repository;

import com.ssafy.where2meow.comment.entity.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Integer> {

    // 사용자가 댓글에 좋아요를 눌렀는지 확인
    boolean existsByCommentIdAndUserId(int commentId, int userId);

    // 사용자가 누른 특정 댓글 좋아요 삭제
    void deleteByCommentIdAndUserId(int commentId, int userId);

    // 특정 댓글의 좋아요 수 카운트
    int countByCommentId(int commentId);

    // 특정 댓글에 속한 좋아요 삭제
    void deleteByCommentId(int commentId);

}
