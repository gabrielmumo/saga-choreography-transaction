package dev.gabrielmumo.sagas.transaction.service;

import dev.gabrielmumo.sagas.transaction.dto.TransactionEvent;
import dev.gabrielmumo.sagas.transaction.dto.TransactionEventType;
import dev.gabrielmumo.sagas.transaction.producer.TransactionEventsProducer;
import dev.gabrielmumo.sagas.transaction.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TransactionService {
    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);
    @Value("${spring.kafka.transaction-created-topic}")
    public String transactionCreatedTopic;
    private final TransactionRepository repository;
    private final TransactionEventsProducer transactionEventsProducer;

    public TransactionService(TransactionRepository repository, TransactionEventsProducer transactionEventsProducer) {
        this.repository = repository;
        this.transactionEventsProducer = transactionEventsProducer;
    }

    public void insertTransaction(TransactionEvent transactionEvent) {
        try {
            var completableFuture = transactionEventsProducer.sendTransactionEvent(
                    transactionEvent,
                    transactionCreatedTopic
            );

            completableFuture.whenComplete((sendResult, throwable) -> {
                if(throwable != null) {
                    log.error("Error sending transaction: {} with key: {}",
                            transactionEvent,
                            transactionEvent.transactionId(),
                            throwable
                    );
                    markTransactionAsFailed(transactionEvent);
                } else {
                    log.info("Transaction: {} was sent successfully. Key: {} & partition: {}",
                            transactionEvent,
                            transactionEvent.transactionId(),
                            sendResult.getRecordMetadata().partition()
                    );
                    repository.upsertTransaction(transactionEvent);
                }
            });
        } catch (Exception e) {
            log.error("Error sending transaction: {} with key: {}",
                    transactionEvent,
                    transactionEvent.transactionId(),
                    e
            );
            markTransactionAsFailed(transactionEvent);
        }
    }

    private void markTransactionAsFailed(TransactionEvent transactionEvent) {
        var failedEvent = transactionEvent.build(TransactionEventType.FAILED);
        insertTransaction(failedEvent);
    }

    public void updateTransaction(TransactionEvent transactionEvent) {
        var transaction = repository.findTransaction(transactionEvent.transactionId());
        if (transaction.isPresent()) {
            repository.upsertTransaction(transactionEvent);
        }
    }

    public Optional<TransactionEvent> findTransactionEvent(Integer id) {
        return repository.findTransaction(id);
    }
}