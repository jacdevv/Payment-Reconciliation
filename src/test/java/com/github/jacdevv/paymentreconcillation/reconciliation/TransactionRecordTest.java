package com.github.jacdevv.paymentreconcillation.reconciliation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionRecordTest {

    @Test
    @DisplayName("toRecord() should copy all fields to a new TransactionRecord instance")
    void toRecord_shouldCreateCopyOfTransactionRecord() {
        Instant now = Instant.parse("2026-08-16T12:00:00Z");
        TransactionRecord original = new TransactionRecord(
                "TXN-123",
                "REF-456",
                new BigDecimal("250.00"),
                "USD",
                TransactionStatus.SUCCESS,
                now
        );

        TransactionRecord copy = original.toRecord();

        assertThat(copy).isNotNull();
        assertThat(copy.getTransactionId()).isEqualTo(original.getTransactionId());
        assertThat(copy.getReference()).isEqualTo(original.getReference());
        assertThat(copy.getAmount()).isEqualByComparingTo(original.getAmount());
        assertThat(copy.getCurrency()).isEqualTo(original.getCurrency());
        assertThat(copy.getStatus()).isEqualTo(original.getStatus());
        assertThat(copy.getTimestamp()).isEqualTo(original.getTimestamp());
        assertThat(copy).isEqualTo(original);
    }

    @Test
    @DisplayName("TransactionStatus enum values should be defined correctly")
    void transactionStatus_shouldContainExpectedValues() {
        assertThat(TransactionStatus.values()).containsExactlyInAnyOrder(
                TransactionStatus.SUCCESS,
                TransactionStatus.FAILED,
                TransactionStatus.REFUNDED,
                TransactionStatus.PENDING
        );
    }
}
