package com.vittor.pennyapi.integration;

import com.vittor.pennyapi.dto.*;
import com.vittor.pennyapi.enums.TransactionType;
import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("User Journey Integration Tests")
class UserJourneyIntegrationTest extends BaseIntegrationTest {

    private static String authToken;
    private static String categoryId;
    private static String transactionId;

    @Test
    @Order(1)
    @DisplayName("Step 1: Should successfully register a new user")
    void shouldRegisterUser() throws Exception {
        RegisterDTO registerDTO = new RegisterDTO(
                "John Doe",
                "john.doe@example.com",
                "password123"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(registerDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("User registered successfully")));
    }

    @Test
    @Order(2)
    @DisplayName("Step 2: Should successfully login and receive JWT token")
    void shouldLoginAndReceiveToken() throws Exception {
        LoginDTO loginDTO = new LoginDTO(
                "john.doe@example.com",
                "password123"
        );

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.type").value("Bearer"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        LoginResponseDTO loginResponse = fromJson(responseBody, LoginResponseDTO.class);
        authToken = loginResponse.token();

        Assertions.assertNotNull(authToken, "JWT token should not be null");
        Assertions.assertFalse(authToken.isEmpty(), "JWT token should not be empty");
    }

    @Test
    @Order(3)
    @DisplayName("Step 3: Should successfully create a category with valid token")
    void shouldCreateCategory() throws Exception {
        CreateCategoryDTO categoryDTO = new CreateCategoryDTO(
                "Groceries",
                "shopping-cart",
                "#FF5733"
        );

        MvcResult result = mockMvc.perform(post("/api/categories")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(categoryDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Groceries"))
                .andExpect(jsonPath("$.icon").value("shopping-cart"))
                .andExpect(jsonPath("$.color").value("#FF5733"))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        CategoryResponseDTO categoryResponse = fromJson(responseBody, CategoryResponseDTO.class);
        categoryId = categoryResponse.id().toString();

        Assertions.assertNotNull(categoryId, "Category ID should not be null");
    }

    @Test
    @Order(4)
    @DisplayName("Step 4: Should successfully create a transaction with valid token and category")
    void shouldCreateTransaction() throws Exception {
        CreateTransactionDTO transactionDTO = new CreateTransactionDTO(
                "Weekly grocery shopping",
                new BigDecimal("150.75"),
                TransactionType.EXPENSE,
                LocalDate.now(),
                java.util.UUID.fromString(categoryId)
        );

        MvcResult result = mockMvc.perform(post("/api/transactions")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(transactionDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.description").value("Weekly grocery shopping"))
                .andExpect(jsonPath("$.amount").value(150.75))
                .andExpect(jsonPath("$.type").value("EXPENSE"))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        TransactionResponseDTO transactionResponse = fromJson(responseBody, TransactionResponseDTO.class);
        transactionId = transactionResponse.id().toString();

        Assertions.assertNotNull(transactionId, "Transaction ID should not be null");
    }

    @Test
    @Order(5)
    @DisplayName("Bonus: Verify created transaction can be retrieved")
    void shouldRetrieveCreatedTransaction() throws Exception {
        mockMvc.perform(get("/api/transactions/" + transactionId)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(transactionId))
                .andExpect(jsonPath("$.description").value("Weekly grocery shopping"))
                .andExpect(jsonPath("$.amount").value(150.75));
    }
}
