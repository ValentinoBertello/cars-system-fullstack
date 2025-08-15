package com.valentinobertello.carsys.config;

import com.valentinobertello.carsys.dtos.sale.PostSaleDto;
import com.valentinobertello.carsys.dtos.sale.PostSaleWithClientDto;
import com.valentinobertello.carsys.dtos.sale.SaleResponse;
import com.valentinobertello.carsys.entities.sale.SaleEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Clase de utilidad para pruebas unitarias y de integración relacionadas con "sales".
 * Proporciona datos de prueba predefinidos (mock data).
 */
public class SaleTestHelper {

    // --- SaleEntity ---
    public static final SaleEntity SALE_ENTITY_1 = SaleEntity.builder()
            .id(1L)
            .car(CarTestHelper.CAR_ENTITY_1)
            .client(ClientTestHelper.CLIENT_ENTITY_1)
            .saleDate(LocalDateTime.of(2024, 7, 10, 14, 30))
            .salePrice(new BigDecimal("25000000"))
            .build();

    public static final SaleEntity SALE_ENTITY_2 = SaleEntity.builder()
            .id(2L)
            .car(CarTestHelper.CAR_ENTITY_2)
            .client(ClientTestHelper.CLIENT_ENTITY_2)
            .saleDate(LocalDateTime.of(2024, 8, 5, 11, 0))
            .salePrice(new BigDecimal("23000000"))
            .build();

    public static final SaleEntity SALE_ENTITY_3 = SaleEntity.builder()
            .id(3L)
            .car(CarTestHelper.CAR_ENTITY_3)
            .client(ClientTestHelper.CLIENT_ENTITY_3)
            .saleDate(LocalDateTime.of(2024, 9, 1, 16, 15))
            .salePrice(new BigDecimal("24000000"))
            .build();

    // --- SaleResponse ---
    public static final SaleResponse SALE_RESPONSE_1 = SaleResponse.builder()
            .id(1L)
            .car(CarTestHelper.CAR_RESPONSE_1)
            .client(ClientTestHelper.CLIENT_RESPONSE_1)
            .saleDate(LocalDateTime.of(2024, 1, 15, 10, 30))
            .salePrice(new BigDecimal("22000000"))
            .build();

    public static final SaleResponse SALE_RESPONSE_2 = SaleResponse.builder()
            .id(2L)
            .car(CarTestHelper.CAR_RESPONSE_2)
            .client(ClientTestHelper.CLIENT_RESPONSE_2)
            .saleDate(LocalDateTime.of(2024, 2, 20, 15, 45))
            .salePrice(new BigDecimal("20500000"))
            .build();

    public static final SaleResponse SALE_RESPONSE_3 = SaleResponse.builder()
            .id(3L)
            .car(CarTestHelper.CAR_RESPONSE_3)
            .client(ClientTestHelper.CLIENT_RESPONSE_3)
            .saleDate(LocalDateTime.of(2024, 3, 5, 11, 0))
            .salePrice(new BigDecimal("21500000"))
            .build();

    // Listados
    public static final List<SaleEntity> SALE_ENTITIES = Arrays.asList(
            SALE_ENTITY_1,
            SALE_ENTITY_2,
            SALE_ENTITY_3
    );

    public static final List<SaleResponse> SALE_RESPONSES = Arrays.asList(
            SALE_RESPONSE_1,
            SALE_RESPONSE_2,
            SALE_RESPONSE_3
    );

    // --- DTOs de creación con cliente existente ---
    public static final PostSaleDto POST_SALE_DTO_1 = PostSaleDto.builder()
            .carId(CarTestHelper.CAR_ENTITY_1.getId())
            .salePrice(new BigDecimal("21000000"))
            .clientId(ClientTestHelper.CLIENT_ENTITY_1.getId())
            .build();

    // --- DTOs de creación con nuevo cliente ---
    public static final PostSaleWithClientDto POST_SALE_WITH_CLIENT_DTO_1 = PostSaleWithClientDto.builder()
            .carId(CarTestHelper.CAR_ENTITY_3.getId())
            .salePrice(new BigDecimal("22500000"))
            .clientName("Nuevo")
            .clientLastName("Comprador")
            .clientPhone("3512345678")
            .clientDni("40123456")
            .build();
}
