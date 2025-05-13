package com.ssafy.where2meow.board.service;

import com.ssafy.where2meow.board.dto.BoardListResponse;
import com.ssafy.where2meow.board.dto.BoardRequest;
import com.ssafy.where2meow.board.entity.Board;
import com.ssafy.where2meow.board.repository.BoardRepository;
import com.ssafy.where2meow.common.util.UuidUserUtil;
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

}
