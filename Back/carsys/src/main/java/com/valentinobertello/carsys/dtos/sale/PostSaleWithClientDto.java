package com.valentinobertello.carsys.dtos.sale;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para recibir datos al insertar una venta y un cliente al mismo tiempo.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostSaleWithClientDto {

    @NotNull
    private Long carId;
    @NotNull
    @DecimalMin("0.0")
    private BigDecimal salePrice;

    @NotBlank
    @Size(max = 50)
    private String clientName;
    @NotBlank
    @Size(max = 50)
    private String clientLastName;

    private String clientPhone;
    @Pattern(
            regexp = "^[0-9]{7,8}$",
            message = "DNI inválido. Debe tener entre 7 y 8 dígitos."
    )
    private String clientDni;
}
