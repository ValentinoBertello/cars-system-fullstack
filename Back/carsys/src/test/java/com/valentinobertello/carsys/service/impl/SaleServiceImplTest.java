package com.valentinobertello.carsys.service.impl;

import com.valentinobertello.carsys.config.ClientTestHelper;
import com.valentinobertello.carsys.dtos.sale.SaleResponse;
import com.valentinobertello.carsys.entities.car.CarEntity;
import com.valentinobertello.carsys.entities.sale.SaleEntity;
import com.valentinobertello.carsys.enums.CarStatus;
import com.valentinobertello.carsys.mapper.ClientDataMapper;
import com.valentinobertello.carsys.mapper.SaleDataMapper;
import com.valentinobertello.carsys.repository.auth.UserRepository;
import com.valentinobertello.carsys.repository.car.CarRepository;
import com.valentinobertello.carsys.repository.client.ClientRepository;
import com.valentinobertello.carsys.repository.sale.SaleRepository;
import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.awt.print.Pageable;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.valentinobertello.carsys.config.CarTestHelper.*;
import static com.valentinobertello.carsys.config.SaleTestHelper.*;
import static com.valentinobertello.carsys.config.UserTestHelper.*;
import static com.valentinobertello.carsys.config.ClientTestHelper.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Clase para tester solo la lógica de negocio de SaleService
 * **/
@ExtendWith(MockitoExtension.class)
class SaleServiceImplTest {

    @Mock
    private SaleRepository saleRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private CarRepository carRepository;
    @Mock
    private SaleDataMapper saleDataMapper;
    @Mock
    private ClientDataMapper clientDataMapper;
    @InjectMocks
    private SaleServiceImpl saleService;

    private final String USER_EMAIL = "test@example.com";

    /**
     * Prueba la creación exitosa de una venta con nuevo cliente.
     */
    @Test
    void saveSaleWithClient() {
        // Simulaciones
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(USER_ENTITY_1));
        when(carRepository.findById(POST_SALE_WITH_CLIENT_DTO_1.getCarId())).thenReturn(Optional.of(CAR_ENTITY_1));
        when(clientRepository.existsByDni(POST_SALE_WITH_CLIENT_DTO_1.getClientDni())).thenReturn(false);
        when(clientRepository.existsByPhone(POST_SALE_WITH_CLIENT_DTO_1.getClientPhone())).thenReturn(false);
        when(clientDataMapper.mapRequestToClientEntity(POST_SALE_WITH_CLIENT_DTO_1, USER_ENTITY_1)).thenReturn(CLIENT_ENTITY_1);
        when(clientRepository.save(CLIENT_ENTITY_1)).thenReturn(CLIENT_ENTITY_1);
        when(carRepository.save(CAR_ENTITY_1)).thenReturn(CAR_ENTITY_1);
        when(saleRepository.save(any(SaleEntity.class))).thenReturn(SALE_ENTITY_1);
        when(saleDataMapper.mapSaleEntityToSaleResponse(SALE_ENTITY_1)).thenReturn(SALE_RESPONSE_1);

        // Llamamos al metodo del servicio
        SaleResponse result = saleService.saveSaleWithClient(POST_SALE_WITH_CLIENT_DTO_1, USER_EMAIL);

        // Verficamos
        assertNotNull(result);
        assertEquals(SALE_RESPONSE_1, result);
        assertEquals(CarStatus.VENDIDO, CAR_ENTITY_1.getStatus());
    }

    /**
     * Prueba la creación exitosa de una venta con cliente existente.
     */
    @Test
    void saveSale() {
        // Simulaciones
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(USER_ENTITY_2));

        when(carRepository.findById(POST_SALE_DTO_1.getCarId())).thenReturn(Optional.of(CAR_ENTITY_2));

        when(clientRepository.findById(POST_SALE_DTO_1.getClientId())).thenReturn(Optional.of(CLIENT_ENTITY_2));

        when(carRepository.save(any())).thenReturn(CAR_ENTITY_2);

        when(saleRepository.save(any())).thenReturn(SALE_ENTITY_2);

        when(saleDataMapper.mapSaleEntityToSaleResponse(SALE_ENTITY_2)).thenReturn(SALE_RESPONSE_2);

        // Llamamos al metodo del servicio
        SaleResponse result = saleService.saveSale(
                POST_SALE_DTO_1,
                USER_EMAIL
        );

        // Verificaciones
        assertEquals(SALE_RESPONSE_2, result);
        verify(carRepository).save(argThat(car -> car.getStatus() == CarStatus.VENDIDO));
    }

    /**
     * Prueba que se obtengan correctamente las ventas asociadas a un DNI de cliente existente.
     */
    @Test
    void getSalesByClientDni() {
        String dni = ClientTestHelper.CLIENT_ENTITY_1.getDni();
        List<SaleEntity> clientSales = List.of(SALE_ENTITY_1);
        List<SaleResponse> expectedResponse = List.of(SALE_RESPONSE_1);

        when(saleRepository.findAllByClientDni(dni)).thenReturn(clientSales);
        when(saleDataMapper.mapSaleEntitiesToSaleResponses(clientSales)).thenReturn(expectedResponse);

        List<SaleResponse> result = saleService.getSalesByClientDni(dni);

        assertEquals(1, result.size());
        assertEquals(expectedResponse, result);
        verify(saleRepository).findAllByClientDni(dni);
    }

    /**
     * Prueba que se lance excepción cuando se intenta vender un auto ya vendido.
     */
    @Test
    void getCarById_ShouldThrowWhenCarAlreadySold() {
        CarEntity soldCar = CAR_ENTITY_3; // Este auto está VENDIDO en el helper
        when(carRepository.findById(soldCar.getId()))
                .thenReturn(Optional.of(soldCar));
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> saleService.getCarById(soldCar.getId()));
        assertEquals("Auto ya vendido.", exception.getMessage());
    }

    /**
     * Prueba validación de cliente cuando ya existe un cliente con el mismo DNI.
     */
    @Test
    void validateClient_ShouldThrowWhenClientDniExists() {
        when(clientRepository.existsByDni(POST_SALE_WITH_CLIENT_DTO_1.getClientDni()))
                .thenReturn(true);
        EntityExistsException exception = assertThrows(EntityExistsException.class,
                () -> saleService.validateClient(POST_SALE_WITH_CLIENT_DTO_1));
        assertEquals("Ya existe un cliente con DNI: " + POST_SALE_WITH_CLIENT_DTO_1.getClientDni(), exception.getMessage());
    }

    /**
     * Prueba validación de cliente cuando ya existe un cliente con el mismo teléfono.
     */
    @Test
    void validateClient_ShouldThrowWhenClientPhoneExists() {
        when(clientRepository.existsByDni(POST_SALE_WITH_CLIENT_DTO_1.getClientDni())).thenReturn(false);
        when(clientRepository.existsByPhone(POST_SALE_WITH_CLIENT_DTO_1.getClientPhone()))
                .thenReturn(true);
        EntityExistsException exception = assertThrows(EntityExistsException.class,
                () -> saleService.validateClient(POST_SALE_WITH_CLIENT_DTO_1));
        assertEquals("Ya existe un cliente con teléfono: " + POST_SALE_WITH_CLIENT_DTO_1.getClientPhone(), exception.getMessage());
    }
}