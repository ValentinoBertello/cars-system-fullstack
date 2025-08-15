package com.valentinobertello.carsys.service.impl;

import com.valentinobertello.carsys.config.CarTestHelper;
import com.valentinobertello.carsys.dtos.car.CarResponse;
import com.valentinobertello.carsys.dtos.car.ModelResponse;
import com.valentinobertello.carsys.dtos.car.UpdateCarDto;
import com.valentinobertello.carsys.entities.car.BrandEntity;
import com.valentinobertello.carsys.entities.car.CarEntity;
import com.valentinobertello.carsys.entities.car.ModelEntity;
import com.valentinobertello.carsys.mapper.CarDataMapper;
import com.valentinobertello.carsys.repository.auth.UserRepository;
import com.valentinobertello.carsys.repository.car.BrandRepository;
import com.valentinobertello.carsys.repository.car.CarRepository;
import com.valentinobertello.carsys.repository.car.ModelRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.valentinobertello.carsys.config.CarTestHelper.*;
import static com.valentinobertello.carsys.config.UserTestHelper.USER_ENTITY_1;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Clase para tester solo la lógica de negocio de CarService
 * **/
@ExtendWith(MockitoExtension.class)
class CarServiceImplTest {

    @Mock
    private CarRepository carRepository;
    @Mock
    private BrandRepository brandRepository;
    @Mock
    private ModelRepository modelRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CarDataMapper carDataMapper;

    @InjectMocks
    private CarServiceImpl carService;

    /**
     * Creación de un auto exitosamente
     * */
    @Test
    void createCar_success() {
        // Configuramos el mock para devolver un modelo Fiat Panda
        when(modelRepository.findById(POST_CAR_DTO_1.getModelId()))
                .thenReturn(Optional.of(CarTestHelper.MODEL_FIAT_PANDA));

        // Simulamos la búsqueda de usuario por email
        when(userRepository.findByEmail(POST_CAR_DTO_1.getUserEmail()))
                .thenReturn(Optional.of(USER_ENTITY_1));

        // Simulamos que la patente no existe en la bd
        when(carRepository.existsByLicensePlate(POST_CAR_DTO_1.getLicensePlate()))
                .thenReturn(false);

        // Simulamos mapeo de postCar a carEntity
        when(carDataMapper.mapCarRequestToCarEntity(POST_CAR_DTO_1, CarTestHelper.MODEL_FIAT_PANDA, USER_ENTITY_1))
                .thenReturn(CAR_ENTITY_1);

        // Simulamos la operación de guardado en la base de datos
        when(carRepository.save(CAR_ENTITY_1)).thenReturn(CAR_ENTITY_1);

        // Mapeamos la entidad guardada a la respuesta esperada
        when(carDataMapper.mapCarEntityToCarResponse(any()))
                .thenReturn(CarTestHelper.CAR_RESPONSE_1);

        // Ejecutamos el método a probar
        CarResponse response = carService.createCar(POST_CAR_DTO_1, USER_ENTITY_1.getEmail());

        // Validaciones
        assertNotNull(response);
        assertEquals(CAR_ENTITY_1.getId(), response.getId());
        verify(carRepository).save(any());
    }

    /**
     * Prueba de createCar con modelo no encontrado, lanza EntityNotFoundException
     * */
    @Test
    void createCar_modelNotFound() {
        when(modelRepository.findById(POST_CAR_DTO_1.getModelId())).thenReturn(Optional.empty());
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> carService.createCar(POST_CAR_DTO_1, USER_ENTITY_1.getEmail()));
        assertTrue(ex.getMessage().contains("Modelo no encontrado con ID: " + POST_CAR_DTO_1.getModelId()));
    }

    /**
     * Prueba de createCar con usuario no encontrado, lanza EntityNotFoundException
     * */
    @Test
    void createCar_userNotFound() {
        when(modelRepository.findById(POST_CAR_DTO_1.getModelId()))
                .thenReturn(Optional.of(CarTestHelper.MODEL_FIAT_PANDA));
        when(userRepository.findByEmail(POST_CAR_DTO_1.getUserEmail()))
                .thenReturn(Optional.empty());
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> carService.createCar(POST_CAR_DTO_1, USER_ENTITY_1.getEmail()));
        assertTrue(ex.getMessage().contains("Usuario no encontrado con email: " + POST_CAR_DTO_1.getUserEmail()));
    }

    /**
     * Prueba de createCar con usuario distinto al enviado del front
     * lanza RuntimeException por permiso
     * */
    @Test
    void createCar_permissionDenied() {
        when(modelRepository.findById(POST_CAR_DTO_1.getModelId()))
                .thenReturn(Optional.of(CarTestHelper.MODEL_FIAT_PANDA));
        when(userRepository.findByEmail(POST_CAR_DTO_1.getUserEmail()))
                .thenReturn(Optional.of(USER_ENTITY_1));
        String otherEmail = "otro@example.com";
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> carService.createCar(POST_CAR_DTO_1, otherEmail));
        assertEquals("No tienes permiso para guardar este vehículo", ex.getMessage());
    }

    /**
     * Prueba de createCar con patente existente lanza EntityExistsException
     * */
    @Test
    void createCar_licenseExists() {
        when(modelRepository.findById(POST_CAR_DTO_1.getModelId()))
                .thenReturn(Optional.of(MODEL_FIAT_PANDA));
        when(userRepository.findByEmail(POST_CAR_DTO_1.getUserEmail()))
                .thenReturn(Optional.of(USER_ENTITY_1));
        when(carRepository.existsByLicensePlate(POST_CAR_DTO_1.getLicensePlate()))
                .thenReturn(true);

        EntityExistsException ex = assertThrows(EntityExistsException.class,
                () -> carService.createCar(POST_CAR_DTO_1, USER_ENTITY_1.getEmail()));
        assertTrue(ex.getMessage().contains("La patente '" + POST_CAR_DTO_1.getLicensePlate()));
    }

    /**
     * Test de búsqueda de autos con filtros y mapeo de página
     * */
    @Test
    void searchCarsByFilters() {
        Page<CarEntity> pageEntities = new PageImpl<>(ALL_CAR_ENTITIES);
        // Simulamos la operación de Find All
        when(carRepository.findAll(Mockito.<Specification<CarEntity>>any(), any(Pageable.class)))
                .thenReturn(pageEntities);

        Page<CarResponse> pageResponses = new PageImpl<>(ALL_CAR_RESPONSES);
        // Mapeamos la page de "entities" a "responses"
        when(carDataMapper.mapCarEntitiesPageToCarResponsesPage(pageEntities))
                .thenReturn(pageResponses);

        // Ejecutamos metodo
        Page<CarResponse> result = carService.getCarsPageByFilters(
                "AB", "Fiat", "Panda", USER_ENTITY_1.getEmail(), PageRequest.of(0, 10));

        // Validaciones
        assertEquals(3, result.getContent().size());
        verify(carRepository).findAll(Mockito.<Specification<CarEntity>>any(), any(Pageable.class));
    }

    /**
     * Test de actualización exitosa: verifica que el precio y el kilometraje se actualicen correctamente
     * */
    @Test
    void updateCar_success() {
        // Arrange: reutilizar objetos predefinidos en CarTestHelper
        UpdateCarDto request = CarTestHelper.UPDATE_CAR_DTO_1;
        CarEntity existing = CarTestHelper.CAR_ENTITY_1;
        CarEntity saved = CarTestHelper.SAVED_CAR_ENTITY_1;
        CarResponse expected = CarTestHelper.UPDATED_CAR_RESPONSE_1;

        when(carRepository.findById(request.getId())).thenReturn(Optional.of(existing));
        when(carRepository.save(existing)).thenReturn(saved);
        when(carDataMapper.mapCarEntityToCarResponse(saved)).thenReturn(expected);

        // Act
        CarResponse response = carService.updateCar(request, existing.getUser().getEmail());

        // Assert: validar que la respuesta coincide con el esperado del helper
        assertNotNull(response);
        assertEquals(expected, response);
        verify(carRepository).save(existing);
    }


    /**
     * Test de obtención de todas los modelos
     * **/
    @Test
    void getAllModels() {
        List<ModelEntity> models = Arrays.asList(
                CarTestHelper.MODEL_FIAT_PANDA,
                CarTestHelper.MODEL_FIAT_500
        );
        when(modelRepository.findAll()).thenReturn(models);
        List<ModelResponse> responses = carService.getAllModels();
        assertEquals(2, responses.size());
        assertEquals("Panda", responses.get(0).getName());
    }

    /**
     * Test de obtención de todas las marcas
     * **/
    @Test
    void getAllBrands() {
        List<BrandEntity> brands = Arrays.asList(
                CarTestHelper.BRAND_FIAT,
                CarTestHelper.BRAND_RENAULT
        );
        when(brandRepository.findAll()).thenReturn(brands);
        List<BrandEntity> result = carService.getAllBrands();
        assertEquals(2, result.size());
        assertTrue(result.contains(CarTestHelper.BRAND_RENAULT));
    }

    /**
     * Test de existencia de patente para un usuario
     * **/
    @Test
    void existsLicensePlate() {
        String plate = "AB 123 CD";
        String email = USER_ENTITY_1.getEmail();
        when(carRepository.existsByLicensePlateAndUserEmail(plate, email)).thenReturn(true);
        Boolean exists = carService.existsLicensePlate(plate, email);
        assertTrue(exists);
    }
}