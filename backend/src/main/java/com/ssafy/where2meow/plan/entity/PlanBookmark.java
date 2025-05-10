package com.ssafy.where2meow.plan.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "plan_bookmark")
public class PlanBookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_bookmark_id")
    private int planBookmarkId;

    @Column(name = "plan_id", nullable = false)
    private int planId;

    @Column(name = "user_id", nullable = false)
    private int userId;

    @Column(name = "bookmarked_at")
    private LocalDateTime bookmarkedAt;

}
