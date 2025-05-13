package com.ssafy.where2meow.board.repository;

import com.ssafy.where2meow.board.entity.BoardBookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

public interface BoardBookmarkRepository extends JpaRepository<BoardBookmark, Integer> {

    // 사용자가 게시글을 북마크했는지 확인
    boolean existsByBoardIdAndUserId(int boardId, int userId);

    // 사용자가 누른 특정 게시글 북마크 삭제
    @Modifying
    void deleteByBoardIdAndUserId(int boardId, int userId);

    // 특정 게시글에 속한 북마크 삭제
    @Modifying
    void deleteByBoardId(int boardId);

}
