package com.ssafy.where2meow.board.repository;

import com.ssafy.where2meow.board.entity.BoardLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardLikeRepository extends JpaRepository<BoardLike, Integer> {
}
