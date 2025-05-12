package com.ssafy.where2meow.address.repository;

import com.ssafy.where2meow.address.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CityRepository extends JpaRepository<City, Integer> {
  Optional<City> findByCityCodeAndStateCode(Integer stateCode, Integer cityCode);
}