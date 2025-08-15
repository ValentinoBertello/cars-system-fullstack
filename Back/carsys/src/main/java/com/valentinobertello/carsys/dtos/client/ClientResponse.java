package com.valentinobertello.carsys.dtos.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO para enviar datos detallados de un cliente en respuestas API.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientResponse {
    private Long id;
    private String name;
    private String lastName;
    private String phone;
    private String dni;
    private LocalDate registrationDate;
}
