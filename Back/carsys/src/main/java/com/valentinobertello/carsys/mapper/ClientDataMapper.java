package com.valentinobertello.carsys.mapper;

import com.valentinobertello.carsys.dtos.client.ClientResponse;
import com.valentinobertello.carsys.dtos.sale.PostSaleWithClientDto;
import com.valentinobertello.carsys.entities.auth.UserEntity;
import com.valentinobertello.carsys.entities.client.ClientEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * La clase "ClientDataMapper" se encarga de mapear objetos "request", "entity" y "response"
 * entre ellos.
 */
@Component
public class ClientDataMapper {

    /**
     * Crea una entidad de cliente {@link ClientEntity} a partir de un {@link PostSaleWithClientDto}.
     */
    public ClientEntity mapRequestToClientEntity(PostSaleWithClientDto dto, UserEntity user) {
        ClientEntity client = new ClientEntity();
        client.setDni(dto.getClientDni());
        client.setName(dto.getClientName());
        client.setLastName(dto.getClientLastName());
        client.setPhone(dto.getClientPhone());
        client.setRegistrationDate(LocalDate.now());
        client.setUser(user);
        return client;
    }

    /**
     * Mapea una entidad de cliente {@link ClientEntity} a su DTO de respuesta {@link ClientResponse}.
     */
    public ClientResponse mapClientEntityToClientResponse(ClientEntity c) {
        return ClientResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .lastName(c.getLastName())
                .dni(c.getDni())
                .phone(c.getPhone())
                .registrationDate(c.getRegistrationDate())
                .build();
    }

    /**
     * Mapea lista de entidades a lista de respuesta.
     */
    public List<ClientResponse> mapClientEntityListToClientResponseList(List<ClientEntity> clientEntityList) {
        return clientEntityList.stream()
                .map(this::mapClientEntityToClientResponse)
                .collect(Collectors.toList());
    }

    /**
     * Mapea una "Page" de "clientEntity" a una "Page" de DTOs de respuesta.
     */
    public Page<ClientResponse> mapClientEntitiesPageToClientResponsesPage(Page<ClientEntity> clientEntitiesPage) {
        // Obtenemos la lista de entidades del page
        List<ClientEntity> clientEntities = clientEntitiesPage.getContent();

        // Creamos lista vacía de clientResponses
        List<ClientResponse> clientResponses = new ArrayList<>();

        // Iteramos sobre cada entity y la convertimos en response
        for (ClientEntity entity : clientEntities) {
            ClientResponse response = this.mapClientEntityToClientResponse(entity);
            clientResponses.add(response);
        }

        // Creamos y retornamos la página con las responses
        return new PageImpl<>(
                clientResponses,
                clientEntitiesPage.getPageable(),
                clientEntitiesPage.getTotalElements()
        );
    }
}
