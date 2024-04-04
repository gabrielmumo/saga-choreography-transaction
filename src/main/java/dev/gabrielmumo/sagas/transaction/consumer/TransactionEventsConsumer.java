package dev.gabrielmumo.sagas.transaction.consumer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TransactionEventsConsumer {

    @Value("${spring.kafka.transaction-completed-topic}")
    public String transactionCompletedTopic;

    @Value("${spring.kafka.transaction-rejected-topic}")
    public String transactionRejectedTopic;
}
