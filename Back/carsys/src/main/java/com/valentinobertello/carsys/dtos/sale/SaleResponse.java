package com.valentinobertello.carsys.dtos.sale;

import com.valentinobertello.carsys.dtos.car.CarResponse;
import com.valentinobertello.carsys.dtos.client.ClientResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para enviar datos detallados de una venta en respuestas API.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleResponse {
    private Long id;
    private CarResponse car;
    private ClientResponse client;
    private LocalDateTime saleDate;
    private BigDecimal salePrice;
}
