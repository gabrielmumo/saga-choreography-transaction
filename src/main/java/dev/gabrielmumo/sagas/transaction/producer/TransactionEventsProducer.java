package dev.gabrielmumo.sagas.transaction.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.gabrielmumo.sagas.transaction.dto.TransactionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

@Component
public class TransactionEventsProducer {
    private static final Logger log = LoggerFactory.getLogger(TransactionEventsProducer.class);
    private final KafkaTemplate<Integer, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public TransactionEventsProducer(KafkaTemplate<Integer, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendTransactionEvent(TransactionEvent transactionEvent, String transactionTopic) {
        try {
            var key = transactionEvent.transactionId();
            var value = objectMapper.writeValueAsString(transactionEvent);
            var completableFuture = kafkaTemplate.send(transactionTopic, key, value);

            completableFuture.whenComplete((sendResult, throwable) -> {
                if(throwable != null) {
                    handleFailure(key, value, throwable);
                } else {
                    handleSuccess(key, value, sendResult);
                }
            });
        } catch (JsonProcessingException e) {
            log.error("Unable to map transaction event {}, error: {}", transactionEvent, e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error sending transaction event {}, error: {}", transactionEvent, e.getMessage());
        }
    }

    private void handleSuccess(Integer key, String value, SendResult<Integer, String> sendResult) {
        log.info("Transaction: {} was sent successfully. Key: {} & partition: {}",
                value,
                key,
                sendResult.getRecordMetadata().partition()
        );
    }

    private void handleFailure(Integer key, String value, Throwable throwable) {
        log.error("Error sending transaction: {} with key: {}", value, key, throwable);
    }
}
