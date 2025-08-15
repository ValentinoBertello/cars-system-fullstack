package com.valentinobertello.carsys.config;

import com.valentinobertello.carsys.dtos.car.CarResponse;
import com.valentinobertello.carsys.dtos.car.PostCarDto;
import com.valentinobertello.carsys.dtos.car.UpdateCarDto;
import com.valentinobertello.carsys.entities.car.BrandEntity;
import com.valentinobertello.carsys.entities.car.CarEntity;
import com.valentinobertello.carsys.entities.car.ModelEntity;
import com.valentinobertello.carsys.enums.CarStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * Clase de utilidad para pruebas unitarias y de integración relacionadas con usuarios.
 * Proporciona datos de prueba predefinidos (mock data).
 */
public class CarTestHelper {
    // --- Brands ---
    public static final BrandEntity BRAND_FIAT = BrandEntity.builder()
            .id(1L)
            .name("Fiat")
            .build();

    public static final BrandEntity BRAND_RENAULT = BrandEntity.builder()
            .id(2L)
            .name("Renault")
            .build();

    // --- Models ---
    public static final ModelEntity MODEL_FIAT_PANDA = ModelEntity.builder()
            .id(10L)
            .name("Panda")
            .brand(BRAND_FIAT)
            .build();

    public static final ModelEntity MODEL_FIAT_500 = ModelEntity.builder()
            .id(11L)
            .name("500")
            .brand(BRAND_FIAT)
            .build();

    public static final ModelEntity MODEL_RENAULT_CLIO = ModelEntity.builder()
            .id(20L)
            .name("Clio")
            .brand(BRAND_RENAULT)
            .build();

    // --- DTO de creación ---
    public static final PostCarDto POST_CAR_DTO_1 = PostCarDto.builder()
            .licensePlate("AB 123 CD")
            .modelId(MODEL_FIAT_PANDA.getId())
            .userEmail(UserTestHelper.USER_ENTITY_1.getEmail())
            .year(2021)
            .color("Rojo")
            .basePrice(new BigDecimal("20000000"))
            .mileage(new BigDecimal("15000"))
            .build();

    // --- Entidades CarEntity ---
    public static final CarEntity CAR_ENTITY_1 = CarEntity.builder()
            .id(100L)
            .licensePlate("AB 123 CD")
            .model(MODEL_FIAT_PANDA)
            .user(UserTestHelper.USER_ENTITY_1)
            .year(2021)
            .color("Rojo")
            .basePrice(new BigDecimal("20500000"))
            .mileage(new BigDecimal("15000"))
            .status(CarStatus.DISPONIBLE)
            .registrationDate(LocalDate.of(2022, 5, 10))
            .build();

    public static final CarEntity CAR_ENTITY_2 = CarEntity.builder()
            .id(101L)
            .licensePlate("NVZ 087")
            .model(MODEL_FIAT_500)
            .user(UserTestHelper.USER_ENTITY_1)
            .year(2020)
            .color("Blanco")
            .basePrice(new BigDecimal("19800000"))
            .mileage(new BigDecimal("22000"))
            .status(CarStatus.REPARACION)
            .registrationDate(LocalDate.of(2021, 8, 20))
            .build();

    public static final CarEntity CAR_ENTITY_3 = CarEntity.builder()
            .id(102L)
            .licensePlate("CD 456 EF")
            .model(MODEL_RENAULT_CLIO)
            .user(UserTestHelper.USER_ENTITY_1)
            .year(2019)
            .color("Azul")
            .basePrice(new BigDecimal("21000000"))
            .mileage(new BigDecimal("30000"))
            .status(CarStatus.VENDIDO)
            .registrationDate(LocalDate.of(2020, 3, 15))
            .build();

    // --- DTOs de respuesta CarResponse ---
    public static final CarResponse CAR_RESPONSE_1 = CarResponse.builder()
            .id(CAR_ENTITY_1.getId())
            .licensePlate(CAR_ENTITY_1.getLicensePlate())
            .modelName(CAR_ENTITY_1.getModel().getName())
            .brandName(CAR_ENTITY_1.getModel().getBrand().getName())
            .userId(CAR_ENTITY_1.getUser().getId())
            .userEmail(CAR_ENTITY_1.getUser().getEmail())
            .year(CAR_ENTITY_1.getYear())
            .color(CAR_ENTITY_1.getColor())
            .basePrice(CAR_ENTITY_1.getBasePrice())
            .mileage(CAR_ENTITY_1.getMileage())
            .status(CAR_ENTITY_1.getStatus().name())
            .registrationDate(CAR_ENTITY_1.getRegistrationDate())
            .build();

    public static final CarResponse CAR_RESPONSE_2 = CarResponse.builder()
            .id(CAR_ENTITY_2.getId())
            .licensePlate(CAR_ENTITY_2.getLicensePlate())
            .modelName(CAR_ENTITY_2.getModel().getName())
            .brandName(CAR_ENTITY_2.getModel().getBrand().getName())
            .userId(CAR_ENTITY_2.getUser().getId())
            .userEmail(CAR_ENTITY_2.getUser().getEmail())
            .year(CAR_ENTITY_2.getYear())
            .color(CAR_ENTITY_2.getColor())
            .basePrice(CAR_ENTITY_2.getBasePrice())
            .mileage(CAR_ENTITY_2.getMileage())
            .status(CAR_ENTITY_2.getStatus().name())
            .registrationDate(CAR_ENTITY_2.getRegistrationDate())
            .build();

    public static final CarResponse CAR_RESPONSE_3 = CarResponse.builder()
            .id(CAR_ENTITY_3.getId())
            .licensePlate(CAR_ENTITY_3.getLicensePlate())
            .modelName(CAR_ENTITY_3.getModel().getName())
            .brandName(CAR_ENTITY_3.getModel().getBrand().getName())
            .userId(CAR_ENTITY_3.getUser().getId())
            .userEmail(CAR_ENTITY_3.getUser().getEmail())
            .year(CAR_ENTITY_3.getYear())
            .color(CAR_ENTITY_3.getColor())
            .basePrice(CAR_ENTITY_3.getBasePrice())
            .mileage(CAR_ENTITY_3.getMileage())
            .status(CAR_ENTITY_3.getStatus().name())
            .registrationDate(CAR_ENTITY_3.getRegistrationDate())
            .build();

    // --- Listas para pruebas de listado ---
    public static final List<CarEntity> ALL_CAR_ENTITIES = Arrays.asList(
            CAR_ENTITY_1,
            CAR_ENTITY_2,
            CAR_ENTITY_3
    );

    public static final List<CarResponse> ALL_CAR_RESPONSES = Arrays.asList(
            CAR_RESPONSE_1,
            CAR_RESPONSE_2,
            CAR_RESPONSE_3
    );

    public static final UpdateCarDto UPDATE_CAR_DTO_1 = UpdateCarDto.builder()
            .id(CAR_ENTITY_1.getId())
            .basePrice(new BigDecimal("25000000"))
            .mileage(new BigDecimal("16000"))
            .build();

    public static final CarEntity SAVED_CAR_ENTITY_1 = CarEntity.builder()
            .build();

    public static final CarResponse UPDATED_CAR_RESPONSE_1 = CarResponse.builder()
            .build();
}
