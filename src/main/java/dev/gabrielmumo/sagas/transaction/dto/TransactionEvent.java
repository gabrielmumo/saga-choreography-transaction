package dev.gabrielmumo.sagas.transaction.dto;

public record TransactionEvent(Integer transactionId,
                               TransactionEventType transactionEventType,
                               String from,
                               String to,
                               Double amount,
                               String message) {
}
