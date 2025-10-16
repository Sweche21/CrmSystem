package com.crm.controller;

import com.crm.dto.SellerDto;
import com.crm.service.SellerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SellerController.class)
class SellerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SellerService sellerService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllSellers_ShouldReturnSellers() throws Exception {

        SellerDto seller = new SellerDto(1L, "Иван Петров", "ivan@mail.com", LocalDateTime.now());
        when(sellerService.getAllSellers()).thenReturn(Arrays.asList(seller));


        mockMvc.perform(get("/api/sellers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Иван Петров"));
    }

    @Test
    void getSellerById_WhenExists_ShouldReturnSeller() throws Exception {

        SellerDto seller = new SellerDto(1L, "Иван Петров", "ivan@mail.com", LocalDateTime.now());
        when(sellerService.getSellerById(1L)).thenReturn(Optional.of(seller));


        mockMvc.perform(get("/api/sellers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Иван Петров"));
    }

    @Test
    void getSellerById_WhenNotExists_ShouldReturnNotFound() throws Exception {

        when(sellerService.getSellerById(999L)).thenReturn(Optional.empty());


        mockMvc.perform(get("/api/sellers/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createSeller_WithValidData_ShouldCreateSeller() throws Exception {

        SellerDto request = new SellerDto("Иван Петров", "ivan@mail.com");
        SellerDto response = new SellerDto(1L, "Иван Петров", "ivan@mail.com", LocalDateTime.now());

        when(sellerService.createSeller(any(SellerDto.class))).thenReturn(response);


        mockMvc.perform(post("/api/sellers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Иван Петров"));
    }

    @Test
    void deleteSeller_WhenExists_ShouldReturnNoContent() throws Exception {

        when(sellerService.deleteSeller(1L)).thenReturn(true);


        mockMvc.perform(delete("/api/sellers/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteSeller_WhenNotExists_ShouldReturnNotFound() throws Exception {

        when(sellerService.deleteSeller(999L)).thenReturn(false);


        mockMvc.perform(delete("/api/sellers/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateSeller_WhenExists_ShouldUpdateSeller() throws Exception {

        SellerDto request = new SellerDto("Новое Имя", "new@mail.com");
        SellerDto response = new SellerDto(1L, "Новое Имя", "new@mail.com", LocalDateTime.now());

        when(sellerService.updateSeller(eq(1L), any(SellerDto.class))).thenReturn(Optional.of(response));


        mockMvc.perform(put("/api/sellers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Новое Имя"));
    }

    @Test
    void updateSeller_WhenNotExists_ShouldReturnNotFound() throws Exception {

        SellerDto request = new SellerDto("Новое Имя", "new@mail.com");

        when(sellerService.updateSeller(eq(999L), any(SellerDto.class))).thenReturn(Optional.empty());


        mockMvc.perform(put("/api/sellers/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

}