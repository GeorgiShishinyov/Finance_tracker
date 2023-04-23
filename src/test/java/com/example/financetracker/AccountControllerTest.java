package com.example.financetracker;

import com.example.financetracker.controller.AccountController;
import com.example.financetracker.model.DTOs.CurrencyDTOs.CurrencyDTO;
import com.example.financetracker.model.DTOs.AccountDTOs.AccountWithOwnerDTO;
import com.example.financetracker.model.DTOs.AccountDTOs.AccountWithoutOwnerDTO;
import com.example.financetracker.model.DTOs.AccountDTOs.CreateAccountDTO;
import com.example.financetracker.model.DTOs.AccountDTOs.EditAccountDTO;
import com.example.financetracker.model.entities.Account;
import com.example.financetracker.model.entities.Currency;
import com.example.financetracker.model.entities.User;
import com.example.financetracker.model.repositories.AccountRepository;
import com.example.financetracker.model.repositories.UserRepository;
import com.example.financetracker.service.AccountService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ModelMapper mapper;

    @MockBean
    private AccountService accountService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @SneakyThrows
    @Test
    public void testCreateAccount(){
        // Prepare test data
        CreateAccountDTO createAccountDTO = new CreateAccountDTO();
        createAccountDTO.setName("My Account");
        createAccountDTO.setBalance(BigDecimal.ONE);
        createAccountDTO.setCurrencyId(1);

        AccountWithOwnerDTO accountWithOwnerDTO = new AccountWithOwnerDTO();
        accountWithOwnerDTO.setId(1);
        accountWithOwnerDTO.setName("My Account");
        accountWithOwnerDTO.setBalance(BigDecimal.ONE);

        when(accountService.create(any(CreateAccountDTO.class), any(Integer.class)))
                .thenReturn(accountWithOwnerDTO);

        // Perform the request
        MockHttpSession mockSession = new MockHttpSession();
        mockSession.setAttribute("LOGGED", true);
        mockSession.setAttribute("LOGGED_ID", 1);

        // Perform the request
        mockMvc.perform(MockMvcRequestBuilders.post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"name\": \"My Account\", \"balance\": 1, \"currencyId\": 1 }")
                        .session(mockSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("My Account"))
                .andExpect(jsonPath("$.balance").value(1));
    }

    @SneakyThrows
    @Test
    public void testDeleteAccountById() {
        // Prepare test data
        Account account = new Account();
        account.setId(1);
        account.setName("My Account");
        account.setBalance(BigDecimal.ZERO);
        Currency currency = new Currency();
        currency.setId(1);
        account.setCurrency(currency);
        when(accountRepository.findById(1)).thenReturn(Optional.of(account));
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(new User()));
        when(accountService.deleteAccountById(anyInt(), anyInt())).thenReturn(new AccountWithoutOwnerDTO(1, "My Account", BigDecimal.ZERO, new CurrencyDTO(1, "USD")));

        // create a new mock session
        MockHttpSession mockSession = new MockHttpSession();
        mockSession.setAttribute("LOGGED", true);
        mockSession.setAttribute("LOGGED_ID", 1);

        // Perform the request
        mockMvc.perform(delete("/accounts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(mockSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("My Account"))
                .andExpect(jsonPath("$.balance").value(0));

    }

    @SneakyThrows
    @Test
    public void testEditAccount(){
        // Prepare test data
        EditAccountDTO editAccountDTO = new EditAccountDTO();
        editAccountDTO.setName("My Edited Account");
        editAccountDTO.setBalance(new BigDecimal("1000"));
        editAccountDTO.setCurrencyId(2);

        AccountWithOwnerDTO accountWithOwnerDTO = new AccountWithOwnerDTO();
        accountWithOwnerDTO.setId(1);
        accountWithOwnerDTO.setName("My Edited Account");
        accountWithOwnerDTO.setBalance(new BigDecimal("1000"));

        when(accountService.edit(any(Integer.class), any(EditAccountDTO.class), any(Integer.class)))
                .thenReturn(accountWithOwnerDTO);

        MockHttpSession mockSession = new MockHttpSession();
        mockSession.setAttribute("LOGGED", true);
        mockSession.setAttribute("LOGGED_ID", 1);

        // Perform the request
        mockMvc.perform(MockMvcRequestBuilders.put("/accounts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"name\": \"My Edited Account\", \"balance\": 1000, \"currencyId\": 2 }")
                        .session(mockSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("My Edited Account"))
                .andExpect(jsonPath("$.balance").value(1000));
    }

    @SneakyThrows
    @Test
    void testGetAccountById(){
        int id = 1;
        int userId = 1;

        // Create a fake account with owner
        User owner = new User();
        owner.setId(userId);
        owner.setEmail("test@example.com");
        owner.setFirstName("John");
        owner.setLastName("Doe");

        owner.setDateOfBirth(LocalDateTime.of(2000, 1, 1, 0, 0));
        owner.setLastLogin(LocalDateTime.of(2023, 4, 19, 12, 0));

        MockHttpSession mockSession = new MockHttpSession();
        mockSession.setAttribute("LOGGED", true);
        mockSession.setAttribute("LOGGED_ID", 1);

        Currency currency = new Currency();
        currency.setId(1);
        currency.setKind("USD");

        Account account = new Account();
        account.setId(id);
        account.setName("Test Account");
        account.setBalance(new BigDecimal("100.00"));
        account.setCurrency(currency);
        account.setOwner(owner);

        // Set up mock service to return the fake account
        when(accountService.getById(id, userId)).thenReturn(mapper.map(account, AccountWithOwnerDTO.class));

        // Perform the request and verify the response
        mockMvc.perform(get("/accounts/" + id).session(mockSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value(account.getName()))
                .andExpect(jsonPath("$.balance").value(account.getBalance().doubleValue()))
                .andExpect(jsonPath("$.currency.id").value(account.getCurrency().getId()))
                .andExpect(jsonPath("$.currency.kind").value(account.getCurrency().getKind()))
                .andExpect(jsonPath("$.owner.id").value(account.getOwner().getId()))
                .andExpect(jsonPath("$.owner.email").value(account.getOwner().getEmail()))
                .andExpect(jsonPath("$.owner.firstName").value(account.getOwner().getFirstName()))
                .andExpect(jsonPath("$.owner.lastName").value(account.getOwner().getLastName()))
                .andExpect(jsonPath("$.owner.dateOfBirth").value(account.getOwner().getDateOfBirth().
                        format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
                .andExpect(jsonPath("$.owner.lastLogin").value(account.getOwner().getLastLogin().
                        format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))));

        // Verify that the service method was called with the correct arguments
        verify(accountService).getById(id, userId);
    }
}

