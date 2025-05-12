package com.ssafy.where2meow.address.repository;

import com.ssafy.where2meow.address.entity.State;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StateRepository extends JpaRepository<State, Integer> {
  Optional<State> findByStateCode(Integer stateCode);
}
