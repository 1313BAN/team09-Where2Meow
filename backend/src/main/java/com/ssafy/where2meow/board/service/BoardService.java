package com.ssafy.where2meow.board.service;

import com.ssafy.where2meow.board.dto.BoardDetailResponse;
import com.ssafy.where2meow.board.dto.BoardListResponse;
import com.ssafy.where2meow.board.dto.BoardRequest;
import com.ssafy.where2meow.board.entity.Board;
import com.ssafy.where2meow.board.entity.BoardCategory;
import com.ssafy.where2meow.board.repository.BoardBookmarkRepository;
import com.ssafy.where2meow.board.repository.BoardCategoryRepository;
import com.ssafy.where2meow.board.repository.BoardLikeRepository;
import com.ssafy.where2meow.board.repository.BoardRepository;
import com.ssafy.where2meow.comment.dto.CommentResponse;
import com.ssafy.where2meow.comment.repository.CommentRepository;
import com.ssafy.where2meow.comment.service.CommentService;
import com.ssafy.where2meow.common.util.UuidUserUtil;
import com.ssafy.where2meow.exception.EntityNotFoundException;
import com.ssafy.where2meow.exception.ForbiddenAccessException;
import com.ssafy.where2meow.user.entity.User;
import com.ssafy.where2meow.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardLikeRepository boardLikeRepository;
    private final BoardBookmarkRepository boardBookmarkRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final BoardCategoryRepository boardCategoryRepository;

    private final CommentService commentService;

    private final UuidUserUtil uuidUserUtil;

    // 게시글 리스트 조회
    // 쿼리 파라미터를 통해 필터링 및 정렬을 선택적으로 지원
    public BoardListResponse getAllBoards(UUID uuid, Integer categoryId, String sort, String direction, int page, int size, boolean bookmarked) {
        Integer userId = uuidUserUtil.getOptionalUserId(uuid);

        return null;
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

}
