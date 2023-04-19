package com.example.financetracker;

import com.example.financetracker.controller.UserController;
import com.example.financetracker.model.DTOs.LoginDTO;
import com.example.financetracker.model.DTOs.RegisterDTO;
import com.example.financetracker.model.DTOs.UserFullInfoDTO;
import com.example.financetracker.model.entities.User;
import com.example.financetracker.model.exceptions.BadRequestException;
import com.example.financetracker.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController controller;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        // configure any required mock behavior
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void testRegisterWithValidInput() {
        RegisterDTO registerDTO = new RegisterDTO();
        // set properties of dto object
        registerDTO.setEmail("test@example.com");
        registerDTO.setPassword("Password123");
        registerDTO.setConfirmPassword("Password123");
        registerDTO.setFirstName("John");
        registerDTO.setLastName("Doe");
        registerDTO.setDateOfBirth(LocalDateTime.of(2000, 1, 1, 0, 0));
        User user = new User();
        // set properties of u object

        user.setId(1);
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setDateOfBirth(LocalDateTime.of(2000, 1, 1, 0, 0));


        UserFullInfoDTO expected = new UserFullInfoDTO();
        // set properties of expected object
        expected.setId(1);
        expected.setEmail("test@example.com");
        expected.setFirstName("John");
        expected.setLastName("Doe");
        expected.setDateOfBirth(LocalDateTime.of(2000, 1, 1, 0, 0));
        expected.setLastLogin(LocalDateTime.of(2022, 4, 18, 12, 0));

        when(userService.register(registerDTO)).thenReturn(expected);

        UserFullInfoDTO result = controller.register(registerDTO);

        assertNotNull(result);
        assertEquals(expected.getId(), result.getId());
    }

    @Test
    public void testRegisterWithWeakPassword() {
        RegisterDTO dto = new RegisterDTO();
        // set properties of dto object to have a weak password
        dto.setEmail("test@example.com");
        dto.setPassword("Pas");
        dto.setConfirmPassword("Pas");
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setDateOfBirth(LocalDateTime.of(2000, 1, 1, 0, 0));
        BadRequestException expectedException = new BadRequestException("Weak password. The password must be " +
                "at least 8 characters long and contain an uppercase letter, " +
                "a lowercase letter, a number, and a special character.");
        when(userService.register(dto)).thenThrow(expectedException);

        BadRequestException thrownException = assertThrows(BadRequestException.class, () -> {
            controller.register(dto);
        });

        assertEquals(expectedException.getMessage(), thrownException.getMessage());
    }

    @Test
    public void testLoginWithValidCredentials() {
        LoginDTO dto = new LoginDTO();
        dto.setEmail("test@example.com");
        dto.setPassword("password123");
        // set properties of dto object
        User u = new User();
        // set properties of u object
        u.setId(1);
        u.setEmail("test@example.com");
        u.setPassword("password123");
        u.setFirstName("John");
        u.setLastName("Doe");
        u.setDateOfBirth(LocalDateTime.of(2000, 1, 1, 0, 0));

        Optional<User> optionalUser = Optional.of(u);

        UserFullInfoDTO expected = new UserFullInfoDTO();
        // set properties of expected object
        expected.setId(1);
        expected.setEmail("test@example.com");
        expected.setFirstName("John");
        expected.setLastName("Doe");
        expected.setDateOfBirth(LocalDateTime.of(2000, 1, 1, 0, 0));
        expected.setLastLogin(LocalDateTime.of(2022, 4, 18, 12, 0));

        when(userService.login(dto, "127.0.0.1")).thenReturn(expected);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        UserFullInfoDTO result = controller.login(dto, session, request);

        assertNotNull(result);
        assertEquals(expected.getId(), result.getId());
    }

    @SneakyThrows
    @Test
    public void registerWithInvalidEmailShouldReturnBadRequest(){
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setEmail("invalidemail");
        registerDTO.setPassword("StrongP@ssword1");
        registerDTO.setConfirmPassword("StrongP@ssword1");

        BadRequestException expectedException = new BadRequestException("invalid email");
        when(userService.register(registerDTO)).thenThrow(expectedException);

        BadRequestException thrownException = assertThrows(BadRequestException.class, () -> {
            controller.register(registerDTO);
        });

        assertEquals(expectedException.getMessage(), thrownException.getMessage());
    }
}