package com.github.jacdevv.paymentreconcillation.reconciliation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ReconciliationService {
    private final CSVProcessor csvProcessor;

    List<TransactionRecord> normalize(List<TransactionRecord> transactionRecords) {
        if (transactionRecords == null) {
            return List.of();
        }

        return transactionRecords.stream()
                .filter(Objects::nonNull)
                .map(this::normalizeRecord)
                .toList();
    }

    private TransactionRecord normalizeRecord(TransactionRecord record) {
        String normalizedTransactionId = record.getTransactionId() != null
                ? record.getTransactionId().trim()
                : null;
        String normalizedReference = record.getReference() != null
                ? record.getReference().trim()
                : null;
        String normalizedCurrency = record.getCurrency() != null
                ? record.getCurrency().trim().toUpperCase(Locale.ROOT)
                : null;
        BigDecimal normalizedAmount = record.getAmount() != null
                ? record.getAmount().setScale(2, RoundingMode.HALF_UP)
                : null;

        return new TransactionRecord(
                normalizedTransactionId,
                normalizedReference,
                normalizedAmount,
                normalizedCurrency,
                record.getStatus(),
                record.getTimestamp()
        );
    }
}
