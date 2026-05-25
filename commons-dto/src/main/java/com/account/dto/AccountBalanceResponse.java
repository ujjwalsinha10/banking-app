package com.account.dto;

import java.math.BigDecimal;


public record AccountBalanceResponse(
        BigDecimal balance,
        BigDecimal totalHolds,
        BigDecimal available
) {
}