package com.ssafy.where2meow.board.service;

import com.ssafy.where2meow.board.dto.BoardListResponse;
import com.ssafy.where2meow.board.dto.BoardRequest;
import com.ssafy.where2meow.board.entity.Board;
import com.ssafy.where2meow.board.repository.BoardLikeRepository;
import com.ssafy.where2meow.board.repository.BoardRepository;
import com.ssafy.where2meow.common.util.UuidUserUtil;
import com.ssafy.where2meow.exception.EntityNotFoundException;
import com.ssafy.where2meow.exception.ForbiddenAccessException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    private final UuidUserUtil uuidUserUtil;
    private final BoardLikeRepository boardLikeRepository;

    // 게시글 리스트 조회
    // 쿼리 파라미터를 통해 필터링 및 정렬을 선택적으로 지원
    public BoardListResponse getAllBoards(UUID uuid, Integer categoryId, String sort, String direction, int page, int size, boolean bookmarked) {
        Integer userId = uuidUserUtil.getOptionalUserId(uuid);

        return null;
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
        board.setUpdatedAt(LocalDateTime.now());

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
        board.setUpdatedAt(LocalDateTime.now());
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
        boardRepository.delete(board);
    }

    private void checkBoardOwnership(Board board, Integer userId) {
        if (board.getUserId() != userId) {
            throw new ForbiddenAccessException("이 게시글에 대한 권한이 없습니다");
        }
    }

}
