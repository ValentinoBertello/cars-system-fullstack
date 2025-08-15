package com.valentinobertello.carsys.controllers;

import com.valentinobertello.carsys.dtos.client.ClientResponse;
import com.valentinobertello.carsys.service.ClientService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(originPatterns = "*")
@RestController
@RequestMapping("/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    /**
     * GET /clients/search/page
     * Busca clientes según Busca clientes según dni, nombre o apellido.
     * Ademas se puede especificar el orden ("asc" o "desc" y el atributo).
     * @return página de ClientResponse con los resultados.
     */
    @GetMapping("/search/page")
    public ResponseEntity<Page<ClientResponse>> getClientsPageByFilter(Authentication authentication,
                                                                    @RequestParam(required = false) String filter,
                                                                    Pageable pageable){
        return ResponseEntity.ok(this.clientService.getClientsPageByFilter(filter, authentication.getName(), pageable));
    }

    /**
     * GET /clients/search
     * Busca clientes según dni, nombre o apellido.
     */
    @GetMapping("/search/{filter}")
    public ResponseEntity<List<ClientResponse>> searchClientsByFilter(Authentication authentication,
                                                                    @PathVariable String filter){
        return ResponseEntity.ok(this.clientService.searchClientsByFilter(filter, authentication.getName()));
    }

    /**
     * GET /exists/phone
     * Devuelve true si el número de telefono ya existe y false si no existe
     */
    @GetMapping("/exists/phone/{phone}")
    public ResponseEntity<Boolean> existsPhone(@PathVariable String phone, Authentication authentication){
        return ResponseEntity.ok(this.clientService.existsPhone(phone, authentication.getName()));
    }

    /**
     * GET /exists/dni
     * Devuelve true si el dni ya existe y false si no existe
     */
    @GetMapping("/exists/dni/{dni}")
    public ResponseEntity<Boolean> existsDni(@PathVariable String dni, Authentication authentication){
        return ResponseEntity.ok(this.clientService.existsDni(dni, authentication.getName()));
    }
}
