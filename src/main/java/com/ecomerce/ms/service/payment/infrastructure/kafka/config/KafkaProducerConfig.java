package com.ecomerce.ms.service.payment.infrastructure.kafka.config;

import com.ecomerce.ms.service.OrderingSagaKey;
import com.ecomerce.ms.service.PaymentProcessingReply;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaProducerConfig {

    private final KafkaProperties kafkaProperties;

    @Bean
    public <K, V> ProducerFactory<K, V> producerFactory() {
        final Map<String, Object> configMap = kafkaProperties.buildProducerProperties();
        return new DefaultKafkaProducerFactory<>(configMap);
    }

    @Bean
    public KafkaTemplate<OrderingSagaKey, PaymentProcessingReply> customerVerificationReplyTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
