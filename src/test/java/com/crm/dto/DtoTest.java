package com.crm.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DtoTest {

    @Test
    void sellerDto_ShouldWorkCorrectly() {
        LocalDateTime now = LocalDateTime.now();
        SellerDto dto = new SellerDto(1L, "Иван", "test@mail.com", now);

        assertEquals(1L, dto.getId());
        assertEquals("Иван", dto.getName());
        assertEquals("test@mail.com", dto.getContactInfo());
        assertEquals(now, dto.getRegistrationDate());

        dto.setName("Петр");
        dto.setContactInfo("new@mail.com");
        assertEquals("Петр", dto.getName());
        assertEquals("new@mail.com", dto.getContactInfo());
    }

    @Test
    void transactionDto_ShouldWorkCorrectly() {
        LocalDateTime now = LocalDateTime.now();
        TransactionDto dto = new TransactionDto(1L, 2L, "Иван", new BigDecimal("1000"), "CARD", now);

        assertEquals(1L, dto.getId());
        assertEquals(2L, dto.getSellerId());
        assertEquals("Иван", dto.getSellerName());
        assertEquals(new BigDecimal("1000"), dto.getAmount());
        assertEquals("CARD", dto.getPaymentType());
        assertEquals(now, dto.getTransactionDate());

        dto.setAmount(new BigDecimal("2000"));
        dto.setPaymentType("CASH");
        assertEquals(new BigDecimal("2000"), dto.getAmount());
        assertEquals("CASH", dto.getPaymentType());
    }

    @Test
    void analyticsDto_ShouldWorkCorrectly() {
        AnalyticsDto.TopSeller topSeller = new AnalyticsDto.TopSeller();
        topSeller.setSellerId(1L);
        topSeller.setSellerName("Иван");
        topSeller.setTotalAmount(new BigDecimal("5000"));
        topSeller.setPeriod("MONTH");

        assertEquals(1L, topSeller.getSellerId());
        assertEquals("Иван", topSeller.getSellerName());
        assertEquals(new BigDecimal("5000"), topSeller.getTotalAmount());
        assertEquals("MONTH", topSeller.getPeriod());

        AnalyticsDto.SellerWithTotal sellerWithTotal = new AnalyticsDto.SellerWithTotal();
        sellerWithTotal.setSellerId(1L);
        sellerWithTotal.setSellerName("Иван");
        sellerWithTotal.setTotalAmount(new BigDecimal("3000"));

        assertEquals(1L, sellerWithTotal.getSellerId());
        assertEquals("Иван", sellerWithTotal.getSellerName());
        assertEquals(new BigDecimal("3000"), sellerWithTotal.getTotalAmount());

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);
        AnalyticsDto.BestPeriod bestPeriod = new AnalyticsDto.BestPeriod(start, end, 10L, new BigDecimal("5000"));

        assertEquals(start, bestPeriod.getStartDate());
        assertEquals(end, bestPeriod.getEndDate());
        assertEquals(10L, bestPeriod.getTransactionCount());
        assertEquals(new BigDecimal("5000"), bestPeriod.getTotalAmount());
    }
}