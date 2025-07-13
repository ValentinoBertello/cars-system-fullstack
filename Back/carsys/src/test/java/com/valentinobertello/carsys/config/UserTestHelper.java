package com.valentinobertello.carsys.config;

import com.valentinobertello.carsys.dtos.auth.UserRequest;
import com.valentinobertello.carsys.dtos.auth.UserResponse;
import com.valentinobertello.carsys.entities.auth.RoleEntity;
import com.valentinobertello.carsys.entities.auth.UserEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Clase de utilidad para pruebas unitarias y de integración relacionadas con usuarios.
 * Proporciona datos de prueba predefinidos (mock data).
 */
public class UserTestHelper {

    /**
     * Roles predefinidos para usar en las pruebas
     * **/
    public static final RoleEntity ROLE_ADMIN = RoleEntity.builder()
            .id(1L)
            .name("ROLE_ADMIN")
            .build();

    public static final RoleEntity ROLE_USER = RoleEntity.builder()
            .id(2L)
            .name("ROLE_ENCARGADO")
            .build();

    /**
     * Usuarios de ejemplo en formato Entity (para pruebas de repositorio o servicio)
     * **/
    public static final UserEntity USER_ENTITY_1 = UserEntity.builder()
            .id(100L)
            .name("Valentino")
            .lastname("Bertello")
            .email("valentino.bertello@example.com")
            .password("PassWord44##")
            .active(true)
            .roles(Arrays.asList(ROLE_ADMIN, ROLE_USER))
            .build();

    public static final UserEntity USER_ENTITY_2 = UserEntity.builder()
            .id(101L)
            .name("María")
            .lastname("Pérez")
            .email("maria.perez@example.com")
            .password("Contraseña123!")
            .active(true)
            .roles(Collections.singletonList(ROLE_USER))
            .build();

    /**
     * Objetos de solicitud (Request DTOs)
     */
    public static final UserRequest USER_REQUEST_1 = UserRequest.builder()
            .name("Valentino")
            .lastname("Bertello")
            .email("valentino.bertello@example.com")
            .password("PassWord44##")
            .roleNames(Arrays.asList("ADMIN", "USER"))
            .build();

    public static final UserRequest USER_REQUEST_2 = UserRequest.builder()
            .name("María")
            .lastname("Pérez")
            .email("maria.perez@example.com")
            .password("Contraseña123!")
            .roleNames(Collections.singletonList("USER"))
            .build();

    public static final UserResponse USER_RESPONSE_1 = UserResponse.builder()
            .id(100L)
            .name("Valentino")
            .lastname("Bertello")
            .email("valentino.bertello@example.com")
            .active(true)
            .roles(Arrays.asList("ADMIN", "USER"))
            .build();

    public static final UserResponse USER_RESPONSE_2 = UserResponse.builder()
            .id(101L)
            .name("María")
            .lastname("Pérez")
            .email("maria.perez@example.com")
            .active(true)
            .roles(Collections.singletonList("USER"))
            .build();

    /**
    * Colecciones predefinidas para pruebas que requieran listados
    * */
    public static final List<UserEntity> ALL_USER_ENTITIES = Arrays.asList(
            USER_ENTITY_1,
            USER_ENTITY_2
    );

    public static final List<UserResponse> ALL_USER_RESPONSES = Arrays.asList(
            USER_RESPONSE_1,
            USER_RESPONSE_2
    );
}