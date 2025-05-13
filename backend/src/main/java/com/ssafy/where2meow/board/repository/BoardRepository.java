package com.ssafy.where2meow.board.repository;

import com.ssafy.where2meow.board.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Integer> {
}
