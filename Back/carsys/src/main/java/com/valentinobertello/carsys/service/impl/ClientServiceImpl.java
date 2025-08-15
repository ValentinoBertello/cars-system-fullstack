package com.valentinobertello.carsys.service.impl;

import com.valentinobertello.carsys.dtos.client.ClientResponse;
import com.valentinobertello.carsys.entities.client.ClientEntity;
import com.valentinobertello.carsys.mapper.ClientDataMapper;
import com.valentinobertello.carsys.repository.client.ClientRepository;
import com.valentinobertello.carsys.service.ClientService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final ClientDataMapper clientDataMapper;

    public ClientServiceImpl(ClientRepository clientRepository, ClientDataMapper clientDataMapper) {
        this.clientRepository = clientRepository;
        this.clientDataMapper = clientDataMapper;
    }

    /**
     * Busca clientes según dni, nombre o apellido.
     * @return página de ClientResponse con los resultados filtrados.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ClientResponse> getClientsPageByFilter(String filter, String name, Pageable pageable) {
        Page<ClientEntity> clientEntityPage = this.clientRepository.searchByFilter(filter, name, pageable);
        return this.clientDataMapper.mapClientEntitiesPageToClientResponsesPage(clientEntityPage);
    }

    /**
     * Busca clientes según dni, nombre o apellido.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ClientResponse> searchClientsByFilter(String filter, String userEmail) {
        List<ClientEntity> clientEntityList = this.clientRepository.searchByFilter(filter, userEmail);
        return this.clientDataMapper.mapClientEntityListToClientResponseList(clientEntityList);
    }

    /**
     * Devuelve true si el número de telefono ya existe y false si no existe
     */
    @Override
    @Transactional(readOnly = true)
    public Boolean existsPhone(String phone, String name) {
        return clientRepository.existsByPhoneAndUserEmail(phone, name);
    }

    /**
     * Devuelve true si el dni ya existe y false si no existe
     */
    @Override
    @Transactional(readOnly = true)
    public Boolean existsDni(String dni, String name) {
        return clientRepository.existsByDniAndUserEmail(dni, name);
    }

}
