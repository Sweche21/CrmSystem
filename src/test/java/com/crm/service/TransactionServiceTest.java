package com.crm.service;

import com.crm.dto.TransactionDto;
import com.crm.entity.PaymentType;
import com.crm.entity.Seller;
import com.crm.entity.Transaction;
import com.crm.exception.ResourceNotFoundException;
import com.crm.exception.ValidationException;
import com.crm.repository.SellerRepository;
import com.crm.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private SellerRepository sellerRepository;

    @InjectMocks
    private TransactionService transactionService;

    private Seller seller;
    private Transaction transaction;
    private TransactionDto transactionDto;

    @BeforeEach
    void setUp() {
        seller = new Seller("Иван Петров", "ivan@mail.com");
        seller.setId(1L);

        transaction = new Transaction(seller, new BigDecimal("1000.50"), PaymentType.CARD);
        transaction.setId(1L);

        transactionDto = new TransactionDto(1L, new BigDecimal("1000.50"), "CARD");
    }

    @Test
    void getAllTransactions_ShouldReturnListOfTransactions() {

        when(transactionRepository.findAll()).thenReturn(Arrays.asList(transaction));

        List<TransactionDto> result = transactionService.getAllTransactions();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(transactionRepository, times(1)).findAll();
    }

    @Test
    void getTransactionById_WhenTransactionExists_ShouldReturnTransaction() {

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));

        Optional<TransactionDto> result = transactionService.getTransactionById(1L);

        assertTrue(result.isPresent());
        verify(transactionRepository, times(1)).findById(1L);
    }

    @Test
    void createTransaction_WithValidData_ShouldCreateTransaction() {

        when(sellerRepository.findById(1L)).thenReturn(Optional.of(seller));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        TransactionDto result = transactionService.createTransaction(transactionDto);

        assertNotNull(result);
        verify(sellerRepository, times(1)).findById(1L);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void createTransaction_WithInvalidSeller_ShouldThrowResourceNotFoundException() {

        when(sellerRepository.findById(999L)).thenReturn(Optional.empty());

        TransactionDto invalidDto = new TransactionDto(999L, new BigDecimal("100"), "CARD");
        assertThrows(ResourceNotFoundException.class, () -> transactionService.createTransaction(invalidDto));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void createTransaction_WithNegativeAmount_ShouldThrowValidationException() {


        TransactionDto invalidDto = new TransactionDto(1L, new BigDecimal("-100"), "CARD");
        assertThrows(ValidationException.class, () -> transactionService.createTransaction(invalidDto));
        verify(sellerRepository, never()).findById(any());
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void createTransaction_WithInvalidPaymentType_ShouldThrowValidationException() {


        TransactionDto invalidDto = new TransactionDto(1L, new BigDecimal("100"), "INVALID");
        assertThrows(ValidationException.class, () -> transactionService.createTransaction(invalidDto));
        verify(sellerRepository, never()).findById(any());
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void getTransactionsBySellerId_ShouldReturnTransactions() {

        when(sellerRepository.existsById(1L)).thenReturn(true);
        when(transactionRepository.findBySellerId(1L)).thenReturn(Arrays.asList(transaction));

        List<TransactionDto> result = transactionService.getTransactionsBySellerId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(transactionRepository, times(1)).findBySellerId(1L);
    }

    @Test
    void getTransactionsBySellerId_WhenSellerNotExists_ShouldThrowException() {

        when(sellerRepository.existsById(999L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> transactionService.getTransactionsBySellerId(999L));
        verify(transactionRepository, never()).findBySellerId(any());
    }

}