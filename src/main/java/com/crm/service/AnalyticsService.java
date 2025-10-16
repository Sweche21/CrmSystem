package com.crm.service;

import com.crm.repository.SellerRepository;
import com.crm.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class AnalyticsService {

    private final TransactionRepository transactionRepository;
    private final SellerRepository sellerRepository;

    public AnalyticsService(TransactionRepository transactionRepository,
                            SellerRepository sellerRepository) {
        this.transactionRepository = transactionRepository;
        this.sellerRepository = sellerRepository;
    }

    public BigDecimal getTotalSales() {
        // Используем нативный запрос для суммы всех транзакций
        BigDecimal total = transactionRepository.getTotalSalesAmount();
        return total != null ? total : BigDecimal.ZERO;
    }

    public BigDecimal getAverageTransactionAmount() {
        Long transactionCount = transactionRepository.count();
        if (transactionCount == null || transactionCount == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalSales = getTotalSales();
        if (totalSales.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        // Вычисляем среднее значение
        return totalSales.divide(BigDecimal.valueOf(transactionCount), 2, RoundingMode.HALF_UP);
    }

    public long getTransactionCount() {
        return transactionRepository.count();
    }

    public long getSellerCount() {
        return sellerRepository.count();
    }

    // Дополнительные аналитические методы
    public BigDecimal getTotalSalesByPaymentType(String paymentType) {
        BigDecimal total = transactionRepository.getTotalSalesByPaymentType(paymentType);
        return total != null ? total : BigDecimal.ZERO;
    }

    public Long getTransactionCountBySeller(Long sellerId) {
        return transactionRepository.countBySellerId(sellerId);
    }
}