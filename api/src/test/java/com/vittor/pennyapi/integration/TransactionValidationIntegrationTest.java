package com.vittor.pennyapi.integration;

import com.vittor.pennyapi.dto.*;
import com.vittor.pennyapi.enums.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Transaction Validation Integration Tests")
class TransactionValidationIntegrationTest extends BaseIntegrationTest {

    private String authToken;
    private String categoryId;

    @BeforeEach
    void setup() throws Exception {
        // Registrar e fazer login
        RegisterDTO registerDTO = new RegisterDTO(
                "Transaction Test User",
                "transaction.test@example.com",
                "password123"
        );

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(registerDTO)))
                .andExpect(status().isCreated());

        LoginDTO loginDTO = new LoginDTO(
                "transaction.test@example.com",
                "password123"
        );

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(loginDTO)))
                .andExpect(status().isOk())
                .andReturn();

        LoginResponseDTO loginResponse = fromJson(
                loginResult.getResponse().getContentAsString(),
                LoginResponseDTO.class
        );
        authToken = loginResponse.token();

        // Criar categoria para transações
        CreateCategoryDTO categoryDTO = new CreateCategoryDTO(
                "Test Category",
                "test",
                "#FFFFFF"
        );

        MvcResult categoryResult = mockMvc.perform(post("/api/categories")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(categoryDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        CategoryResponseDTO category = fromJson(
                categoryResult.getResponse().getContentAsString(),
                CategoryResponseDTO.class
        );
        categoryId = category.id().toString();
    }

    @Test
    @DisplayName("Should reject transaction with amount less than 0.01")
    void shouldRejectInvalidAmount() throws Exception {
        CreateTransactionDTO transactionDTO = new CreateTransactionDTO(
                "Invalid amount transaction",
                new BigDecimal("0.00"),
                TransactionType.EXPENSE,
                LocalDate.now(),
                UUID.fromString(categoryId)
        );

        mockMvc.perform(post("/api/transactions")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(transactionDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should reject transaction with future date")
    void shouldRejectFutureDate() throws Exception {
        CreateTransactionDTO transactionDTO = new CreateTransactionDTO(
                "Future transaction",
                new BigDecimal("100.00"),
                TransactionType.INCOME,
                LocalDate.now().plusDays(1),
                UUID.fromString(categoryId)
        );

        mockMvc.perform(post("/api/transactions")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(transactionDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should successfully retrieve financial summary")
    void shouldRetrieveFinancialSummary() throws Exception {
        // Criar múltiplas transações
        CreateTransactionDTO income = new CreateTransactionDTO(
                "Income transaction",
                new BigDecimal("1000.00"),
                TransactionType.INCOME,
                LocalDate.now(),
                UUID.fromString(categoryId)
        );

        CreateTransactionDTO expense = new CreateTransactionDTO(
                "Expense transaction",
                new BigDecimal("300.00"),
                TransactionType.EXPENSE,
                LocalDate.now(),
                UUID.fromString(categoryId)
        );

        mockMvc.perform(post("/api/transactions")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(income)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/transactions")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(expense)))
                .andExpect(status().isCreated());

        // Buscar resumo financeiro
        mockMvc.perform(get("/api/transactions/summary")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalIncome").value(1000.00))
                .andExpect(jsonPath("$.totalExpense").value(300.00))
                .andExpect(jsonPath("$.balance").value(700.00));
    }
}
