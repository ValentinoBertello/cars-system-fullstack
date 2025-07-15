package com.valentinobertello.carsys.service.impl;

import com.valentinobertello.carsys.dtos.car.CarResponse;
import com.valentinobertello.carsys.dtos.car.ModelResponse;
import com.valentinobertello.carsys.dtos.car.PostCarDto;
import com.valentinobertello.carsys.dtos.car.UpdateCarDto;
import com.valentinobertello.carsys.entities.auth.UserEntity;
import com.valentinobertello.carsys.entities.car.BrandEntity;
import com.valentinobertello.carsys.entities.car.CarEntity;
import com.valentinobertello.carsys.entities.car.ModelEntity;
import com.valentinobertello.carsys.mapper.CarDataMapper;
import com.valentinobertello.carsys.repository.auth.UserRepository;
import com.valentinobertello.carsys.repository.car.BrandRepository;
import com.valentinobertello.carsys.repository.car.CarRepository;
import com.valentinobertello.carsys.repository.car.ModelRepository;
import com.valentinobertello.carsys.repository.specifications.CarSpecifications;
import com.valentinobertello.carsys.service.CarService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Toda la lógica de negocio relacionada con los autos
 * de la aplicación.
 */
@Service
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;
    private final ModelRepository modelRepository;
    private final BrandRepository brandRepository;
    private final UserRepository userRepository;
    private final CarDataMapper carDataMapper;

    public CarServiceImpl(CarRepository carRepository, ModelRepository modelRepository, BrandRepository brandRepository, UserRepository userRepository, CarDataMapper carDataMapper) {
        this.carRepository = carRepository;
        this.modelRepository = modelRepository;
        this.brandRepository = brandRepository;
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
        CarEntity newCar = carDataMapper.mapCarRequestToCarEntity(carRequest, model, user);

        CarEntity savedCar = carRepository.save(newCar);
        return carDataMapper.mapCarEntityToCarResponse(savedCar);
    }

    /**
     * Edita ciertos datos de un auto en particular.
     */
    @Override
    public CarResponse updateCar(UpdateCarDto carRequest, String username) {
        CarEntity carBd = this.carRepository.findById(carRequest.getId()).orElseThrow(() -> new EntityNotFoundException(
                "Auto no encontrado."));

        // Comprobación de propietario
        if (!carBd.getUser().getEmail().equals(username)) {
            throw new RuntimeException("No tienes permiso para editar este vehículo");
        }

        carBd.setBasePrice(carRequest.getBasePrice());
        carBd.setColor(carRequest.getColor());
        carBd.setMileage(carRequest.getMileage());
        carBd = this.carRepository.save(carBd);

        return this.carDataMapper.mapCarEntityToCarResponse(carBd);
    }

    /**
     * Busca vehículos según filtros opcionales de patente, marca y modelo,
     * construyendo dinámicamente la consulta con JPA Specifications.
     * @return página de CarResponse con los resultados filtrados.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<CarResponse> searchCarsByFilters(String licensePlate,
                                                 String brand,
                                                 String model,
                                                 String username,
                                                 Pageable pageable) {
        Page<CarEntity> carEntities = this.carRepository.findAll(
                    CarSpecifications.carSearch(licensePlate, brand, model, username), pageable
            );
            return this.carDataMapper.mapCarEntitiesPageToCarResponsesPage(carEntities);
    }

    /**
     * Devuelve todos los modelos de vehículos.
     */
    @Override
    public List<ModelResponse> getAllModels() {
        return this.modelRepository.findAll().stream()
                .map(model -> ModelResponse.builder()
                        .id(model.getId())
                        .name(model.getName())
                        .brandName(model.getBrand().getName())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Devuelve todas las marcas de vehículos.
     */
    @Override
    public List<BrandEntity> getAllBrands() {
        return this.brandRepository.findAll();
    }

}
