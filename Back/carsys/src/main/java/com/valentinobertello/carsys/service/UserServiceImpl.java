package com.valentinobertello.carsys.service;

import com.valentinobertello.carsys.dtos.UserRequest;
import com.valentinobertello.carsys.dtos.UserResponse;
import com.valentinobertello.carsys.entities.auth.RoleEntity;
import com.valentinobertello.carsys.entities.auth.UserEntity;
import com.valentinobertello.carsys.mapper.UserDataMapper;
import com.valentinobertello.carsys.repository.RoleRepository;
import com.valentinobertello.carsys.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Toda la lógica de negocio referida a los usuarios que
 * de la aplicación.
 * **/
@Service
public class UserServiceImpl implements UserService{

    private final UserDataMapper userDataMapper;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserDataMapper userDataMapper, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userDataMapper = userDataMapper;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }


    /**
     * Obtiene todos los usuarios registrados en la base de datos.
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        List<UserEntity> userEntities = this.userRepository.findAll();
        return this.userDataMapper.mapUserEntitiesToUserResponses(userEntities);
    }

    /**
     * Crea y persiste un nuevo usuario en la base de datos.
     */
    @Override
    @Transactional
    public UserResponse saveUser(UserRequest userRequest) {
        this.validateUserRequest(userRequest);

        // Comprobamos que todos los roles solicitados existen en la base de datos
        List<RoleEntity> roles = roleRepository.findByNameIn(userRequest.getRoleNames());
        if (roles.size() != userRequest.getRoleNames().size()) {
            throw new EntityNotFoundException("Algunos roles no existen");
        }

        // Mapeamos y encriptamos contraseña
        UserEntity userEntity = this.userDataMapper.mapUserRequestToUserEntity(
                userRequest,
                roles,
                this.passwordEncoder.encode(userRequest.getPassword()));

        userEntity = this.userRepository.save(userEntity);
        return this.userDataMapper.mapUserEntityToUserResponse(userEntity);
    }

    /**
     * Busca un usuario por su identificador.
     */
    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con id " + id));
        return userDataMapper.mapUserEntityToUserResponse(entity);    }

    /**
     * Busca un usuario por su email.
     */
    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        return this.userDataMapper.mapUserEntityToUserResponse(userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario inexistente")));
    }

    /**
     * Revisamos si el email recibido ya existe
     */
    @Override
    @Transactional(readOnly = true)
    public Boolean checkRepeatedEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public void validateUserRequest(UserRequest userRequest) {
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }
    }
}
