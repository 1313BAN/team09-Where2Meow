package com.ssafy.where2meow.address.repository;

import com.ssafy.where2meow.address.entity.City;
import com.ssafy.where2meow.address.entity.State;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CityRepository extends JpaRepository<City, Integer> {
  Optional<City> findByCityCodeAndStateCode(Integer stateCode, Integer cityCode);

  /**
   * state_code에 해당하는 City 객체 조회
   */
  List<City> findByStateCode(Integer stateCode);
}