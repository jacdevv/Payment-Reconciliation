package com.github.jacdevv.paymentreconcillation.reconciliation;

import java.io.InputStream;
import java.util.List;

public interface FileProcessor {
    boolean supports(String filename);
    List<TransactionRecord> process(InputStream inputStream);
}
