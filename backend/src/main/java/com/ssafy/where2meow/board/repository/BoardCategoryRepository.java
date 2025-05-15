package com.ssafy.where2meow.board.repository;

import com.ssafy.where2meow.board.entity.BoardCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardCategoryRepository extends JpaRepository<BoardCategory, Integer> {
}
