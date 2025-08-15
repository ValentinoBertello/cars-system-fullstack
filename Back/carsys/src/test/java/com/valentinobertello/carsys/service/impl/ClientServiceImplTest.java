package com.valentinobertello.carsys.service.impl;

import com.valentinobertello.carsys.config.ClientTestHelper;
import com.valentinobertello.carsys.dtos.client.ClientResponse;
import com.valentinobertello.carsys.entities.client.ClientEntity;
import com.valentinobertello.carsys.mapper.ClientDataMapper;
import com.valentinobertello.carsys.repository.client.ClientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Clase para tester solo la lógica de negocio de ClientService
 * **/
@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {

    @Mock
    private ClientRepository clientRepository;
    @Mock
    private ClientDataMapper clientDataMapper;
    @InjectMocks
    private ClientServiceImpl clientService;

    @Test
    void getClientsPageByFilter() {

        // Creamos la page de entidades y la de responses
        Page<ClientEntity> pageEntities = new PageImpl<>(ClientTestHelper.CLIENT_ENTITIES);
        when(clientRepository.searchByFilter("45", "Valentino", PageRequest.of(0, 10)))
                .thenReturn(pageEntities);

        Page<ClientResponse> pageResponses = new PageImpl<>(ClientTestHelper.CLIENT_RESPONSES);
        when(clientDataMapper.mapClientEntitiesPageToClientResponsesPage(pageEntities))
                .thenReturn(pageResponses);

        // Llamamos al método del servicio
        Page<ClientResponse> result = clientService.getClientsPageByFilter(
                "45", "Valentino", PageRequest.of(0, 10));

        // Verificamos que la page tenga lo esperado
        assertEquals("45591511", result.getContent().get(0).getDni());
        assertEquals(3, result.getContent().size());
        verify(clientRepository).searchByFilter(anyString(), anyString(), any(Pageable.class));
        verify(clientDataMapper).mapClientEntitiesPageToClientResponsesPage(pageEntities);
    }

    @Test
    void searchClientsByFilter() {
        when(clientRepository.searchByFilter("45", "valentino@gmail.com"))
                .thenReturn(ClientTestHelper.CLIENT_ENTITIES);

        when(clientDataMapper.mapClientEntityListToClientResponseList(ClientTestHelper.CLIENT_ENTITIES))
                .thenReturn(ClientTestHelper.CLIENT_RESPONSES);

        List<ClientResponse> result = clientService.searchClientsByFilter(
                "45", "valentino@gmail.com");

        assertEquals(ClientTestHelper.CLIENT_RESPONSES.size(), result.size());
        verify(clientRepository).searchByFilter("45", "valentino@gmail.com");
        verify(clientDataMapper).mapClientEntityListToClientResponseList(ClientTestHelper.CLIENT_ENTITIES);
    }

    @Test
    void existsPhone() {
        when(clientRepository.existsByPhoneAndUserEmail("12345678", "valentino@gmail.com"))
                .thenReturn(true);
        Boolean phoneExists = clientService.existsPhone("12345678", "valentino@gmail.com");
        assertTrue(phoneExists);
    }

    @Test
    void existsDni() {
        when(clientRepository.existsByDniAndUserEmail("45591511", "valentino@gmail.com"))
                .thenReturn(true);
        Boolean dniExists = clientService.existsDni("45591511", "valentino@gmail.com");
        assertTrue(dniExists);
    }
}