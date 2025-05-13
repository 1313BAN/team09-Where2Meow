package com.ssafy.where2meow.board.repository;

import com.ssafy.where2meow.board.entity.BoardBookmark;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardBookmarkRepository extends JpaRepository<BoardBookmark, Integer> {
}
