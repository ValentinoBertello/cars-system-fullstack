package com.valentinobertello.carsys.dtos.car;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para recibir datos y editar un auto.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCarDto {

    @NotNull
    private Long id;

    @Size(max = 30)
    private String color;

    @DecimalMin("0.00")
    private BigDecimal basePrice;

    @DecimalMin("0.00")
    private BigDecimal mileage;
}
