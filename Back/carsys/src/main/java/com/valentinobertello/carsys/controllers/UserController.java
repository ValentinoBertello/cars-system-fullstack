package com.valentinobertello.carsys.controllers;

import com.valentinobertello.carsys.dtos.auth.UserRequest;
import com.valentinobertello.carsys.dtos.auth.UserResponse;
import com.valentinobertello.carsys.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(originPatterns = "*") // Permite peticiones CORS desde cualquier origen
@RestController // Marca la clase como controlador REST, que devuelve directamente objetos JSON/XML
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * GET /users
     * Devuelve la lista completa de usuarios.
     */
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers(){
        return ResponseEntity.ok(this.userService.getAllUsers());
    }

    /**
     * GET /users/email/{email}
     * Busca un usuario por su correo electrónico.
     */
    @GetMapping("email/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email){
        return ResponseEntity.ok(this.userService.getUserByEmail(email));
    }

    /**
     * POST /users/register
     * Registra un nuevo usuario.
     */
    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@RequestBody @Valid UserRequest userRequest){
        // Evita que alguien se asigne el rol ROLE_ADMIN al registrarse
        userRequest.getRoleNames().removeIf(role -> role.equals("ROLE_ADMIN"));
        return ResponseEntity.ok(this.userService.saveUser(userRequest));
    }

    /**
     * GET /users/check-email/{email}
     * Revisamos si el email recibido ya existe
     */
    @GetMapping("/check-email/{email}")
    public ResponseEntity<Boolean> checkRepeatedEmail(@PathVariable String email){
        return ResponseEntity.ok(this.userService.checkRepeatedEmail(email));
    }
}