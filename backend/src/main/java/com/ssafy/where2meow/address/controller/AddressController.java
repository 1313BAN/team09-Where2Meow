package com.ssafy.where2meow.address.controller;

import com.ssafy.where2meow.address.entity.City;
import com.ssafy.where2meow.address.entity.Country;
import com.ssafy.where2meow.address.entity.State;
import com.ssafy.where2meow.address.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/address")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @GetMapping("/country")
    public ResponseEntity<List<Country>> getCountries() {
        return ResponseEntity.ok(addressService.getCountries());
    }

    @GetMapping("/state")
    public ResponseEntity<List<State>> getStates(@RequestParam Integer countryId) {
        return ResponseEntity.ok(addressService.getStatesByCountry(countryId));
    }

    @GetMapping("/city")
    public ResponseEntity<List<City>> getCities(@RequestParam Integer stateCode) {
        return ResponseEntity.ok(addressService.getCitiesByState(stateCode));
    }

}
