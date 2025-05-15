package com.ssafy.where2meow.board.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(of = "bookmarkId")
@Entity
@Table(name = "board_bookmark")
public class BoardBookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bookmark_id")
    private int bookmarkId;

    @Column(name = "board_id", nullable = false)
    private int boardId;

    @Column(name = "user_id", nullable = false)
    private int userId;

    @CreationTimestamp
    @Column(name = "bookmarked_at")
    private LocalDateTime bookmarkedAt;

}
