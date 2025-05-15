package com.ssafy.where2meow.board.service;

import com.ssafy.where2meow.board.entity.Board;
import com.ssafy.where2meow.board.entity.BoardLike;
import com.ssafy.where2meow.board.repository.BoardLikeRepository;
import com.ssafy.where2meow.board.repository.BoardRepository;
import com.ssafy.where2meow.common.util.UuidUserUtil;
import com.ssafy.where2meow.exception.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BoardLikeService {

    private final BoardRepository boardRepository;
    private final BoardLikeRepository boardLikeRepository;

    private final UuidUserUtil uuidUserUtil;

    // 사용자가 게시글에 좋아요를 눌렀는지 확인
    public boolean hasUserLiked(int boardId, int userId) {
        return boardLikeRepository.existsByBoardIdAndUserId(boardId, userId);
    }

    // 게시글 좋아요 추가
    @Transactional
    public void createLike(int boardId, UUID uuid) {
        Integer userId = uuidUserUtil.getRequiredUserId(uuid);
        if (!hasUserLiked(boardId, userId)) {
            Board board = boardRepository.findById(boardId)
                    .orElseThrow(() -> new EntityNotFoundException("Board", "boardId", boardId));
            BoardLike boardLike = new BoardLike();
            boardLike.setBoardId(boardId);
            boardLike.setUserId(userId);
            boardLikeRepository.save(boardLike);
        }
    }

    // 게시글 좋아요 삭제
    @Transactional
    public void deleteLike(int boardId, UUID uuid) {
        Integer userId = uuidUserUtil.getRequiredUserId(uuid);
        boardLikeRepository.deleteByBoardIdAndUserId(boardId, userId);
    }

}
