package com.ssafy.where2meow.comment.service;

import com.ssafy.where2meow.board.repository.BoardRepository;
import com.ssafy.where2meow.comment.dto.CommentRequest;
import com.ssafy.where2meow.comment.dto.CommentResponse;
import com.ssafy.where2meow.comment.entity.Comment;
import com.ssafy.where2meow.comment.repository.CommentLikeRepository;
import com.ssafy.where2meow.comment.repository.CommentRepository;
import com.ssafy.where2meow.common.util.UuidUserUtil;
import com.ssafy.where2meow.exception.EntityNotFoundException;
import com.ssafy.where2meow.exception.ForbiddenAccessException;
import com.ssafy.where2meow.user.entity.User;
import com.ssafy.where2meow.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;

    private final UuidUserUtil uuidUserUtil;

    // 내가 쓴 댓글 조회
    public List<CommentResponse> getUserComments(UUID uuid, int page, int size) {  // 매개변수 추가
        Integer userId = uuidUserUtil.getRequiredUserId(uuid);

        // 페이지네이션 적용
        Pageable pageable = PageRequest.of(page, size);
        Page<Comment> commentPage = commentRepository.findByUserId(userId, pageable);
        List<Comment> comments = commentPage.getContent();

        List<CommentResponse> commentResponses = new ArrayList<>();

        // 사용자 정보를 한 번만 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User", "userId", userId));
        String username = user.getName();

        // 모든 댓글 ID 추출
        List<Integer> commentIds = comments.stream()
                .map(Comment::getCommentId)
                .toList();

        // 좋아요 수 조회 및 Map으로 변환
        Map<Integer, Integer> likeCounts = new HashMap<>();
        if (!commentIds.isEmpty()) {
            List<Object[]> likeCountResults = commentLikeRepository.countByCommentIdIn(commentIds);
            for (Object[] result : likeCountResults) {
                Integer commentId = (Integer) result[0];
                Long count = (Long) result[1];
                likeCounts.put(commentId, count.intValue());
            }
        }

        // 사용자 좋아요 여부 조회
        List<Integer> likedCommentIds = new ArrayList<>();
        if (!commentIds.isEmpty()) {
            likedCommentIds = commentLikeRepository.findCommentIdsLikedByUser(commentIds, userId);
        }

        // 댓글 응답 생성
        for (Comment comment : comments) {
            int likeCount = likeCounts.getOrDefault(comment.getCommentId(), 0);
            boolean isLiked = likedCommentIds.contains(comment.getCommentId());

            CommentResponse response = CommentResponse.builder()
                    .commentId(comment.getCommentId())
                    .boardId(comment.getBoardId())
                    .userId(comment.getUserId())
                    .username(username)
                    .content(comment.getContent())
                    .createdAt(comment.getCreatedAt())
                    .updatedAt(comment.getUpdatedAt())
                    .likeCount(likeCount)
                    .isLiked(isLiked)
                    .build();
            commentResponses.add(response);
        }

        return commentResponses;
    }

    // 게시글에 해당하는 모든 댓글 조회
    public List<CommentResponse> getCommentsByBoardId(int boardId, UUID uuid) {
        Integer userId = uuidUserUtil.getOptionalUserId(uuid);
        List<Comment> comments = commentRepository.findByBoardId(boardId);
        List<CommentResponse> commentResponses = new ArrayList<>();

        for (Comment comment : comments) {
            // 사용자 정보 조회 - 실제로는 사용자 서비스 호출
            User user = userRepository.findById(comment.getUserId())
                    .orElseThrow(() -> new EntityNotFoundException("User", "userId", comment.getUserId()));
            String username = user.getName();

            // 좋아요 수 조회
            int likeCount = commentLikeRepository.countByCommentId(comment.getCommentId());

            // 사용자가 좋아요를 눌렀는지 확인
            boolean isLiked = userId != null &&
                    commentLikeRepository.existsByCommentIdAndUserId(comment.getCommentId(), userId);

            // CommentResponse 객체 생성
            CommentResponse response = CommentResponse.builder()
                    .commentId(comment.getCommentId())
                    .boardId(comment.getBoardId())
                    .userId(comment.getUserId())
                    .username(username)
                    .content(comment.getContent())
                    .createdAt(comment.getCreatedAt())
                    .updatedAt(comment.getUpdatedAt())
                    .likeCount(likeCount)
                    .isLiked(isLiked)
                    .build();

            commentResponses.add(response);
        }

        return commentResponses;
    }

    // 댓글 추가
    @Transactional
    public Comment createComment(CommentRequest commentRequest, UUID uuid) {
        Integer userId = uuidUserUtil.getRequiredUserId(uuid);

        int boardId = commentRequest.getBoardId();
        if(!boardRepository.existsById(boardId)) {
            throw new EntityNotFoundException("Board", "boardId", boardId);
        }

        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setBoardId(commentRequest.getBoardId());
        comment.setContent(commentRequest.getContent());

        return commentRepository.save(comment);
    }

    // 댓글 수정
    @Transactional
    public Comment updateComment(int commentId, CommentRequest commentRequest, UUID uuid) {
        Integer userId = uuidUserUtil.getRequiredUserId(uuid);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment", "commentId", commentId));

        checkCommentOwnership(comment, userId);

        comment.setContent(commentRequest.getContent());
        return commentRepository.save(comment);
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(int commentId, UUID uuid) {
        Integer userId = uuidUserUtil.getRequiredUserId(uuid);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment", "commentId", commentId));

        checkCommentOwnership(comment, userId);

        commentLikeRepository.deleteByCommentId(commentId);
        commentRepository.delete(comment);
    }

    private void checkCommentOwnership(Comment comment, Integer userId) {
        if (comment.getUserId() != userId) {
            throw new ForbiddenAccessException("이 댓글에 대한 권한이 없습니다");
        }
    }

}
