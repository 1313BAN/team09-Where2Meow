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

    // 좋아요 수 기준 정렬로 게시글 조회 (전체)
    @Query("SELECT b FROM Board b " +
            "LEFT JOIN BoardLike bl ON b.boardId = bl.boardId " +
            "GROUP BY b.boardId " +
            "ORDER BY COUNT(bl.likeId) DESC, b.createdAt DESC")
    Page<Board> findAllOrderByLikeCountDesc(Pageable pageable);

    @Query("SELECT b FROM Board b " +
            "LEFT JOIN BoardLike bl ON b.boardId = bl.boardId " +
            "GROUP BY b.boardId " +
            "ORDER BY COUNT(bl.likeId) ASC, b.createdAt DESC")
    Page<Board> findAllOrderByLikeCountAsc(Pageable pageable);

    // 좋아요 수 기준 정렬로 게시글 조회 (카테고리별)
    @Query("SELECT b FROM Board b " +
            "LEFT JOIN BoardLike bl ON b.boardId = bl.boardId " +
            "WHERE b.categoryId = :categoryId " +
            "GROUP BY b.boardId " +
            "ORDER BY COUNT(bl.likeId) DESC, b.createdAt DESC")
    Page<Board> findAllByCategoryIdOrderByLikeCountDesc(
            @Param("categoryId") int categoryId, 
            Pageable pageable);

    @Query("SELECT b FROM Board b " +
            "LEFT JOIN BoardLike bl ON b.boardId = bl.boardId " +
            "WHERE b.categoryId = :categoryId " +
            "GROUP BY b.boardId " +
            "ORDER BY COUNT(bl.likeId) ASC, b.createdAt DESC")
    Page<Board> findAllByCategoryIdOrderByLikeCountAsc(
            @Param("categoryId") int categoryId, 
            Pageable pageable);

    // 좋아요 수 기준 정렬로 북마크한 게시글 조회
    @Query("SELECT b FROM Board b " +
            "JOIN BoardBookmark bb ON b.boardId = bb.boardId " +
            "LEFT JOIN BoardLike bl ON b.boardId = bl.boardId " +
            "WHERE bb.userId = :userId " +
            "AND (:categoryId IS NULL OR b.categoryId = :categoryId) " +
            "GROUP BY b.boardId " +
            "ORDER BY COUNT(bl.likeId) DESC, b.createdAt DESC")
    Page<Board> findBookmarkedBoardsByUserIdAndCategoryIdOrderByLikeCountDesc(
            @Param("userId") int userId,
            @Param("categoryId") Integer categoryId,
            Pageable pageable);

    @Query("SELECT b FROM Board b " +
            "JOIN BoardBookmark bb ON b.boardId = bb.boardId " +
            "LEFT JOIN BoardLike bl ON b.boardId = bl.boardId " +
            "WHERE bb.userId = :userId " +
            "AND (:categoryId IS NULL OR b.categoryId = :categoryId) " +
            "GROUP BY b.boardId " +
            "ORDER BY COUNT(bl.likeId) ASC, b.createdAt DESC")
    Page<Board> findBookmarkedBoardsByUserIdAndCategoryIdOrderByLikeCountAsc(
            @Param("userId") int userId,
            @Param("categoryId") Integer categoryId,
            Pageable pageable);

    // 사용자가 쓴 게시글 좋아요 수 기준 정렬
    @Query("SELECT b FROM Board b " +
            "LEFT JOIN BoardLike bl ON b.boardId = bl.boardId " +
            "WHERE b.userId = :userId " +
            "GROUP BY b.boardId " +
            "ORDER BY COUNT(bl.likeId) DESC, b.createdAt DESC")
    Page<Board> findAllByUserIdOrderByLikeCountDesc(
            @Param("userId") int userId, 
            Pageable pageable);

    @Query("SELECT b FROM Board b " +
            "LEFT JOIN BoardLike bl ON b.boardId = bl.boardId " +
            "WHERE b.userId = :userId " +
            "GROUP BY b.boardId " +
            "ORDER BY COUNT(bl.likeId) ASC, b.createdAt DESC")
    Page<Board> findAllByUserIdOrderByLikeCountAsc(
            @Param("userId") int userId, 
            Pageable pageable);

    // 검색 결과 좋아요 수 기준 정렬
    @Query("SELECT b FROM Board b " +
            "LEFT JOIN BoardLike bl ON b.boardId = bl.boardId " +
            "WHERE b.title LIKE %:keyword% OR b.content LIKE %:keyword% " +
            "GROUP BY b.boardId " +
            "ORDER BY COUNT(bl.likeId) DESC, b.createdAt DESC")
    Page<Board> searchByKeywordOrderByLikeCountDesc(
            @Param("keyword") String keyword, 
            Pageable pageable);

    @Query("SELECT b FROM Board b " +
            "LEFT JOIN BoardLike bl ON b.boardId = bl.boardId " +
            "WHERE b.title LIKE %:keyword% OR b.content LIKE %:keyword% " +
            "GROUP BY b.boardId " +
            "ORDER BY COUNT(bl.likeId) ASC, b.createdAt DESC")
    Page<Board> searchByKeywordOrderByLikeCountAsc(
            @Param("keyword") String keyword, 
            Pageable pageable);

}
