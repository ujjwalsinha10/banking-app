package com.commons.exceptions;

public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException(String customerId) {
        super("No customer with ID: " + customerId);
    }
}