package org.backendada.proyectofinal.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.backendada.proyectofinal.user.entity.User;
import org.backendada.proyectofinal.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @BeforeEach
    public void setup() {
        user = new User();
        user.setName("Name");
        user.setPassword("Password");
        user.setEmail("email@email.com");

        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
    }

    @Test
    public void testGetAll() throws Exception {
        mockMvc.perform(get("/api/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Name"))
                .andExpect(jsonPath("$[0].password").value("Password"))
                .andExpect(jsonPath("$[0].email").value("email@email.com"));

        verify(userRepository).findAll();
    }

    @Test
    public void testUpdate() throws Exception {
        user.setName("New Name");
        mockMvc.perform(put("/api/user/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Name"))
                .andExpect(jsonPath("$.password").value("Password"))
                .andExpect(jsonPath("$.email").value("email@email.com"));

        verify(userRepository).findById(1L);
    }

    @Test
    public void testPost() throws Exception {
        User newUser = new User();
        newUser.setName("New Name");
        newUser.setPassword("New Password");
        newUser.setEmail("New email@email.com");

        when(userRepository.save(newUser)).thenReturn(newUser);

        mockMvc.perform(post("/api/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isOk());

        verify(userRepository).save(userCaptor.capture());
        assert "New Name".equals(userCaptor.getValue().getName());
        assert "New Password".equals(userCaptor.getValue().getPassword());
        assert "New email@email.com".equals(userCaptor.getValue().getEmail());
    }

    @Test
    public void testDelete() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(delete("/api/user/1"))
                .andExpect(status().isOk());

        verify(userRepository).findById(1L);
        verify(userRepository).delete(userCaptor.capture());
        assert "Name".equals(userCaptor.getValue().getName());
        assert "Password".equals(userCaptor.getValue().getPassword());
        assert "email@email.com".equals(userCaptor.getValue().getEmail());
    }

    @Test
    public void testDeleteNotFound() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/user/1"))
                .andExpect(status().isNotFound());

        verify(userRepository).findById(1L);
        verify(userRepository, never()).delete(any(User.class));
    }

}
