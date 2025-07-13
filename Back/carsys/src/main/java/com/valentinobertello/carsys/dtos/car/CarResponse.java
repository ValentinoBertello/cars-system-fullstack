package com.valentinobertello.carsys.dtos.car;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para enviar datos detallados de un vehículo en respuestas API.
 * Incluye información combinada (modelo + marca) para facilitar el consumo.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarResponse {

    private Long id;

    private String licensePlate;

    private String modelName;

    private String brandName;

    private Long userId;

    private String userEmail;

    private Integer year;

    private String color;

    private BigDecimal basePrice;

    private BigDecimal mileage;

    private String status;

    private LocalDate registrationDate;
}
