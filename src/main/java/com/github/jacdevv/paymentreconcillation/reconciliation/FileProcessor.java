package com.github.jacdevv.paymentreconcillation.reconciliation;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Stream;

public interface FileProcessor {
    boolean supports(String filename);
    Stream<TransactionRecord> process(InputStream inputStream);
}
