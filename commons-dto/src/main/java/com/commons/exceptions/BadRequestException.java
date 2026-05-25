package com.commons.exceptions;

import java.util.UUID;

public class BadRequestException extends RuntimeException {
	
    public BadRequestException(String desc) {
        super(desc);
    }

}