package com.ssafy.where2meow.comment.repository;

import com.ssafy.where2meow.comment.entity.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Integer> {

    // 사용자가 댓글에 좋아요를 눌렀는지 확인
    boolean existsByCommentIdAndUserId(int commentId, int userId);

    // 사용자가 누른 특정 댓글 좋아요 삭제
    void deleteByCommentIdAndUserId(int commentId, int userId);

    // 특정 댓글의 좋아요 수 카운트
    int countByCommentId(int commentId);

    // 특정 댓글에 속한 좋아요 삭제
    void deleteByCommentId(int commentId);

    // 특정 댓글 ID 목록에 대한 좋아요 수 조회
    @Query("SELECT c.commentId, COUNT(c) FROM CommentLike c WHERE c.commentId IN :commentIds GROUP BY c.commentId")
    List<Object[]> countByCommentIdIn(@Param("commentIds") List<Integer> commentIds);

    // 특정 사용자가 특정 댓글 목록에 좋아요를 했는지 조회
    @Query("SELECT c.commentId FROM CommentLike c WHERE c.commentId IN :commentIds AND c.userId = :userId")
    List<Integer> findCommentIdsLikedByUser(@Param("commentIds") List<Integer> commentIds, @Param("userId") Integer userId);

}
