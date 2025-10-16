package com.crm.service;

import com.crm.repository.SellerRepository;
import com.crm.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private SellerRepository sellerRepository;

    @InjectMocks
    private AnalyticsService analyticsService;

    @Test
    void getTotalSales_WhenTransactionsExist_ShouldReturnTotal() {

        BigDecimal expectedTotal = new BigDecimal("15000.50");
        when(transactionRepository.getTotalSalesAmount()).thenReturn(expectedTotal);

        BigDecimal result = analyticsService.getTotalSales();

        assertEquals(expectedTotal, result);
        verify(transactionRepository, times(1)).getTotalSalesAmount();
    }

    @Test
    void getTotalSales_WhenNoTransactions_ShouldReturnZero() {

        when(transactionRepository.getTotalSalesAmount()).thenReturn(null);

        BigDecimal result = analyticsService.getTotalSales();

        assertEquals(BigDecimal.ZERO, result);
        verify(transactionRepository, times(1)).getTotalSalesAmount();
    }

    @Test
    void getAverageTransactionAmount_WhenTransactionsExist_ShouldReturnAverage() {

        BigDecimal totalSales = new BigDecimal("10000.00");
        Long transactionCount = 5L;

        when(transactionRepository.getTotalSalesAmount()).thenReturn(totalSales);
        when(transactionRepository.count()).thenReturn(transactionCount);

        BigDecimal result = analyticsService.getAverageTransactionAmount();

        assertEquals(new BigDecimal("2000.00"), result);
        verify(transactionRepository, times(1)).getTotalSalesAmount();
        verify(transactionRepository, times(1)).count();
    }

    @Test
    void getAverageTransactionAmount_WhenNoTransactions_ShouldReturnZero() {

        when(transactionRepository.count()).thenReturn(0L);

        BigDecimal result = analyticsService.getAverageTransactionAmount();

        assertEquals(BigDecimal.ZERO, result);
        verify(transactionRepository, times(1)).count();
    }

    @Test
    void getTransactionCount_ShouldReturnCount() {

        when(transactionRepository.count()).thenReturn(10L);

        long result = analyticsService.getTransactionCount();

        assertEquals(10L, result);
        verify(transactionRepository, times(1)).count();
    }

    @Test
    void getSellerCount_ShouldReturnCount() {

        when(sellerRepository.count()).thenReturn(5L);

        long result = analyticsService.getSellerCount();

        assertEquals(5L, result);
        verify(sellerRepository, times(1)).count();
    }

    @Test
    void getTotalSalesByPaymentType_ShouldReturnAmount() {

        String paymentType = "CARD";
        BigDecimal expectedAmount = new BigDecimal("7500.25");
        when(transactionRepository.getTotalSalesByPaymentType(paymentType)).thenReturn(expectedAmount);

        BigDecimal result = analyticsService.getTotalSalesByPaymentType(paymentType);

        assertEquals(expectedAmount, result);
        verify(transactionRepository, times(1)).getTotalSalesByPaymentType(paymentType);
    }

    @Test
    void getTransactionCountBySeller_ShouldReturnCount() {

        Long sellerId = 1L;
        Long expectedCount = 3L;
        when(transactionRepository.countBySellerId(sellerId)).thenReturn(expectedCount);

        Long result = analyticsService.getTransactionCountBySeller(sellerId);

        assertEquals(expectedCount, result);
        verify(transactionRepository, times(1)).countBySellerId(sellerId);
    }
}