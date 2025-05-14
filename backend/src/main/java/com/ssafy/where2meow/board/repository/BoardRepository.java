package com.ssafy.where2meow.board.repository;

import com.ssafy.where2meow.board.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface BoardRepository extends JpaRepository<Board, Integer> {

    // 게시글 조회수 증가
    @Modifying
    @Query("UPDATE Board b SET b.viewCount = b.viewCount + 1 WHERE b.boardId = :boardId")
    void increaseViewCount(int boardId);

}
