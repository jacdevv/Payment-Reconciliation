package com.github.jacdevv.paymentreconcillation.reconciliation;

public interface FileProcessor {
    boolean supports(String filename);
}
