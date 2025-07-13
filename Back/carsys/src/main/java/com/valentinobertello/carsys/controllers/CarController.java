package com.valentinobertello.carsys.controllers;

import com.valentinobertello.carsys.dtos.car.CarResponse;
import com.valentinobertello.carsys.dtos.car.PostCarDto;
import com.valentinobertello.carsys.service.CarService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(originPatterns = "*")
@RestController
@RequestMapping("/cars")
public class CarController {

    private final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }

    @PostMapping("/register")
    public ResponseEntity<CarResponse> createCar(@RequestBody @Valid PostCarDto carRequest){
        return ResponseEntity.ok(this.carService.createCar(carRequest));
    }

}
