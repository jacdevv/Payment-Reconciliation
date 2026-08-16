package com.github.jacdevv.paymentreconcillation.reconciliation;

import com.opencsv.bean.CsvToBeanBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class CSVProcessor implements FileProcessor {
    @Override
    public boolean supports(String filename) {
        return filename != null &&  filename.trim().toLowerCase().endsWith(".csv");
    }

    @Override
    public List<TransactionRecord> process(InputStream inputStream) {
        try (var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return new CsvToBeanBuilder<TransactionRecord>(reader)
                    .withType(TransactionRecord.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build()
                    .parse()
                    .stream()
                    .map(TransactionRecord::toRecord)
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse CSV from stream", e);
        }
    }
}
