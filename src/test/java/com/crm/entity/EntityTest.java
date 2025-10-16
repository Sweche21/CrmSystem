package com.crm.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EntityTest {

    @Test
    void sellerEntity_ShouldWorkCorrectly() {
        Seller seller = new Seller("Иван", "test@mail.com");

        assertNull(seller.getId());
        assertEquals("Иван", seller.getName());
        assertEquals("test@mail.com", seller.getContactInfo());
        assertNotNull(seller.getRegistrationDate());
        assertNotNull(seller.getTransactions());

        seller.setId(1L);
        seller.setName("Петр");
        seller.setContactInfo("new@mail.com");
        LocalDateTime newDate = LocalDateTime.now();
        seller.setRegistrationDate(newDate);

        assertEquals(1L, seller.getId());
        assertEquals("Петр", seller.getName());
        assertEquals("new@mail.com", seller.getContactInfo());
        assertEquals(newDate, seller.getRegistrationDate());
    }

    @Test
    void transactionEntity_ShouldWorkCorrectly() {
        Seller seller = new Seller("Иван", "test@mail.com");
        Transaction transaction = new Transaction(seller, new BigDecimal("1000"), PaymentType.CARD);

        assertNull(transaction.getId());
        assertEquals(seller, transaction.getSeller());
        assertEquals(new BigDecimal("1000"), transaction.getAmount());
        assertEquals(PaymentType.CARD, transaction.getPaymentType());
        assertNotNull(transaction.getTransactionDate());

        transaction.setId(1L);
        Seller newSeller = new Seller("Петр", "new@mail.com");
        transaction.setSeller(newSeller);
        transaction.setAmount(new BigDecimal("2000"));
        transaction.setPaymentType(PaymentType.CASH);
        LocalDateTime newDate = LocalDateTime.now();
        transaction.setTransactionDate(newDate);

        assertEquals(1L, transaction.getId());
        assertEquals(newSeller, transaction.getSeller());
        assertEquals(new BigDecimal("2000"), transaction.getAmount());
        assertEquals(PaymentType.CASH, transaction.getPaymentType());
        assertEquals(newDate, transaction.getTransactionDate());
    }

    @Test
    void paymentType_ShouldHaveCorrectValues() {
        assertEquals("CASH", PaymentType.CASH.name());
        assertEquals("CARD", PaymentType.CARD.name());
        assertEquals("TRANSFER", PaymentType.TRANSFER.name());

        assertEquals(PaymentType.CASH, PaymentType.valueOf("CASH"));
        assertEquals(PaymentType.CARD, PaymentType.valueOf("CARD"));
        assertEquals(PaymentType.TRANSFER, PaymentType.valueOf("TRANSFER"));
    }

    @Test
    void sellerConstructor_ShouldSetDefaultValues() {
        Seller seller = new Seller();

        assertNull(seller.getId());
        assertNull(seller.getName());
        assertNull(seller.getContactInfo());
        assertNotNull(seller.getRegistrationDate()); // устанавливается в конструкторе
        assertNotNull(seller.getTransactions());
    }

    @Test
    void transactionConstructor_ShouldSetDefaultValues() {
        Transaction transaction = new Transaction();

        assertNull(transaction.getId());
        assertNull(transaction.getSeller());
        assertNull(transaction.getAmount());
        assertNull(transaction.getPaymentType());
        assertNotNull(transaction.getTransactionDate()); // устанавливается в конструкторе
    }
}