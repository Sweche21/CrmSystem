package com.crm.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }

    public static DuplicateResourceException forSeller(String name) {
        return new DuplicateResourceException("Продавец с именем '" + name + "' уже существует");
    }

    public static DuplicateResourceException forEmail(String email) {
        return new DuplicateResourceException("Продавец с email '" + email + "' уже зарегистрирован");
    }
}