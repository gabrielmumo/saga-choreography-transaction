package dev.gabrielmumo.sagas.transaction.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.gabrielmumo.sagas.transaction.dto.TransactionEvent;
import dev.gabrielmumo.sagas.transaction.service.TransactionService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionEventsConsumer {
    private static final Logger log = LoggerFactory.getLogger(TransactionEventsConsumer.class);

    private final TransactionService transactionService;
    private final ObjectMapper objectMapper;

    public TransactionEventsConsumer(TransactionService transactionService, ObjectMapper objectMapper) {
        this.transactionService = transactionService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = {"${spring.kafka.transaction-rejected-topic}"})
    public void onTransactionRejectedEvent(ConsumerRecord<Integer, String> consumerRecord) {
        log.info("Reconciliation listener started. Transaction: {}", consumerRecord.key());
        handleEvent(consumerRecord);
    }

    @KafkaListener(topics = {"${spring.kafka.transaction-completed-topic}"})
    public void onTransactionCompletedEvent(ConsumerRecord<Integer, String> consumerRecord) {
        log.info("Confirmation listener started. Transaction: {}", consumerRecord.key());
        handleEvent(consumerRecord);
    }

    private void handleEvent(ConsumerRecord<Integer, String> consumerRecord) {
        try {
            TransactionEvent transactionEvent = objectMapper.readValue(consumerRecord.value(), TransactionEvent.class);
            transactionService.updateTransaction(transactionEvent);
        } catch (JsonProcessingException e) {
            log.error("Unable to map event: ", e);
        }
    }
}
