package com.ssafy.where2meow.address.repository;

import com.ssafy.where2meow.address.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, Integer> {
}
