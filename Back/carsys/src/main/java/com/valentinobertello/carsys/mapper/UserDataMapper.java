package com.valentinobertello.carsys.mapper;
import com.valentinobertello.carsys.dtos.auth.UserRequest;
import com.valentinobertello.carsys.dtos.auth.UserResponse;
import com.valentinobertello.carsys.entities.auth.RoleEntity;
import com.valentinobertello.carsys.entities.auth.UserEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * La clase "UserDataMapper" se encarga de mapear objetos "request", "entity" y "response"
 * entre ellos.
 */
@Component
public class UserDataMapper {

    /**
     * Mapea una entidad de usuario {@link UserEntity} a su DTO de respuesta {@link UserResponse}.
     */
    public UserResponse mapUserEntityToUserResponse(UserEntity userEntity) {
        return UserResponse.builder()
                .id(userEntity.getId())
                .name(userEntity.getName())
                .lastname(userEntity.getLastname())
                .email(userEntity.getEmail())
                .active(userEntity.getActive())
                .roles(mapRolesToStringList(userEntity.getRoles()))
                .build();
    }

    /**
     * Convierte una lista de entidades de rol {@link RoleEntity} en una lista de nombres de rol.
     */
    private List<String> mapRolesToStringList(List<RoleEntity> roles) {
        return roles.stream()
                .map(RoleEntity::getName)
                .toList();
    }

    /**
     * Mapea una lista de entidades de usuario a una lista de DTOs de respuesta.
     */
    public List<UserResponse> mapUserEntitiesToUserResponses(List<UserEntity> userEntities) {
        List<UserResponse> responses = new ArrayList<>();
        for (UserEntity uE : userEntities){
            responses.add(this.mapUserEntityToUserResponse(uE));
        }
        return responses;
    }

    /**
     * Mapea los datos recibidos en el DTO de petición {@link UserRequest} a una nueva entidad de usuario.
     *
     * @param userRequest       DTO con los datos ingresados por el cliente (nombre, email, etc.)
     * @param roles             lista de entidades de rol asignadas al usuario
     * @param encodedPassword   contraseña ya cifrada para almacenar en la entidad
     */
    public UserEntity mapUserRequestToUserEntity(UserRequest userRequest, List<RoleEntity> roles,
                                                 String encodedPassword) {
        return UserEntity.builder()
                .name(userRequest.getName())
                .lastname(userRequest.getLastname())
                .email(userRequest.getEmail())
                .password(encodedPassword)
                .roles(roles)
                .active(true)
                .build();
    }
}
