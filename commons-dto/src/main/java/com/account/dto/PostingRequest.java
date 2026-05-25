package com.account.dto;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record PostingRequest(
        @NotNull @Positive BigDecimal amount,
        String reason
) {}