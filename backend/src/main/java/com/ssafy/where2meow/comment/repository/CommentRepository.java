package com.ssafy.where2meow.comment.repository;

import com.ssafy.where2meow.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
}
