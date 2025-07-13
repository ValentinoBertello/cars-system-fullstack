package com.valentinobertello.carsys.service.impl;

import com.valentinobertello.carsys.dtos.car.CarResponse;
import com.valentinobertello.carsys.dtos.car.PostCarDto;
import com.valentinobertello.carsys.entities.auth.UserEntity;
import com.valentinobertello.carsys.entities.car.CarEntity;
import com.valentinobertello.carsys.entities.car.ModelEntity;
import com.valentinobertello.carsys.mapper.CarDataMapper;
import com.valentinobertello.carsys.repository.auth.UserRepository;
import com.valentinobertello.carsys.repository.car.CarRepository;
import com.valentinobertello.carsys.repository.car.ModelRepository;
import com.valentinobertello.carsys.service.CarService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;
    private final ModelRepository modelRepository;
    private final UserRepository userRepository;
    private final CarDataMapper carDataMapper;

    public CarServiceImpl(CarRepository carRepository, ModelRepository modelRepository, UserRepository userRepository, CarDataMapper carDataMapper) {
        this.carRepository = carRepository;
        this.modelRepository = modelRepository;
        this.userRepository = userRepository;
        this.carDataMapper = carDataMapper;
    }

    /**
     * Crea y persiste un nuevo auto en la base de datos.
     */
    @Override
    @Transactional
    public CarResponse createCar(PostCarDto carRequest) {
        ModelEntity model = modelRepository.findById(carRequest.getModelId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Modelo no encontrado con ID: " + carRequest.getModelId()));

        UserEntity user = userRepository.findByEmail(carRequest.getUserEmail())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Usuario no encontrado con email: " + carRequest.getUserEmail()));

        //Mapeamos el PostCarDto a "carEntity"
        CarEntity newCar = carDataMapper.mapUserRequestToUserEntity(carRequest, model, user);

        CarEntity savedCar = carRepository.save(newCar);
        return carDataMapper.mapCarEntityToCarResponse(savedCar);
    }


}
