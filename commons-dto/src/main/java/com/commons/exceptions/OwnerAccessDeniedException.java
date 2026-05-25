package com.commons.exceptions;

public class OwnerAccessDeniedException extends RuntimeException {
    public OwnerAccessDeniedException() {
        super("Invalid Owner " );
    }
}