package com.github.jacdevv.paymentreconcillation.reconciliation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ReconciliationServiceTest {

    @Mock
    private CSVProcessor csvProcessor;

    private ReconciliationService service;

    @BeforeEach
    void setUp() {
        service = new ReconciliationService(csvProcessor);
    }

    @Test
    @DisplayName("normalize() should return empty list when given null")
    void normalize_shouldReturnEmptyListWhenInputIsNull() {
        List<TransactionRecord> result = service.normalize(null);
        assertThat(result).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("normalize() should return empty list when given empty list")
    void normalize_shouldReturnEmptyListWhenInputIsEmpty() {
        List<TransactionRecord> result = service.normalize(List.of());
        assertThat(result).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("normalize() should filter out null records in the list")
    void normalize_shouldFilterNullRecords() {
        List<TransactionRecord> input = new ArrayList<>();
        input.add(null);
        input.add(new TransactionRecord("TXN-1", "REF-1", new BigDecimal("10.00"), "USD", TransactionStatus.SUCCESS, Instant.now()));
        input.add(null);

        List<TransactionRecord> result = service.normalize(input);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTransactionId()).isEqualTo("TXN-1");
    }

    @Test
    @DisplayName("normalize() should trim strings, uppercase currency, and normalize amount scale")
    void normalize_shouldNormalizeRecordFields() {
        Instant timestamp = Instant.parse("2026-08-16T12:00:00Z");
        TransactionRecord unnormalized = new TransactionRecord(
                "  TXN-001  ",
                "  REF-ABC  ",
                new BigDecimal("50.5"),
                "  eur  ",
                TransactionStatus.SUCCESS,
                timestamp
        );

        List<TransactionRecord> result = service.normalize(List.of(unnormalized));

        assertThat(result).hasSize(1);
        TransactionRecord normalized = result.get(0);
        assertThat(normalized.getTransactionId()).isEqualTo("TXN-001");
        assertThat(normalized.getReference()).isEqualTo("REF-ABC");
        assertThat(normalized.getCurrency()).isEqualTo("EUR");
        assertThat(normalized.getAmount()).isEqualByComparingTo(new BigDecimal("50.50"));
        assertThat(normalized.getAmount().scale()).isEqualTo(2);
        assertThat(normalized.getStatus()).isEqualTo(TransactionStatus.SUCCESS);
        assertThat(normalized.getTimestamp()).isEqualTo(timestamp);
    }

    @Test
    @DisplayName("normalize() should handle null fields within a record gracefully")
    void normalize_shouldHandleNullFieldsInsideRecord() {
        TransactionRecord nullFieldsRecord = new TransactionRecord(
                null,
                null,
                null,
                null,
                null,
                null
        );

        List<TransactionRecord> result = service.normalize(List.of(nullFieldsRecord));

        assertThat(result).hasSize(1);
        TransactionRecord record = result.get(0);
        assertThat(record.getTransactionId()).isNull();
        assertThat(record.getReference()).isNull();
        assertThat(record.getAmount()).isNull();
        assertThat(record.getCurrency()).isNull();
        assertThat(record.getStatus()).isNull();
        assertThat(record.getTimestamp()).isNull();
    }
}
