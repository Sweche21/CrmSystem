package com.crm.service;
import java.util.Arrays;
import com.crm.dto.TransactionDto;
import com.crm.dto.AnalyticsDto;
import com.crm.entity.Transaction;
import com.crm.entity.Seller;
import com.crm.entity.PaymentType;
import com.crm.exception.ResourceNotFoundException;
import com.crm.exception.ValidationException;
import com.crm.exception.AnalyticsException;
import com.crm.repository.TransactionRepository;
import com.crm.repository.SellerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private SellerRepository sellerRepository;

    public List<TransactionDto> getAllTransactions() {
        try {
            return transactionRepository.findAll().stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при получении списка транзакций: " + e.getMessage());
        }
    }

    public Optional<TransactionDto> getTransactionById(Long id) {
        if (id == null) {
            throw ValidationException.forInvalidId();
        }

        return transactionRepository.findById(id)
                .map(this::convertToDto);
    }

    public TransactionDto createTransaction(TransactionDto transactionDto) {
        // Валидация входных данных
        validateTransactionDto(transactionDto);

        // Поиск продавца
        Seller seller = sellerRepository.findById(transactionDto.getSellerId())
                .orElseThrow(() -> ResourceNotFoundException.forSeller(transactionDto.getSellerId()));

        try {
            Transaction transaction = new Transaction();
            transaction.setSeller(seller);
            transaction.setAmount(transactionDto.getAmount());
            transaction.setPaymentType(PaymentType.valueOf(transactionDto.getPaymentType()));
            transaction.setTransactionDate(LocalDateTime.now());

            Transaction savedTransaction = transactionRepository.save(transaction);
            return convertToDto(savedTransaction);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании транзакции: " + e.getMessage());
        }
    }

    public List<TransactionDto> getTransactionsBySellerId(Long sellerId) {
        if (sellerId == null) {
            throw ValidationException.forInvalidId();
        }

        // Проверяем существование продавца
        if (!sellerRepository.existsById(sellerId)) {
            throw ResourceNotFoundException.forSeller(sellerId);
        }

        try {
            return transactionRepository.findBySellerId(sellerId).stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при получении транзакций продавца: " + e.getMessage());
        }
    }

    public List<TransactionDto> getTransactionsBySellerIdAndPeriod(Long sellerId, LocalDateTime startDate, LocalDateTime endDate) {
        if (sellerId == null) {
            throw ValidationException.forInvalidId();
        }

        if (startDate == null || endDate == null) {
            throw ValidationException.forInvalidDateRange();
        }

        if (startDate.isAfter(endDate)) {
            throw ValidationException.forInvalidDateRange();
        }

        // Проверяем существование продавца
        if (!sellerRepository.existsById(sellerId)) {
            throw ResourceNotFoundException.forSeller(sellerId);
        }

        try {
            return transactionRepository.findBySellerIdAndPeriod(sellerId, startDate, endDate).stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при получении транзакций за период: " + e.getMessage());
        }
    }

    public Optional<AnalyticsDto.TopSeller> getTopSellerByPeriod(String period) {
        if (period == null || period.trim().isEmpty()) {
            throw AnalyticsException.forInvalidPeriod();
        }

        // Валидация периода
        String upperPeriod = period.toUpperCase();
        if (!Arrays.asList("DAY", "MONTH", "QUARTER", "YEAR").contains(upperPeriod)) {
            throw AnalyticsException.forInvalidPeriod();
        }

        LocalDateTime startDate;
        LocalDateTime endDate = LocalDateTime.now();

        try {
            switch (upperPeriod) {
                case "DAY":
                    startDate = endDate.truncatedTo(ChronoUnit.DAYS);
                    break;
                case "MONTH":
                    startDate = endDate.withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS);
                    break;
                case "QUARTER":
                    int currentQuarter = (endDate.getMonthValue() - 1) / 3 + 1;
                    startDate = endDate.withMonth((currentQuarter - 1) * 3 + 1)
                            .withDayOfMonth(1)
                            .truncatedTo(ChronoUnit.DAYS);
                    break;
                case "YEAR":
                    startDate = endDate.withDayOfYear(1).truncatedTo(ChronoUnit.DAYS);
                    break;
                default:
                    throw AnalyticsException.forInvalidPeriod();
            }

            List<Object[]> result = transactionRepository.findTopSellerByPeriod(startDate, endDate);

            // Берем первый элемент из результата (самый продуктивный)
            if (result.isEmpty()) {
                return Optional.empty();
            }

            Object[] topSellerData = result.get(0);
            AnalyticsDto.TopSeller topSeller = new AnalyticsDto.TopSeller(
                    (Long) topSellerData[0],
                    (String) topSellerData[1],
                    (BigDecimal) topSellerData[2],
                    period
            );

            return Optional.of(topSeller);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при анализе топ-продавца: " + e.getMessage());
        }
    }

    public Optional<AnalyticsDto.BestPeriod> getBestPeriodForSeller(Long sellerId) {
        if (sellerId == null) {
            throw ValidationException.forInvalidId();
        }

        // Проверяем существование продавца
        if (!sellerRepository.existsById(sellerId)) {
            throw ResourceNotFoundException.forSeller(sellerId);
        }

        List<Transaction> transactions;
        try {
            transactions = transactionRepository.findBySellerIdOrderByDate(sellerId);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при получении транзакций для анализа: " + e.getMessage());
        }

        if (transactions.isEmpty()) {
            throw AnalyticsException.forSellerWithoutTransactions(sellerId);
        }

        if (transactions.size() < 2) {
            throw AnalyticsException.forInsufficientData();
        }

        try {
            return findBestPeriod(transactions);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при анализе лучшего периода: " + e.getMessage());
        }
    }

    public BigDecimal getTotalAmountBySellerAndPeriod(Long sellerId, LocalDateTime startDate, LocalDateTime endDate) {
        if (sellerId == null) {
            throw ValidationException.forInvalidId();
        }

        if (startDate == null || endDate == null) {
            throw ValidationException.forInvalidDateRange();
        }

        if (startDate.isAfter(endDate)) {
            throw ValidationException.forInvalidDateRange();
        }

        // Проверяем существование продавца
        if (!sellerRepository.existsById(sellerId)) {
            throw ResourceNotFoundException.forSeller(sellerId);
        }

        try {
            List<Transaction> transactions = transactionRepository.findBySellerIdAndPeriod(sellerId, startDate, endDate);
            return transactions.stream()
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при расчете общей суммы: " + e.getMessage());
        }
    }

    private Optional<AnalyticsDto.BestPeriod> findBestPeriod(List<Transaction> transactions) {
        if (transactions.size() == 1) {
            Transaction single = transactions.get(0);
            return Optional.of(new AnalyticsDto.BestPeriod(
                    single.getTransactionDate(),
                    single.getTransactionDate(),
                    1L,
                    single.getAmount()
            ));
        }

        // Сортируем транзакции по дате
        transactions.sort(Comparator.comparing(Transaction::getTransactionDate));

        AnalyticsDto.BestPeriod bestPeriod = null;
        double maxDensity = 0;

        try {
            // Анализируем различные размеры окон (от 1 дня до 30 дней)
            for (int windowDays = 1; windowDays <= 30; windowDays++) {
                for (int i = 0; i < transactions.size(); i++) {
                    LocalDateTime windowStart = transactions.get(i).getTransactionDate();
                    LocalDateTime windowEnd = windowStart.plusDays(windowDays);

                    List<Transaction> windowTransactions = getTransactionsInWindow(transactions, windowStart, windowEnd);

                    if (!windowTransactions.isEmpty()) {
                        long count = windowTransactions.size();
                        BigDecimal totalAmount = windowTransactions.stream()
                                .map(Transaction::getAmount)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                        // Плотность = количество транзакций / длительность периода в днях
                        double density = count / (double) windowDays;

                        if (density > maxDensity ||
                                (density == maxDensity && count > (bestPeriod != null ? bestPeriod.getTransactionCount() : 0))) {
                            maxDensity = density;
                            bestPeriod = new AnalyticsDto.BestPeriod(windowStart, windowEnd, count, totalAmount);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка в алгоритме поиска лучшего периода: " + e.getMessage());
        }

        return Optional.ofNullable(bestPeriod);
    }

    private List<Transaction> getTransactionsInWindow(List<Transaction> transactions,
                                                      LocalDateTime start,
                                                      LocalDateTime end) {
        return transactions.stream()
                .filter(t -> !t.getTransactionDate().isBefore(start) && t.getTransactionDate().isBefore(end))
                .collect(Collectors.toList());
    }

    private void validateTransactionDto(TransactionDto transactionDto) {
        if (transactionDto == null) {
            throw ValidationException.forNullObject();
        }

        if (transactionDto.getSellerId() == null) {
            throw ValidationException.forMissingSellerId();
        }

        if (transactionDto.getAmount() == null) {
            throw ValidationException.forInvalidAmount();
        }

        if (transactionDto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw ValidationException.forInvalidAmount();
        }

        if (transactionDto.getAmount().compareTo(new BigDecimal("1000000")) > 0) {
            throw ValidationException.forAmountTooLarge();
        }

        if (transactionDto.getPaymentType() == null || transactionDto.getPaymentType().trim().isEmpty()) {
            throw ValidationException.forInvalidPaymentType();
        }

        try {
            PaymentType.valueOf(transactionDto.getPaymentType());
        } catch (IllegalArgumentException e) {
            throw ValidationException.forInvalidPaymentType();
        }
    }

    private TransactionDto convertToDto(Transaction transaction) {
        return new TransactionDto(
                transaction.getId(),
                transaction.getSeller().getId(),
                transaction.getSeller().getName(),
                transaction.getAmount(),
                transaction.getPaymentType().name(),
                transaction.getTransactionDate()
        );
    }
}