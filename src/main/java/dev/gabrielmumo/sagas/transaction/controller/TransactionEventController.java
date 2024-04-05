package dev.gabrielmumo.sagas.transaction.controller;

import dev.gabrielmumo.sagas.transaction.dto.TransactionEvent;
import dev.gabrielmumo.sagas.transaction.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransactionEventController {
    private static final Logger log = LoggerFactory.getLogger(TransactionEventController.class);

    private final TransactionService transactionService;

    public TransactionEventController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/v1/transactions/create")
    public ResponseEntity<TransactionEvent> sendTransactionCreatedEvent(@RequestBody TransactionEvent transactionEvent) {
        log.info("Sending transaction event: {} ", transactionEvent);

        transactionService.insertTransaction(transactionEvent);

        return ResponseEntity.status(HttpStatus.CREATED).body(transactionEvent);
    }

    @GetMapping("/v1/transactions/{id}")
    public ResponseEntity<TransactionEvent> getTransaction(@PathVariable Integer id) {
        var transaction = transactionService.findTransactionEvent(id);
        return transaction.map(transactionEvent -> ResponseEntity.status(HttpStatus.OK).body(transactionEvent))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
