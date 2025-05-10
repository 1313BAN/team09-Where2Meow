package com.ssafy.where2meow.plan.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "plan_attraction")
public class PlanAttraction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_attraction_id")
    private int planAttractionId;

    @ManyToOne
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    @Column(name = "attraction_id", nullable = false)
    private int attractionId;

    @Column(name = "content")
    private String content;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "attraction_order")
    private int attractionOrder;

}
