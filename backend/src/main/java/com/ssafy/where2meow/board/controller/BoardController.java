package com.ssafy.where2meow.board.controller;

import com.ssafy.where2meow.board.dto.BoardListResponse;
import com.ssafy.where2meow.board.dto.BoardRequest;
import com.ssafy.where2meow.board.entity.Board;
import com.ssafy.where2meow.board.service.BoardBookmarkService;
import com.ssafy.where2meow.board.service.BoardLikeService;
import com.ssafy.where2meow.board.service.BoardService;
import com.ssafy.where2meow.common.util.UuidUserUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/board")
@RequiredArgsConstructor
@Tag(name = "Board", description = "게시판 관리 API")
public class BoardController {

    private final BoardService boardService;
    private final BoardLikeService boardLikeService;
    private final BoardBookmarkService boardBookmarkService;

    private final UuidUserUtil uuidUserUtil;

    @GetMapping
    @Operation(
            summary = "게시글 리스트 조회", 
            description = "게시글 리스트를 조회합니다. 다양한 필터링 및 정렬 옵션을 지원합니다."
    )
    @ApiResponse(responseCode = "200", description = "게시글 리스트 조회 성공")
    public ResponseEntity<BoardListResponse> getAllBoards(
            @Parameter(description = "카테고리 ID") @RequestParam(required = false) Integer categoryId,
            @Parameter(description = "정렬 기준 (createdAt, updatedAt, viewCount, likeCount)") @RequestParam(defaultValue = "createdAt") String sort,
            @Parameter(description = "정렬 방향 (asc, desc)") @RequestParam(defaultValue = "desc") String direction,
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "북마크한 게시글만 조회") @RequestParam(defaultValue = "false") boolean bookmarked) {
        
        UUID currentUserUuid = uuidUserUtil.getCurrentUserUuid();
        BoardListResponse response = boardService.getAllBoards(currentUserUuid, categoryId, sort, direction, page, size, bookmarked);
        
        return ResponseEntity.ok(response);
    }
    
    // 내가 쓴 게시글 조회(/api/board/user)
    
    // 게시글 상세 조회(/api/board/{board_id})
    
    // 게시글 검색(/api/board/search?keyword={keyword})

    @PostMapping
    @Operation(
            summary = "게시글 추가",
            description = "게시글을 추가합니다."
    )
    @ApiResponse(responseCode = "201", description = "게시글 추가 성공")
    public ResponseEntity<Board> createBoard(@RequestBody BoardRequest boardRequest) {
        UUID uuid = uuidUserUtil.getCurrentUserUuid();

        Board createdBoard = boardService.createBoard(boardRequest, uuid);
        URI location = URI.create("/api/board/" + createdBoard.getBoardId());
        return ResponseEntity.created(location).body(createdBoard);
    }

    @PutMapping("/{boardId}")
    @Operation(
            summary = "게시글 수정",
            description = "게시글을 수정합니다."
    )
    @ApiResponse(responseCode = "200", description = "게시글 수정 성공")
    @ApiResponse(responseCode = "403", description = "게시글 수정 권한 없음")
    @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음")
    public ResponseEntity<Board> updateBoard(
            @PathVariable int boardId, @RequestBody BoardRequest boardRequest) {
        UUID uuid = uuidUserUtil.getCurrentUserUuid();

        Board updatedBoard = boardService.updateBoard(boardId, boardRequest, uuid);
        return ResponseEntity.ok(updatedBoard);
    }

    @DeleteMapping("/{boardId}")
    @Operation(
            summary = "게시글 삭제",
            description = "게시글을 삭제합니다."
    )
    @ApiResponse(responseCode = "204", description = "게시글 삭제 성공")
    @ApiResponse(responseCode = "403", description = "게시글 삭제 권한 없음")
    @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음")
    public ResponseEntity<Void> deleteBoard(@PathVariable int boardId) {
        UUID uuid = uuidUserUtil.getCurrentUserUuid();

        boardService.deleteBoard(boardId, uuid);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{boardId}/like")
    @Operation(
            summary = "게시글 좋아요 추가",
            description = "로그인한 사용자가 특정 게시글에 좋아요를 추가합니다."
    )
    @ApiResponse(responseCode = "204", description = "게시글 좋아요 추가 성공")
    public ResponseEntity<Void> createLike(@PathVariable int boardId) {
        UUID uuid = uuidUserUtil.getCurrentUserUuid();

        boardLikeService.createLike(boardId, uuid);
        return ResponseEntity.noContent().build();
    }
    
    @DeleteMapping("/{boardId}/like")
    @Operation(
            summary = "게시글 좋아요 삭제",
            description = "로그인한 사용자가 특정 게시글에 좋아요를 삭제합니다."
    )
    @ApiResponse(responseCode = "204", description = "게시글 좋아요 삭제 성공")
    public ResponseEntity<Void> deleteLike(@PathVariable int boardId) {
        UUID uuid = uuidUserUtil.getCurrentUserUuid();

        boardLikeService.deleteLike(boardId, uuid);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{boardId}/bookmark")
    @Operation(
            summary = "게시글 북마크 추가",
            description = "로그인한 사용자가 특정 게시글에 북마크를 추가합니다."
    )
    @ApiResponse(responseCode = "204", description = "게시글 북마크 추가 성공")
    public ResponseEntity<Void> createBookmark(@PathVariable int boardId) {
        UUID uuid = uuidUserUtil.getCurrentUserUuid();

        boardBookmarkService.createBookmark(boardId, uuid);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{boardId}/bookmark")
    @Operation(
            summary = "게시글 북마크 삭제",
            description = "로그인한 사용자가 특정 게시글에 북마크를 삭제합니다."
    )
    @ApiResponse(responseCode = "204", description = "게시글 북마크 삭제 성공")
    public ResponseEntity<Void> deleteBookmark(@PathVariable int boardId) {
        UUID uuid = uuidUserUtil.getCurrentUserUuid();

        boardBookmarkService.deleteBookmark(boardId, uuid);
        return ResponseEntity.noContent().build();
    }
    
    // 게시글의 댓글 조회(/api/board/{board_id}/comment)
    
}
