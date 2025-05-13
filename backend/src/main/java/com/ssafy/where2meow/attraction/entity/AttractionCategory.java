package com.ssafy.where2meow.attraction.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "attraction_category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class AttractionCategory {
  @Id
  @Column(name = "attraction_category_id", nullable = false)
  private Integer attractionCategoryId;

  @Column(name = "attraction_category_name", length = 45)
  private String attractionCategoryName;
}
