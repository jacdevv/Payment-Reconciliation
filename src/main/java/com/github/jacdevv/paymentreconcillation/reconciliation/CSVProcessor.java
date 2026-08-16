package com.github.jacdevv.paymentreconcillation.reconciliation;

public class CSVProcessor implements FileProcessor {
    @Override
    public boolean supports(String filename) {
        return filename != null &&  filename.trim().toLowerCase().endsWith(".csv");
    }
}
