package com.ssafy.where2meow.board.repository;

import com.ssafy.where2meow.board.entity.BoardLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

public interface BoardLikeRepository extends JpaRepository<BoardLike, Integer> {

    // 사용자가 게시글에 좋아요를 눌렀는지 확인
    boolean existsByBoardIdAndUserId(int boardId, int userId);

    // 사용자가 누른 특정 게시글 좋아요 삭제
    @Modifying
    void deleteByBoardIdAndUserId(int boardId, int userId);

    // 특정 게시글의 좋아요 수 카운트
    int countByBoardId(int boardId);

    // 특정 게시글에 속한 좋아요 삭제
    @Modifying
    void deleteByBoardId(int boardId);

}
