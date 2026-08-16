package com.github.jacdevv.paymentreconcillation.reconciliation;

import com.opencsv.bean.CsvToBeanBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

public class CSVProcessor implements FileProcessor {
    @Override
    public boolean supports(String filename) {
        return filename != null && filename.trim().toLowerCase().endsWith(".csv");
    }

    @Override
    public Stream<TransactionRecord> process(InputStream inputStream) {
        var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        return new CsvToBeanBuilder<TransactionRecord>(reader)
                .withType(TransactionRecord.class)
                .withIgnoreLeadingWhiteSpace(true)
                .build()
                .stream()
                .onClose(() -> {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        throw new UncheckedIOException("Failed to close CSV reader", e);
                    }
                });
    }
}
