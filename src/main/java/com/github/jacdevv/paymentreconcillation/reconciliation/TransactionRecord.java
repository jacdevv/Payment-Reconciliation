package com.github.jacdevv.paymentreconcillation.reconciliation;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRecord {

    @CsvBindByName(column = "transaction_id")
    private String transactionId;

    @CsvBindByName(column = "reference")
    private String reference;

    @CsvBindByName(column = "amount")
    private BigDecimal amount;

    @CsvBindByName(column = "currency")
    private String currency;

    @CsvBindByName(column = "status")
    private TransactionStatus status;

    @CsvBindByName(column = "timestamp")
    @CsvDate(value = "yyyy-MM-dd'T'HH:mm:ss[XXX][X]")
    private Instant timestamp;

    public TransactionRecord toRecord() {
        return new TransactionRecord(
                transactionId,
                reference,
                amount,
                currency,
                status,
                timestamp
        );
    }
}