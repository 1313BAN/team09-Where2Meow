package com.ssafy.where2meow.address.service;

import com.ssafy.where2meow.address.entity.City;
import com.ssafy.where2meow.address.entity.Country;
import com.ssafy.where2meow.address.entity.State;
import com.ssafy.where2meow.address.repository.CityRepository;
import com.ssafy.where2meow.address.repository.CountryRepository;
import com.ssafy.where2meow.address.repository.StateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final CountryRepository countryRepository;
    private final StateRepository stateRepository;
    private final CityRepository cityRepository;

    public List<Country> getCountries() {
        return countryRepository.findAll();
    }

    public List<State> getStatesByCountry(Integer countryId) {
        if(countryId == null) {
            return stateRepository.findAll();
        }

        return stateRepository.findByCountryId(countryId);
    }

    public List<City> getCitiesByState(Integer stateCode) {
        if(stateCode == null) {
            return cityRepository.findAll();
        }

        return cityRepository.findByStateCode(stateCode);
    }

}
