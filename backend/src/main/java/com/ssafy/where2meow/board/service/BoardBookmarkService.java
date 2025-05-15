package com.ssafy.where2meow.board.service;

import com.ssafy.where2meow.board.entity.Board;
import com.ssafy.where2meow.board.entity.BoardBookmark;
import com.ssafy.where2meow.board.repository.BoardBookmarkRepository;
import com.ssafy.where2meow.board.repository.BoardRepository;
import com.ssafy.where2meow.common.util.UuidUserUtil;
import com.ssafy.where2meow.exception.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BoardBookmarkService {

    private final BoardRepository boardRepository;
    private final BoardBookmarkRepository boardBookmarkRepository;

    private final UuidUserUtil uuidUserUtil;

    // 사용자가 게시글에 북마크를 눌렀는지 확인
    public boolean hasUserBookmarked(int boardId, int userId) {
        return boardBookmarkRepository.existsByBoardIdAndUserId(boardId, userId);
    }

    // 게시글 북마크 추가
    @Transactional
    public void createBookmark(int boardId, UUID uuid) {
        Integer userId = uuidUserUtil.getRequiredUserId(uuid);
        if (!hasUserBookmarked(boardId, userId)) {
            Board board = boardRepository.findById(boardId)
                    .orElseThrow(() -> new EntityNotFoundException("Board", "boardId", boardId));
            BoardBookmark boardBookmark = new BoardBookmark();
            boardBookmark.setBoardId(boardId);
            boardBookmark.setUserId(userId);
            boardBookmarkRepository.save(boardBookmark);
        }
    }

    // 게시글 북마크 삭제
    @Transactional
    public void deleteBookmark(int boardId, UUID uuid) {
        Integer userId = uuidUserUtil.getRequiredUserId(uuid);
        boardBookmarkRepository.deleteByBoardIdAndUserId(boardId, userId);
    }

}
