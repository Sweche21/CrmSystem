package com.crm.controller;

import com.crm.dto.TransactionDto;
import com.crm.dto.AnalyticsDto;
import com.crm.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllTransactions_ShouldReturnTransactions() throws Exception {

        TransactionDto transaction = new TransactionDto(1L, 1L, "Иван Петров",
                new BigDecimal("1000.50"), "CARD", LocalDateTime.now());
        when(transactionService.getAllTransactions()).thenReturn(Arrays.asList(transaction));


        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].amount").value(1000.50))
                .andExpect(jsonPath("$[0].paymentType").value("CARD"));
    }

    @Test
    void getTransactionById_WhenExists_ShouldReturnTransaction() throws Exception {

        TransactionDto transaction = new TransactionDto(1L, 1L, "Иван Петров",
                new BigDecimal("1000.50"), "CARD", LocalDateTime.now());
        when(transactionService.getTransactionById(1L)).thenReturn(Optional.of(transaction));


        mockMvc.perform(get("/api/transactions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.sellerName").value("Иван Петров"));
    }

    @Test
    void getTransactionById_WhenNotExists_ShouldReturnNotFound() throws Exception {

        when(transactionService.getTransactionById(999L)).thenReturn(Optional.empty());


        mockMvc.perform(get("/api/transactions/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createTransaction_WithValidData_ShouldCreateTransaction() throws Exception {

        TransactionDto request = new TransactionDto(1L, new BigDecimal("1000.50"), "CARD");
        TransactionDto response = new TransactionDto(1L, 1L, "Иван Петров",
                new BigDecimal("1000.50"), "CARD", LocalDateTime.now());

        when(transactionService.createTransaction(any(TransactionDto.class))).thenReturn(response);


        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(1000.50))
                .andExpect(jsonPath("$.paymentType").value("CARD"));
    }

    @Test
    void getTransactionsBySeller_ShouldReturnTransactions() throws Exception {

        TransactionDto transaction = new TransactionDto(1L, 1L, "Иван Петров",
                new BigDecimal("1000.50"), "CARD", LocalDateTime.now());
        when(transactionService.getTransactionsBySellerId(1L)).thenReturn(Arrays.asList(transaction));


        mockMvc.perform(get("/api/transactions/seller/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sellerId").value(1))
                .andExpect(jsonPath("$[0].sellerName").value("Иван Петров"));
    }

    @Test
    void getTopSeller_WhenExists_ShouldReturnTopSeller() throws Exception {

        AnalyticsDto.TopSeller topSeller = new AnalyticsDto.TopSeller(1L, "Иван Петров",
                new BigDecimal("50000"), "MONTH");
        when(transactionService.getTopSellerByPeriod("MONTH")).thenReturn(Optional.of(topSeller));


        mockMvc.perform(get("/api/transactions/analytics/top-seller")
                        .param("period", "MONTH"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sellerName").value("Иван Петров"))
                .andExpect(jsonPath("$.period").value("MONTH"));
    }

    @Test
    void getTopSeller_WhenNotExists_ShouldReturnNotFound() throws Exception {

        when(transactionService.getTopSellerByPeriod("MONTH")).thenReturn(Optional.empty());


        mockMvc.perform(get("/api/transactions/analytics/top-seller")
                        .param("period", "MONTH"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getBestPeriodForSeller_WhenExists_ShouldReturnBestPeriod() throws Exception {

        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        AnalyticsDto.BestPeriod bestPeriod = new AnalyticsDto.BestPeriod(
                startDate, endDate, 15L, new BigDecimal("25000"));

        when(transactionService.getBestPeriodForSeller(1L)).thenReturn(Optional.of(bestPeriod));


        mockMvc.perform(get("/api/transactions/analytics/best-period/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionCount").value(15))
                .andExpect(jsonPath("$.totalAmount").value(25000));
    }

    @Test
    void getBestPeriodForSeller_WhenNotExists_ShouldReturnNotFound() throws Exception {

        when(transactionService.getBestPeriodForSeller(999L)).thenReturn(Optional.empty());


        mockMvc.perform(get("/api/transactions/analytics/best-period/999"))
                .andExpect(status().isNotFound());
    }
}