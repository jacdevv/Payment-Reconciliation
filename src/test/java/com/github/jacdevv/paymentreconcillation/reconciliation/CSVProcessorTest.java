package com.github.jacdevv.paymentreconcillation.reconciliation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CSVProcessorTest {

    private CSVProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new CSVProcessor();
    }

    @Nested
    @DisplayName("supports() Filename Checks")
    class SupportsTests {

        @ParameterizedTest
        @ValueSource(strings = {
                "transactions.csv",
                "DATA.CSV",
                "Transactions.Csv",
                "path/to/transactions.csv",
                "C:\\files\\transactions.csv",
                " transactions.csv "
        })
        void supports_shouldReturnTrueForValidCsvFiles(String filename) {
            assertThat(processor.supports(filename)).isTrue();
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {
                "   ",
                "transactions.xlsx",
                "statement.json",
                "report.txt",
                "csv",
                ".csv.bak",
                "transactions.csv1"
        })
        void supports_shouldReturnFalseForInvalidFilenames(String filename) {
            assertThat(processor.supports(filename)).isFalse();
        }
    }

    @Nested
    @DisplayName("process() CSV Parsing")
    class ProcessTests {

        @Test
        void process_shouldParseValidCsvStreamCorrectly() {
            String csv = """
                    transaction_id,reference,amount,currency,status,timestamp
                    TXN-001,REF-001,150.75,USD,SUCCESS,2026-08-16T12:00:00Z
                    TXN-002,REF-002,3000.00,EUR,FAILED,2026-08-16T14:30:00+07
                    TXN-003,REF-003,50.25,GBP,REFUNDED,2026-08-16T08:15:30Z
                    TXN-004,REF-004,10.00,USD,PENDING,2026-08-16T18:00:00Z
                    """;
            InputStream stream = new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8));

            List<TransactionRecord> records = processor.process(stream);

            assertThat(records).hasSize(4);

            TransactionRecord first = records.get(0);
            assertThat(first.getTransactionId()).isEqualTo("TXN-001");
            assertThat(first.getReference()).isEqualTo("REF-001");
            assertThat(first.getAmount()).isEqualByComparingTo(new BigDecimal("150.75"));
            assertThat(first.getCurrency()).isEqualTo("USD");
            assertThat(first.getStatus()).isEqualTo(TransactionStatus.SUCCESS);
            assertThat(first.getTimestamp()).isEqualTo(Instant.parse("2026-08-16T12:00:00Z"));

            TransactionRecord second = records.get(1);
            assertThat(second.getTransactionId()).isEqualTo("TXN-002");
            assertThat(second.getStatus()).isEqualTo(TransactionStatus.FAILED);
            assertThat(second.getCurrency()).isEqualTo("EUR");

            TransactionRecord third = records.get(2);
            assertThat(third.getStatus()).isEqualTo(TransactionStatus.REFUNDED);

            TransactionRecord fourth = records.get(3);
            assertThat(fourth.getStatus()).isEqualTo(TransactionStatus.PENDING);
        }

        @Test
        void process_shouldReturnEmptyListWhenCsvOnlyHasHeader() {
            String csv = "transaction_id,reference,amount,currency,status,timestamp\n";
            InputStream stream = new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8));

            List<TransactionRecord> records = processor.process(stream);

            assertThat(records).isEmpty();
        }

        @Test
        void process_shouldThrowExceptionForInvalidStatusEnum() {
            String csv = """
                    transaction_id,reference,amount,currency,status,timestamp
                    TXN-001,REF-001,100.00,USD,INVALID_STATUS,2026-08-16T12:00:00Z
                    """;
            InputStream stream = new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8));

            assertThatThrownBy(() -> processor.process(stream))
                    .isInstanceOf(RuntimeException.class);
        }

        @Test
        void process_shouldThrowExceptionForInvalidAmount() {
            String csv = """
                    transaction_id,reference,amount,currency,status,timestamp
                    TXN-001,REF-001,INVALID_AMOUNT,USD,SUCCESS,2026-08-16T12:00:00Z
                    """;
            InputStream stream = new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8));

            assertThatThrownBy(() -> processor.process(stream))
                    .isInstanceOf(RuntimeException.class);
        }

        @Test
        void process_shouldThrowExceptionForInvalidTimestamp() {
            String csv = """
                    transaction_id,reference,amount,currency,status,timestamp
                    TXN-001,REF-001,100.00,USD,SUCCESS,INVALID_DATE
                    """;
            InputStream stream = new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8));

            assertThatThrownBy(() -> processor.process(stream))
                    .isInstanceOf(RuntimeException.class);
        }
    }
}
