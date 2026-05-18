package com.commons.exceptions;

import java.util.UUID;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(UUID accountId) {
        super("No account with ID: " + accountId);
    }
}