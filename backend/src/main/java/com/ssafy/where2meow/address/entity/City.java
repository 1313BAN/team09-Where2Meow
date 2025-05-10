package com.ssafy.where2meow.address.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "city")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class City {
  @Id
  @Column(name = "city_id", nullable = false)
  private Integer cityId;

  @Column(name = "city_code", nullable = false)
  private Integer cityCode;

  @Column(name = "city_name", length = 20)
  private String cityName;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "state_id", nullable = false)
  private State state;
}
