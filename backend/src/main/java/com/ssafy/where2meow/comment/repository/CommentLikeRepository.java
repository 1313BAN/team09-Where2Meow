package com.ssafy.where2meow.comment.repository;

import com.ssafy.where2meow.comment.entity.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Integer> {
}
