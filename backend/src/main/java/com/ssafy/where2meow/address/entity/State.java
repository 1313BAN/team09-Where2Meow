package com.ssafy.where2meow.address.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "state")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class State {
  @Id
  @Column(name = "state_id", nullable = false)
  private Integer stateId;

  @Column(name = "state_code", nullable = false)
  private Integer stateCode;

  @Column(name = "state_name", length = 20)
  private String stateName;

  @Column(name = "country_id", nullable = false)
  private Integer countryId;
}
