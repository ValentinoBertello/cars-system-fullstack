package com.valentinobertello.carsys.config;

import com.valentinobertello.carsys.dtos.client.ClientResponse;
import com.valentinobertello.carsys.entities.client.ClientEntity;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * Clase de utilidad para pruebas unitarias y de integración relacionadas con clientes.
 * Proporciona datos de prueba predefinidos (mock data).
 */
public class ClientTestHelper {

    // --- ClientEntity ---
    public static final ClientEntity CLIENT_ENTITY_1 = ClientEntity.builder()
            .id(1L)
            .name("Valentino")
            .lastName("Bertello")
            .phone("3541660812")
            .dni("45591511")
            .registrationDate(LocalDate.of(2023, 5, 10))
            .user(UserTestHelper.USER_ENTITY_1)
            .build();

    public static final ClientEntity CLIENT_ENTITY_2 = ClientEntity.builder()
            .id(2L)
            .name("Fernando")
            .lastName("Cotella")
            .phone("3541758221")
            .dni("44785221")
            .registrationDate(LocalDate.of(2022, 8, 20))
            .user(UserTestHelper.USER_ENTITY_1)
            .build();

    public static final ClientEntity CLIENT_ENTITY_3 = ClientEntity.builder()
            .id(3L)
            .name("Leo")
            .lastName("Fochi")
            .phone("351526889")
            .dni("39695854")
            .registrationDate(LocalDate.of(2024, 2, 14))
            .user(UserTestHelper.USER_ENTITY_1)
            .build();

    // --- ClientResponse ---
    public static final ClientResponse CLIENT_RESPONSE_1 = ClientResponse.builder()
            .id(1L)
            .name("Valentino")
            .lastName("Bertello")
            .phone("3541660812")
            .dni("45591511")
            .registrationDate(LocalDate.of(2023, 5, 10))
            .build();

    public static final ClientResponse CLIENT_RESPONSE_2 = ClientResponse.builder()
            .id(2L)
            .name("Fernando")
            .lastName("Cotella")
            .phone("3541758221")
            .dni("44785221")
            .registrationDate(LocalDate.of(2022, 8, 20))
            .build();

    public static final ClientResponse CLIENT_RESPONSE_3 = ClientResponse.builder()
            .id(3L)
            .name("Leo")
            .lastName("Fochi")
            .phone("351526889")
            .dni("39695854")
            .registrationDate(LocalDate.of(2024, 2, 14))
            .build();

    public static final List<ClientEntity> CLIENT_ENTITIES = Arrays.asList(
            CLIENT_ENTITY_1,
            CLIENT_ENTITY_2,
            CLIENT_ENTITY_3
    );

    public static final List<ClientResponse> CLIENT_RESPONSES = Arrays.asList(
            CLIENT_RESPONSE_1,
            CLIENT_RESPONSE_2,
            CLIENT_RESPONSE_3
    );
}
