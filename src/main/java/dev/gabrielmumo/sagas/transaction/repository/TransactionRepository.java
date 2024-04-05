package dev.gabrielmumo.sagas.transaction.repository;

import dev.gabrielmumo.sagas.transaction.dto.TransactionEvent;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class TransactionRepository {
    private final Map<Integer, TransactionEvent> mockedDB = new HashMap<>();

    public void upsertTransaction(TransactionEvent transactionEvent) {
        mockedDB.put(transactionEvent.transactionId(), transactionEvent);
    }

    public Optional<TransactionEvent> findTransaction(Integer transactionId) {
        return Optional.ofNullable(mockedDB.get(transactionId));
    }
}
