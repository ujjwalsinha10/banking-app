package com.commons.exceptions;
public class PreconditionRequiredException extends RuntimeException {
    public PreconditionRequiredException(String msg) { super(msg); }
}