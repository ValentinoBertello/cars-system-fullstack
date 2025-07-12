package com.valentinobertello.carsys.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.valentinobertello.carsys.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.valentinobertello.carsys.config.UserTestHelper.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class) // Prueba enfocada solo en el controlador UserController
@AutoConfigureMockMvc(addFilters = false) // Desactiva los filtros de seguridad para testing
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc; // Cliente MVC para simular peticiones HTTP

    @MockitoBean
    private UserService userService;

    /**
     * Prueba para el endpoint GET /users
     */
    @Test
    void getAllUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(ALL_USER_RESPONSES);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Valentino"))
                .andExpect(jsonPath("$[1].name").value("María"));
    }

    /**
     * Prueba para el endpoint GET /users/email/{email}
     * Verifica la búsqueda de usuario por email
     */
    @Test
    void getUserByEmail() throws Exception {
        when(userService.getUserByEmail(any())).thenReturn(USER_RESPONSE_1);

        mockMvc.perform(get("/users/email/valenbertello@gmail.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.name").value("Valentino"));
    }

    /**
     * Prueba para el endpoint POST /users/register
     * Verifica el registro de un nuevo usuario
     */
    @Test
    void registerUser() throws Exception {
        when(userService.saveUser(USER_REQUEST_1)).thenReturn(USER_RESPONSE_1);

        mockMvc.perform(post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(USER_REQUEST_1)))
                .andExpect(status().isOk());
    }
}