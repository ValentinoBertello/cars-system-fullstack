package com.valentinobertello.carsys.service;

import com.valentinobertello.carsys.dtos.car.CarResponse;
import com.valentinobertello.carsys.dtos.car.PostCarDto;
import org.springframework.stereotype.Service;

@Service
public interface CarService {
    CarResponse createCar(PostCarDto carRequest);
}
