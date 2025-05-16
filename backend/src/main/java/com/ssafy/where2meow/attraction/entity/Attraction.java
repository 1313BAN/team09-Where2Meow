package com.ssafy.where2meow.attraction.entity;

import com.ssafy.where2meow.address.entity.City;
import com.ssafy.where2meow.address.entity.Country;
import com.ssafy.where2meow.address.entity.State;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "attraction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(of = "attractionId")
public class Attraction {
  @Id
  @Column(name = "attraction_id", nullable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer attractionId;

  @Column(name = "content_id")
  private Integer contentId;

  @Column(name = "attraction_name", length = 500)
  private String attractionName;

  @Column(name = "first_image1", length = 100)
  private String firstImage1;

  @Column(name = "first_image2", length = 100)
  private String firstImage2;

  @Column(name = "map_level")
  private Integer mapLevel;

  @Column(name = "latitude", precision = 20, scale = 17)
  private BigDecimal latitude;

  @Column(name = "longitude", precision = 20, scale = 17)
  private BigDecimal longitude;

  @Column(name = "tel", length = 20)
  private String tel;

  @Column(name = "addr1", length = 100)
  private String addr1;

  @Column(name = "addr2", length = 100)
  private String addr2;

  @Column(name = "homepage", length = 1000)
  private String homepage;

  @Column(name = "overview", length = 10000)
  private String overview;

  @Column(name = "attraction_category_id")
  private Integer attractionCategoryId;

  @Column(name = "country_id")
  private Integer countryId;

  @Column(name = "state_code")
  private Integer stateCode;

  @Column(name = "city_code")
  private Integer cityCode;
}
