package cloudflight.integra.backend.controller;


import cloudflight.integra.backend.exception.UserNotFoundException;
import cloudflight.integra.backend.model.User;
import cloudflight.integra.backend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    // GET /users
    @Test
    void testFindAll() throws Exception {
        User user1 = new User(UUID.randomUUID(), "Ana", "ana@email.com");
        User user2 = new User(UUID.randomUUID(), "Ion", "ion@email.com");

        when(userService.findAll()).thenReturn(List.of(user1, user2));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].username").value("Ana"))
                .andExpect(jsonPath("$[1].username").value("Ion"));

        verify(userService, times(1)).findAll();
    }

    // GET /users/{id}
    @Test
    void testFindUser() throws Exception {
        UUID id = UUID.randomUUID();
        User user = new User(id, "Ana", "ana@email.com");
        when(userService.findUser(id)).thenReturn(user);

        mockMvc.perform(get("/users/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Ana"));

        verify(userService, times(1)).findUser(id);
    }

    @Test
    void testNotFindUser() throws Exception {
        UUID id = UUID.randomUUID();

        when(userService.findUser(id))
                .thenThrow(new UserNotFoundException("User with id " + id + " not found"));

        mockMvc.perform(get("/users/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User with id " + id + " not found"));

        verify(userService, times(1)).findUser(id);
    }

    // POST /users
    @Test
    void testAddUserValid() throws Exception {
        User user = new User(null, "Ana", "ana@email.com");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated());

        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    void testAddUserInvalid() throws Exception {
        User user = new User(null, "A", "not-an-email");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

    // PUT /users/{id}
    @Test
    void testUpdateUser() throws Exception {
        UUID id = UUID.randomUUID();
        User user = new User(null, "AnaUpdated", "anaupdated@email.com");

        mockMvc.perform(put("/users/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());

        verify(userService, times(1)).updateUser(any(UUID.class), any(User.class));
    }

    @Test
    void testUpdateUserNotFindUser() throws Exception {
        UUID id = UUID.randomUUID();
        User user = new User(id, "AnaUpdated", "anaupdated@email.com");

        doThrow(new UserNotFoundException("User with id " + id + " not found"))
                .when(userService).updateUser(any(UUID.class), any(User.class));

        mockMvc.perform(put("/users/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User with id " + id + " not found"));

        verify(userService, times(1)).updateUser(any(UUID.class), any(User.class));
    }

    // DELETE /users/{id}
    @Test
    void testDeleteUser() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/users/{id}", id))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(id);
    }

    @Test
    void testDeleteUserNotFound() throws Exception {
        UUID id = UUID.randomUUID();

        doThrow(new UserNotFoundException("User with id " + id + " not found"))
                .when(userService).deleteUser(any(UUID.class));

        mockMvc.perform(delete("/users/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User with id " + id + " not found"));

        verify(userService, times(1)).deleteUser(id);
    }
}
