package com.valentinobertello.carsys.service;

import com.valentinobertello.carsys.dtos.auth.UserRequest;
import com.valentinobertello.carsys.dtos.auth.UserResponse;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Toda la lógica de negocio referida a los usuarios que
 * de la aplicación.
 * **/
@Service
public interface UserService {
    List<UserResponse> getAllUsers();

    UserResponse saveUser(UserRequest userRequest);

    UserResponse getUserById(Long id);

    UserResponse getUserByEmail(String email);

    Boolean checkRepeatedEmail(String email);
}
