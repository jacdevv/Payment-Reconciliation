package com.github.jacdevv.paymentreconcillation.reconciliation;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionRecord(
        String transactionId,
        String reference,
        BigDecimal amount,
        String currency,
        TransactionStatus status,
        Instant timestampj
) {}