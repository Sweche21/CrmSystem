package com.crm.controller;

import com.crm.dto.TransactionDto;
import com.crm.dto.AnalyticsDto;
import com.crm.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@Tag(name = "Транзакции", description = "API для управления транзакциями")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @GetMapping
    @Operation(summary = "Получить все транзакции", description = "Возвращает список всех транзакций в системе")
    public List<TransactionDto> getAllTransactions() {
        return transactionService.getAllTransactions();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить транзакцию по ID", description = "Возвращает информацию о конкретной транзакции")
    public ResponseEntity<TransactionDto> getTransactionById(@PathVariable Long id) {
        return transactionService.getTransactionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Создать новую транзакцию", description = "Создает новую транзакцию в системе")
    public TransactionDto createTransaction(@RequestBody TransactionDto transactionDto) {
        return transactionService.createTransaction(transactionDto);
    }

    @GetMapping("/seller/{sellerId}")
    @Operation(summary = "Получить транзакции продавца", description = "Возвращает все транзакции конкретного продавца")
    public List<TransactionDto> getTransactionsBySeller(@PathVariable Long sellerId) {
        return transactionService.getTransactionsBySellerId(sellerId);
    }

    @GetMapping("/analytics/top-seller")
    @Operation(summary = "Самый продуктивный продавец",
            description = "Возвращает самого продуктивного продавца за указанный период (DAY, MONTH, QUARTER, YEAR)")
    public ResponseEntity<AnalyticsDto.TopSeller> getTopSeller(@RequestParam String period) {
        return transactionService.getTopSellerByPeriod(period)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/analytics/best-period/{sellerId}")
    @Operation(summary = "Лучший период продавца",
            description = "Возвращает самый продуктивный период времени для конкретного продавца")
    public ResponseEntity<AnalyticsDto.BestPeriod> getBestPeriodForSeller(@PathVariable Long sellerId) {
        return transactionService.getBestPeriodForSeller(sellerId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}