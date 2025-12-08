package com.obs.inventory.exception;

import lombok.Getter;

@Getter
public class ErrorBusinessException extends RuntimeException {

    private final String errorNumber;

    public ErrorBusinessException(String message, String errorNumber) {
        super(message);
        this.errorNumber = errorNumber;
    }
}