package com.ssafy.where2meow.board.repository;

import com.ssafy.where2meow.board.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardRepository extends JpaRepository<Board, Integer> {

    // 사용자 ID로 게시글 조회
    Page<Board> findAllByUserId(int userId, Pageable pageable);

    // 카테고리 별 조회
    Page<Board> findAllByCategoryId(int categoryId, Pageable pageable);

    // 게시글 검색
    @Query("SELECT b FROM Board b WHERE b.title LIKE %:keyword% OR b.content LIKE %:keyword%")
    Page<Board> searchByKeyword(String keyword, Pageable pageable);

    // 게시글 조회수 증가
    @Modifying
    @Transactional
    @Query("UPDATE Board b SET b.viewCount = b.viewCount + 1 WHERE b.boardId = :boardId")
    void increaseViewCount(int boardId);

    // 사용자가 북마크한 게시글 조회
    @Query("SELECT b FROM Board b JOIN BoardBookmark bb ON b.boardId = bb.boardId " +
            "WHERE bb.userId = :userId " +
            "AND (:categoryId IS NULL OR b.categoryId = :categoryId)")
    Page<Board> findBookmarkedBoardsByUserIdAndCategoryId(
            @Param("userId") int userId,
            @Param("categoryId") Integer categoryId,
            Pageable pageable);

}
