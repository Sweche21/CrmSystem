package com.crm.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AnalyticsException extends RuntimeException {

    public AnalyticsException(String message) {
        super(message);
    }

    public static AnalyticsException forNoData() {
        return new AnalyticsException("Нет данных для анализа за указанный период");
    }

    public static AnalyticsException forInvalidPeriod() {
        return new AnalyticsException("Недопустимый период. Допустимые значения: DAY, MONTH, QUARTER, YEAR");
    }

    public static AnalyticsException forSellerWithoutTransactions(Long sellerId) {
        return new AnalyticsException("У продавца с ID '" + sellerId + "' нет транзакций для анализа");
    }

    public static AnalyticsException forInsufficientData() {
        return new AnalyticsException("Недостаточно данных для выполнения аналитики");
    }
}