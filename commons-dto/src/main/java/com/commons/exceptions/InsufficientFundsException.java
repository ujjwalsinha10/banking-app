package com.commons.exceptions;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException() {
        super("Insufficient Funds");
    }
}