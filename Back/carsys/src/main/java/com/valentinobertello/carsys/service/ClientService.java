package com.valentinobertello.carsys.service;

import com.valentinobertello.carsys.dtos.client.ClientResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ClientService {

    Page<ClientResponse> getClientsPageByFilter(String filter, String name, Pageable pageable);

    List<ClientResponse> searchClientsByFilter(String filter, String name);

    Boolean existsPhone(String phone, String name);

    Boolean existsDni(String dni, String name);

}
