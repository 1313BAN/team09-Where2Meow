package com.ssafy.where2meow.board.service;

import com.ssafy.where2meow.board.dto.BoardDetailResponse;
import com.ssafy.where2meow.board.dto.BoardListResponse;
import com.ssafy.where2meow.board.dto.BoardRequest;
import com.ssafy.where2meow.board.dto.BoardResponse;
import com.ssafy.where2meow.board.entity.Board;
import com.ssafy.where2meow.board.entity.BoardCategory;
import com.ssafy.where2meow.board.repository.BoardBookmarkRepository;
import com.ssafy.where2meow.board.repository.BoardCategoryRepository;
import com.ssafy.where2meow.board.repository.BoardLikeRepository;
import com.ssafy.where2meow.board.repository.BoardRepository;
import com.ssafy.where2meow.comment.dto.CommentResponse;
import com.ssafy.where2meow.comment.service.CommentService;
import com.ssafy.where2meow.common.util.UuidUserUtil;
import com.ssafy.where2meow.exception.EntityNotFoundException;
import com.ssafy.where2meow.exception.ForbiddenAccessException;
import com.ssafy.where2meow.user.entity.User;
import com.ssafy.where2meow.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardLikeRepository boardLikeRepository;
    private final BoardBookmarkRepository boardBookmarkRepository;
    private final UserRepository userRepository;
    private final BoardCategoryRepository boardCategoryRepository;

    private final CommentService commentService;

    private final UuidUserUtil uuidUserUtil;

    // 게시글 리스트 조회
    // 쿼리 파라미터를 통해 필터링 및 정렬을 선택적으로 지원
    public BoardListResponse getAllBoards(UUID uuid, Integer categoryId, String sort, String direction, int page, int size, boolean bookmarked) {
        Integer userId = uuidUserUtil.getOptionalUserId(uuid);

        // 정렬 기준 설정
        Sort sortOption = createSort(sort, direction);

        // 페이징 설정
        Pageable pageable = PageRequest.of(page, size, sortOption);

        // 게시글 리스트 조회
        Page<Board> boards;

        if(bookmarked && userId != null) {
            boards = getBoardsBookmarkedByUser(userId, categoryId, pageable);
        } else if (categoryId != null) {
            boards = boardRepository.findAllByCategoryId(categoryId, pageable);
        } else {
            boards = boardRepository.findAll(pageable);
        }

        List<BoardResponse> boardResponses = boards.getContent().stream()
                .map(board -> convertToBoardResponse(board, userId))
                .toList();

        return BoardListResponse.builder()
                .boards(boardResponses)
                .totalPages(boards.getTotalPages())
                .totalElements(boards.getTotalElements())
                .currentPage(boards.getNumber())
                .isFirst(boards.isFirst())
                .isLast(boards.isLast())
                .build();
    }

    // 내가 쓴 게시글 리스트 조회
    public BoardListResponse getUserBoards(UUID uuid, String sort, String direction, int page, int size) {
        Integer userId = uuidUserUtil.getRequiredUserId(uuid);

        Sort sortOption = createSort(sort, direction);
        Pageable pageable = PageRequest.of(page, size, sortOption);
        Page<Board> boards = boardRepository.findAllByUserId(userId, pageable);

        List<BoardResponse> boardResponses = boards.stream()
                .map(board -> convertToBoardResponse(board, userId))
                .toList();

        return BoardListResponse.builder()
                .boards(boardResponses)
                .totalPages(boards.getTotalPages())
                .totalElements(boards.getTotalElements())
                .currentPage(boards.getNumber())
                .isFirst(boards.isFirst())
                .isLast(boards.isLast())
                .build();
    }

    // 게시글 검색
    public BoardListResponse searchBoards(String keyword, String sort, String direction, int page, int size) {
        Sort sortOption = createSort(sort, direction);
        Pageable pageable = PageRequest.of(page, size, sortOption);

        Page<Board> boards = boardRepository.searchByKeyword(keyword, pageable);

        List<BoardResponse> boardResponses = boards.getContent().stream()
                .map(board -> convertToBoardResponse(board, null))
                .toList();

        return BoardListResponse.builder()
                .boards(boardResponses)
                .totalPages(boards.getTotalPages())
                .totalElements(boards.getTotalElements())
                .currentPage(boards.getNumber())
                .isFirst(boards.isFirst())
                .isLast(boards.isLast())
                .build();
    }

    // 특정 게시글 상세 조회
    @Transactional
    public BoardDetailResponse getBoardDetail(int boardId, UUID uuid) {
        Integer userId = uuidUserUtil.getOptionalUserId(uuid);
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("Board", "boardId", boardId));

        // 조회수 증가
        boardRepository.increaseViewCount(boardId);
        board.setViewCount(board.getViewCount() + 1);

        // 관련 정보 조회
        User user = userRepository.findById(board.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User", "userId", board.getUserId()));
        String username = user.getName();

        BoardCategory category = boardCategoryRepository.findById(board.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category", "categoryId", board.getCategoryId()));
        String categoryName = category.getCategoryName();

        int likeCount = boardLikeRepository.countByBoardId(boardId);

        boolean isLiked = false;
        boolean isBookmarked = false;

        if(userId != null) {
            isLiked = boardLikeRepository.existsByBoardIdAndUserId(boardId, userId);
            isBookmarked = boardBookmarkRepository.existsByBoardIdAndUserId(boardId, userId);
        }

        // 관련 댓글 정보 조회
        List<CommentResponse> commentResponses = commentService.getCommentsByBoardId(boardId, uuid);

        return BoardDetailResponse.fromBoard(board,
                username,
                categoryName,
                likeCount,
                isLiked,
                isBookmarked,
                commentResponses);
    }

    // 게시글 추가
    @Transactional
    public Board createBoard(BoardRequest boardRequest, UUID uuid) {
        Integer userId = uuidUserUtil.getRequiredUserId(uuid);

        Board board = new Board();
        board.setUserId(userId);
        board.setTitle(boardRequest.getTitle());
        board.setContent(boardRequest.getContent());
        board.setCategoryId(boardRequest.getCategoryId());

        return boardRepository.save(board);
    }

    // 게시글 수정
    @Transactional
    public Board updateBoard(int boardId, BoardRequest boardRequest, UUID uuid) {
        Integer userId = uuidUserUtil.getRequiredUserId(uuid);
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("Board", "boardId", boardId));

        checkBoardOwnership(board, userId);

        board.setTitle(boardRequest.getTitle());
        board.setContent(boardRequest.getContent());
        board.setCategoryId(boardRequest.getCategoryId());
        return boardRepository.save(board);
    }

    // 게시글 삭제
    @Transactional
    public void deleteBoard(int boardId, UUID uuid) {
        Integer userId = uuidUserUtil.getRequiredUserId(uuid);
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("Board", "boardId", boardId));

        checkBoardOwnership(board, userId);

        boardLikeRepository.deleteByBoardId(boardId);
        boardBookmarkRepository.deleteByBoardId(boardId);
        boardRepository.delete(board);
    }

    private void checkBoardOwnership(Board board, Integer userId) {
        if (board.getUserId() != userId) {
            throw new ForbiddenAccessException("이 게시글에 대한 권한이 없습니다");
        }
    }

    private Sort createSort(String sort, String direction) {
        if (sort == null || direction == null) {
            throw new IllegalArgumentException("sort와 direction은 필수 파라미터입니다.");
        }

        Sort.Direction dir;
        try {
            dir = Sort.Direction.fromString(direction);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid sort direction: " + direction);
        }

        return Sort.by(dir, sort);
    }

    private Page<Board> getBoardsBookmarkedByUser(int userId, Integer categoryId, Pageable pageable) {
        return boardRepository.findBookmarkedBoardsByUserIdAndCategoryId(userId, categoryId, pageable);
    }

    private BoardResponse convertToBoardResponse(Board board, Integer userId) {
        // 카테고리 이름 조회
        String categoryName = boardCategoryRepository.findById(board.getCategoryId())
                .map(BoardCategory::getCategoryName)
                .orElseThrow(() -> new EntityNotFoundException("Category", "categoryId", board.getCategoryId()));
        // 좋아요 수 조회
        int likeCount = boardLikeRepository.countByBoardId(board.getBoardId());

        // 좋아요 여부 확인
        boolean isLiked = false;
        if (userId != null) {
            isLiked = boardLikeRepository.existsByBoardIdAndUserId(board.getBoardId(), userId);
        }

        // 북마크 여부 확인
        boolean isBookmarked = false;
        if (userId != null) {
            isBookmarked = boardBookmarkRepository.existsByBoardIdAndUserId(board.getBoardId(), userId);
        }

        // BoardResponse 생성 및 반환
        return BoardResponse.fromBoard(board, categoryName, likeCount, isLiked, isBookmarked);
    }

}
