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

@DisplayName("Category Business Rules Integration Tests")
class CategoryIntegrationTest extends BaseIntegrationTest {

    private String authToken;

    @BeforeEach
    void setup() throws Exception {
        // Registrar e fazer login para cada teste
        RegisterDTO registerDTO = new RegisterDTO(
                "Test User",
                "category.test@example.com",
                "password123"
        );

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(registerDTO)))
                .andExpect(status().isCreated());

        LoginDTO loginDTO = new LoginDTO(
                "category.test@example.com",
                "password123"
        );

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(loginDTO)))
                .andExpect(status().isOk())
                .andReturn();

        LoginResponseDTO loginResponse = fromJson(
                result.getResponse().getContentAsString(),
                LoginResponseDTO.class
        );
        authToken = loginResponse.token();
    }

    @Test
    @DisplayName("Should prevent deleting category with associated transactions")
    void shouldPreventDeletingCategoryWithTransactions() throws Exception {
        // Criar categoria
        CreateCategoryDTO categoryDTO = new CreateCategoryDTO(
                "Salary",
                "money",
                "#00FF00"
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

        // Criar transação vinculada à categoria
        CreateTransactionDTO transactionDTO = new CreateTransactionDTO(
                "Monthly salary",
                new BigDecimal("5000.00"),
                TransactionType.INCOME,
                LocalDate.now(),
                category.id()
        );

        mockMvc.perform(post("/api/transactions")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(transactionDTO)))
                .andExpect(status().isCreated());

        // Tentar deletar categoria com transações
        mockMvc.perform(delete("/api/categories/" + category.id())
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        "Cannot delete category with existing transactions"
                ));
    }

    @Test
    @DisplayName("Should prevent accessing another user's category")
    void shouldPreventCrossUserCategoryAccess() throws Exception {
        // Criar categoria com primeiro usuário
        CreateCategoryDTO categoryDTO = new CreateCategoryDTO(
                "Private Category",
                "lock",
                "#FF0000"
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

        // Registrar e fazer login com segundo usuário
        RegisterDTO secondUserDTO = new RegisterDTO(
                "Second User",
                "second.user@example.com",
                "password123"
        );

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(secondUserDTO)))
                .andExpect(status().isCreated());

        LoginDTO secondLoginDTO = new LoginDTO(
                "second.user@example.com",
                "password123"
        );

        MvcResult secondLoginResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(secondLoginDTO)))
                .andExpect(status().isOk())
                .andReturn();

        LoginResponseDTO secondUserLogin = fromJson(
                secondLoginResult.getResponse().getContentAsString(),
                LoginResponseDTO.class
        );

        // Segundo usuário tenta acessar categoria do primeiro
        mockMvc.perform(get("/api/categories/" + category.id())
                .header("Authorization", "Bearer " + secondUserLogin.token()))
                .andExpect(status().isNotFound());
    }
}
