package com.ssafy.where2meow.comment.repository;

import com.ssafy.where2meow.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    
    // 특정 게시글에 대한 댓글 정보 조회
    List<Comment> findByBoardId(int boardId);
    
}
