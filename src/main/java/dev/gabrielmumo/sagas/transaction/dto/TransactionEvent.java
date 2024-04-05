package dev.gabrielmumo.sagas.transaction.dto;

public record TransactionEvent(Integer transactionId,
                               TransactionEventType transactionEventType,
                               String from,
                               String to,
                               Double amount,
                               String message) {

    public TransactionEvent build(TransactionEventType transactionEventType) {
        return new TransactionEvent(
                this.transactionId,
                transactionEventType,
                this.from,
                this.to,
                this.amount,
                this.message
        );
    }
}
