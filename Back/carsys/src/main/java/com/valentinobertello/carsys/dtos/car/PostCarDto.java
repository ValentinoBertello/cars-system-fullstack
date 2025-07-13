package com.valentinobertello.carsys.dtos.car;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para recibir datos al crear un auto.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostCarDto {

    @Pattern(
            regexp = "^([A-Za-z]{2}\\s?\\d{3}\\s?[A-Za-z]{2}|[A-Za-z]{3}\\s?\\d{3})$",
            message = "The license plate must follow the format 'AB 123 CD' or 'NVZ 087'"
    )
    private String licensePlate;

    @NotNull(message = "The model cannot be null")
    private Long modelId;

    @NotBlank(message = "The user email cannot be empty")
    private String userEmail;

    @Min(value = 1800, message = "The year must be 1886 or later")
    @Max(value = 2100, message = "The year must be 2100 or earlier")
    private Integer year;

    @NotBlank(message = "The color cannot be empty")
    private String color;

    @NotNull(message = "The price cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "The price must be greater than 0")
    private BigDecimal basePrice;

    @Min(value = 0, message = "The kilometers must be 0 or greater")
    private BigDecimal mileage;
}
