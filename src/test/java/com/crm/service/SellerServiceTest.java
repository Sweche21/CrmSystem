package com.crm.service;

import com.crm.dto.SellerDto;
import com.crm.entity.Seller;
import com.crm.exception.ResourceNotFoundException;
import com.crm.exception.DuplicateResourceException;
import com.crm.exception.ValidationException;
import com.crm.repository.SellerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SellerServiceTest {

    @Mock
    private SellerRepository sellerRepository;

    @InjectMocks
    private SellerService sellerService;

    private Seller seller;
    private SellerDto sellerDto;

    @BeforeEach
    void setUp() {
        seller = new Seller("Иван Петров", "ivan@mail.com");
        seller.setId(1L);
        seller.setRegistrationDate(LocalDateTime.now());

        sellerDto = new SellerDto("Иван Петров", "ivan@mail.com");
    }

    @Test
    void getAllSellers_ShouldReturnListOfSellers() {

        when(sellerRepository.findAll()).thenReturn(Arrays.asList(seller));

        List<SellerDto> result = sellerService.getAllSellers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Иван Петров", result.get(0).getName());
        verify(sellerRepository, times(1)).findAll();
    }

    @Test
    void getSellerById_WhenSellerExists_ShouldReturnSeller() {

        when(sellerRepository.findById(1L)).thenReturn(Optional.of(seller));

        Optional<SellerDto> result = sellerService.getSellerById(1L);

        assertTrue(result.isPresent());
        assertEquals("Иван Петров", result.get().getName());
        verify(sellerRepository, times(1)).findById(1L);
    }

    @Test
    void getSellerById_WhenSellerNotExists_ShouldReturnEmpty() {

        when(sellerRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<SellerDto> result = sellerService.getSellerById(999L);

        assertFalse(result.isPresent());
        verify(sellerRepository, times(1)).findById(999L);
    }

    @Test
    void createSeller_WithValidData_ShouldCreateSeller() {

        when(sellerRepository.findByName("Иван Петров")).thenReturn(Optional.empty());
        when(sellerRepository.save(any(Seller.class))).thenReturn(seller);

        SellerDto result = sellerService.createSeller(sellerDto);

        assertNotNull(result);
        assertEquals("Иван Петров", result.getName());
        verify(sellerRepository, times(1)).findByName("Иван Петров");
        verify(sellerRepository, times(1)).save(any(Seller.class));
    }

    @Test
    void createSeller_WithEmptyName_ShouldThrowValidationException() {

        SellerDto invalidSellerDto = new SellerDto("", "test@mail.com");


        assertThrows(ValidationException.class, () -> sellerService.createSeller(invalidSellerDto));
        verify(sellerRepository, never()).save(any(Seller.class));
    }

    @Test
    void createSeller_WithDuplicateName_ShouldThrowDuplicateResourceException() {

        when(sellerRepository.findByName("Иван Петров")).thenReturn(Optional.of(seller));


        assertThrows(DuplicateResourceException.class, () -> sellerService.createSeller(sellerDto));
        verify(sellerRepository, never()).save(any(Seller.class));
    }

    @Test
    void deleteSeller_WhenSellerExists_ShouldDeleteSeller() {

        when(sellerRepository.findById(1L)).thenReturn(Optional.of(seller));

        boolean result = sellerService.deleteSeller(1L);

        assertTrue(result);
        verify(sellerRepository, times(1)).delete(seller);
    }

    @Test
    void deleteSeller_WhenSellerNotExists_ShouldThrowResourceNotFoundException() {

        when(sellerRepository.findById(999L)).thenReturn(Optional.empty());


        assertThrows(ResourceNotFoundException.class, () -> sellerService.deleteSeller(999L));
        verify(sellerRepository, never()).delete(any(Seller.class));
    }

}