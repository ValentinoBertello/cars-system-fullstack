package com.valentinobertello.carsys.dtos.auth;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Representa la información que se envía desde el servidor al cliente
 * después de crear, actualizar o consultar un usuario.
 * **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse implements Serializable {

    private Long id;

    private String name;

    private String lastname;

    private String email;

    private Boolean active;

    private List<String> roles;
}
