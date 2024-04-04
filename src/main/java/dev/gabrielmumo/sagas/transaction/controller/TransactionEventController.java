package dev.gabrielmumo.sagas.transaction.controller;

import dev.gabrielmumo.sagas.transaction.dto.TransactionEvent;
import dev.gabrielmumo.sagas.transaction.producer.TransactionEventsProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransactionEventController {
    private static final Logger log = LoggerFactory.getLogger(TransactionEventController.class);
    @Value("${spring.kafka.transaction-created-topic}")
    public String transactionCreatedTopic;

    private final TransactionEventsProducer transactionEventsProducer;

    public TransactionEventController(TransactionEventsProducer transactionEventsProducer) {
        this.transactionEventsProducer = transactionEventsProducer;
    }

    @PostMapping("/v1/transactions/create")
    public ResponseEntity<TransactionEvent> sendTransactionCreatedEvent(@RequestBody TransactionEvent transactionEvent) {
        log.info("Sending transaction event: {} ", transactionEvent);

        transactionEventsProducer.sendTransactionEvent(transactionEvent, transactionCreatedTopic);

        return ResponseEntity.status(HttpStatus.CREATED).body(transactionEvent);
    }
}
