package com.crm.controller;

import com.crm.dto.SellerDto;
import com.crm.dto.AnalyticsDto;
import com.crm.service.SellerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/sellers")
@Tag(name = "Продавцы", description = "API для управления продавцами")
public class SellerController {

    @Autowired
    private SellerService sellerService;

    @GetMapping
    @Operation(summary = "Получить всех продавцов", description = "Возвращает список всех продавцов в системе")
    public List<SellerDto> getAllSellers() {
        return sellerService.getAllSellers();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить продавца по ID", description = "Возвращает информацию о конкретном продавце")
    public ResponseEntity<SellerDto> getSellerById(@PathVariable Long id) {
        return sellerService.getSellerById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Создать нового продавца", description = "Создает нового продавца в системе")
    public SellerDto createSeller(@RequestBody SellerDto sellerDto) {
        return sellerService.createSeller(sellerDto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить информацию о продавце", description = "Обновляет информацию о существующем продавце")
    public ResponseEntity<SellerDto> updateSeller(@PathVariable Long id, @RequestBody SellerDto sellerDto) {
        return sellerService.updateSeller(id, sellerDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить продавца", description = "Удаляет продавца из системы")
    public ResponseEntity<Void> deleteSeller(@PathVariable Long id) {
        return sellerService.deleteSeller(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/analytics/low-performance")
    @Operation(summary = "Продавцы с низкой производительностью",
            description = "Возвращает список продавцов, у которых сумма транзакций за период меньше указанной")
    public List<AnalyticsDto.SellerWithTotal> getSellersWithLowPerformance(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam BigDecimal minAmount) {
        return sellerService.getSellersWithTotalLessThan(startDate, endDate, minAmount);
    }
}