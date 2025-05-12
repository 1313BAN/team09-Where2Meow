package com.ssafy.where2meow.address.repository;

import com.ssafy.where2meow.address.entity.State;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StateRepository extends JpaRepository<State, Integer> {
  Optional<State> findByStateCode(Integer stateCode);
  
  /**
   * 여러 주(state) 코드에 해당하는 State 객체 조회
   */
  List<State> findByStateCodeIn(List<Integer> stateCodes);
}
