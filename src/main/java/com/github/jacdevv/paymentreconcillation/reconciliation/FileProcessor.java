package com.github.jacdevv.paymentreconcillation.reconciliation;

public interface FileProcessor {
    boolean supports(String contentType, String filename);

}
