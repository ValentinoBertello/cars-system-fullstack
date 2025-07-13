package com.valentinobertello.carsys.service;

import com.valentinobertello.carsys.dtos.auth.UserResponse;
import com.valentinobertello.carsys.mapper.UserDataMapper;
import com.valentinobertello.carsys.repository.auth.RoleRepository;
import com.valentinobertello.carsys.repository.auth.UserRepository;
import com.valentinobertello.carsys.service.impl.UserServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

import static com.valentinobertello.carsys.config.UserTestHelper.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Clase para tester solo la lógica de negocio de UserService
 * **/
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserDataMapper userDataMapper;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    /**
     * Prueba para obtener todos los usuarios
     */
    @Test
    void getAllUsers() {
        when(userRepository.findAll()).thenReturn(ALL_USER_ENTITIES);
        when(userDataMapper.mapUserEntitiesToUserResponses(ALL_USER_ENTITIES)).thenReturn(ALL_USER_RESPONSES);
        List<UserResponse> result = this.userServiceImpl.getAllUsers();

        assertEquals("Valentino", result.get(0).getName());
        assertEquals("María", result.get(1).getName());
    }

    /**
     * Prueba para guardar un nuevo usuario exitosamente
     * Verifica el flujo completo de registro
     */
    @Test
    void saveUser() {
        when(userRepository.existsByEmail(USER_REQUEST_1.getEmail())).thenReturn(false);
        when(roleRepository.findByNameIn(any())).thenReturn(USER_ENTITY_1.getRoles());
        when(userDataMapper.mapUserRequestToUserEntity(any(),any(),any())).thenReturn(USER_ENTITY_1);
        when(userRepository.save(USER_ENTITY_1)).thenReturn(USER_ENTITY_1);
        when(userDataMapper.mapUserEntityToUserResponse(USER_ENTITY_1)).thenReturn(USER_RESPONSE_1);

        UserResponse result = this.userServiceImpl.saveUser(USER_REQUEST_1);

        assertEquals("valentino.bertello@example.com", result.getEmail());
    }

    /**
     * Prueba para verificar el manejo de excepción cuando no se encuentran roles
     */
    @Test
    void saveUserEntityNotFoundException() {
        when(roleRepository.findByNameIn(any())).thenReturn(new ArrayList<>());
        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> userServiceImpl.saveUser(USER_REQUEST_2));
    }

    /**
     * Prueba para verificar el manejo de excepción cuando el email ya existe
     */
    @Test
    void saveUserIllegalArgumentException() {
        when(userRepository.existsByEmail(any())).thenReturn(true);
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> userServiceImpl.saveUser(USER_REQUEST_2));
    }

    /**
     * Prueba para obtener un usuario por ID
     */
    @Test
    void getUserById() {
        when(userRepository.findById(100L)).thenReturn(java.util.Optional.of(USER_ENTITY_1));
        when(userDataMapper.mapUserEntityToUserResponse(USER_ENTITY_1))
                .thenReturn(USER_RESPONSE_1);

        UserResponse resp = userServiceImpl.getUserById(100L);
        assertNotNull(resp);
        assertEquals("Valentino", resp.getName());
        verify(userRepository).findById(100L);
    }

    /**
     * Prueba para obtener un usuario por email
     * Verifica el mapeo correcto y la llamada al repositorio
     */
    @Test
    void getUserByEmail() {
        when(userRepository.findByEmail("valentino.bertello@example.com"))
                .thenReturn(java.util.Optional.of(USER_ENTITY_1));
        when(userDataMapper.mapUserEntityToUserResponse(USER_ENTITY_1))
                .thenReturn(USER_RESPONSE_1);

        UserResponse resp = userServiceImpl.getUserByEmail("valentino.bertello@example.com");
        assertEquals("Valentino", resp.getName());
        verify(userRepository).findByEmail("valentino.bertello@example.com");
    }
}