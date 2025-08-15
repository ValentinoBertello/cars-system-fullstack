package com.valentinobertello.carsys.dtos.sale;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para recibir datos al insertar una venta.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostSaleDto {

    @NotNull
    private Long carId;
    @NotNull
    @DecimalMin("0.0")
    private BigDecimal salePrice;
    private Long clientId;
}
