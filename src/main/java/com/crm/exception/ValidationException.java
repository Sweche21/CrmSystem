package com.crm.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }

    public static ValidationException forEmptyName() {
        return new ValidationException("Имя продавца не может быть пустым");
    }

    public static ValidationException forInvalidAmount() {
        return new ValidationException("Сумма транзакции должна быть положительной");
    }

    public static ValidationException forInvalidPaymentType() {
        return new ValidationException("Недопустимый тип оплаты. Допустимые значения: CASH, CARD, TRANSFER");
    }

    public static ValidationException forInvalidDateRange() {
        return new ValidationException("Некорректный диапазон дат. Дата начала должна быть раньше даты окончания");
    }

    public static ValidationException forNegativeAmount() {
        return new ValidationException("Сумма не может быть отрицательной");
    }

    public static ValidationException forInvalidId() {
        return new ValidationException("Недопустимый идентификатор");
    }

    public static ValidationException forMissingSellerId() {
        return new ValidationException("ID продавца обязателен для заполнения");
    }

    public static ValidationException forNullObject() {
        return new ValidationException("Переданный объект не может быть null");
    }

    public static ValidationException forAmountTooLarge() {
        return new ValidationException("Сумма транзакции слишком велика");
    }

    public static ValidationException forSellerWithTransactions() {
        return new ValidationException("Невозможно удалить продавца. У продавца есть связанные транзакции");
    }
}