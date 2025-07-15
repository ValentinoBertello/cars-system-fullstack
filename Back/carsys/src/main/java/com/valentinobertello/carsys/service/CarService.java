package com.valentinobertello.carsys.service;

import com.valentinobertello.carsys.dtos.car.CarResponse;
import com.valentinobertello.carsys.dtos.car.ModelResponse;
import com.valentinobertello.carsys.dtos.car.PostCarDto;
import com.valentinobertello.carsys.dtos.car.UpdateCarDto;
import com.valentinobertello.carsys.entities.car.BrandEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CarService {
    CarResponse createCar(PostCarDto carRequest);

    CarResponse updateCar(UpdateCarDto carRequest, String username);

    Page<CarResponse> searchCarsByFilters(String licensePlate, String brand, String model, String username,
                                          Pageable pageable);

    List<ModelResponse> getAllModels();

    List<BrandEntity> getAllBrands();
}
