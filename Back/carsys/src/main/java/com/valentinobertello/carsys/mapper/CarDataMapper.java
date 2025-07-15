package com.valentinobertello.carsys.mapper;

import com.valentinobertello.carsys.dtos.car.CarResponse;
import com.valentinobertello.carsys.dtos.car.PostCarDto;
import com.valentinobertello.carsys.entities.auth.UserEntity;
import com.valentinobertello.carsys.entities.car.CarEntity;
import com.valentinobertello.carsys.entities.car.ModelEntity;
import com.valentinobertello.carsys.enums.CarStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * La clase "CarDataMapper" se encarga de mapear objetos "request", "entity" y "response"
 * entre ellos.
 */
@Component
public class CarDataMapper {

    /**
     * Mapea los datos recibidos en el DTO de petición {@link PostCarDto} a una nueva entidad de "car".
     *
     * @param postCarDto DTO con los datos ingresados por el cliente (patente, color, etc.)
     */
    public CarEntity mapCarRequestToCarEntity(PostCarDto postCarDto, ModelEntity modelEntity, UserEntity userEntity) {
        return CarEntity.builder()
                .licensePlate(postCarDto.getLicensePlate())
                .model(modelEntity)
                .user(userEntity)
                .year(postCarDto.getYear())
                .color(postCarDto.getColor())
                .basePrice(postCarDto.getBasePrice())
                .mileage(postCarDto.getMileage())
                .status(CarStatus.DISPONIBLE)
                .registrationDate(LocalDate.now())
                .build();
    }

    /**
     * Mapea una entidad de usuario {@link CarEntity} a su DTO de respuesta {@link CarResponse}.
     */
    public CarResponse mapCarEntityToCarResponse(CarEntity carSaved) {
        return CarResponse.builder()
                .id(carSaved.getId())
                .userId(carSaved.getUser().getId())
                .userEmail(carSaved.getUser().getEmail())
                .modelName(carSaved.getModel().getName())
                .brandName(carSaved.getModel().getBrand().getName())
                .year(carSaved.getYear())
                .basePrice(carSaved.getBasePrice())
                .mileage(carSaved.getMileage())
                .color(carSaved.getColor())
                .licensePlate(carSaved.getLicensePlate())
                .status(carSaved.getStatus().name())
                .registrationDate(carSaved.getRegistrationDate())
                .build();
    }

    /**
     * Mapea una lista de "carEntity" a una lista de DTOs de respuesta.
     */
    public List<CarResponse> mapCarEntitiesToCarResponses(List<CarEntity> carEntities) {
        return carEntities.stream()
                .map(this::mapCarEntityToCarResponse)
                .collect(Collectors.toList());
    }

    /**
     * Mapea una "Page" de "carEntity" a una "Page" de DTOs de respuesta.
     */
    public Page<CarResponse> mapCarEntitiesPageToCarResponsesPage(Page<CarEntity> carEntitiesPage) {
        // Obtenemos la lista de entidades del page
        List<CarEntity> carEntities = carEntitiesPage.getContent();

        // Creamos lista vacía de carResponse
        List<CarResponse> carResponses = new ArrayList<>();

        // Iteramos sobre cada entity y la convertimos en response
        for (CarEntity entity : carEntities) {
            CarResponse response = this.mapCarEntityToCarResponse(entity);
            carResponses.add(response);
        }

        // Creamos y retornamos la página con las responses
        return new PageImpl<>(
                carResponses,
                carEntitiesPage.getPageable(),
                carEntitiesPage.getTotalElements()
        );
    }

}
