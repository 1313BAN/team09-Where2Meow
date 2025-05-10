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
@Table(name = "plan_like")
public class PlanLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_like_id")
    private int planLikeId;

    @Column(name = "plan_id")
    private int planId;

    @Column(name = "user_id")
    private int userId;

    @Column(name = "liked_at")
    private LocalDateTime likedAt;

}
