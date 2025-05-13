package com.ssafy.where2meow.address.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "country")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Country {

  @Id
  @Column(name = "country_id", nullable = false)
  private Integer countryId;

  @Column(name = "country_code", nullable = false, length = 5)
  private String countryCode;

  @Column(name = "country_name", length = 100)
  private String countryName;
}
