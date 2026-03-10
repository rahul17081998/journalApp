package com.rahul.journal_app.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import com.rahul.journal_app.model.SentimentalData;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;
@Slf4j
@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    @Value("${kafka.config.userName}")
    private String kafkaUserName;
    @Value("${kafka.config.password}")
    private String kafkaPassword;

    @Bean
    public ProducerFactory<String, SentimentalData> producerFactory() {
//        log.info("--------Kafka JAAS config initialized");
        log.info("--------bootstrap.server: {}", bootstrapServers);
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        // Confluent Cloud Security
        configProps.put("security.protocol", "SASL_SSL");
        configProps.put("sasl.mechanism", "PLAIN");
        configProps.put("sasl.jaas.config", getKafkaString());
//        configProps.put("sasl.jaas.config", "org.apache.kafka.common.security.plain.PlainLoginModule required username='NMYQ225BRYPTZOTL' password='cfltL/tIQqJGpGBzXoEDjv76pgudtXQSn787DzOdic2hX7rfiPg5MBQdUbRlW7XA';");
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    private String getKafkaString(){
        // String configString="org.apache.kafka.common.security.plain.PlainLoginModule required username='"+kafkaUserName+"' password='"+kafkaPassword+"';"
        String configString="org.apache.kafka.common.security.plain.PlainLoginModule required username='KAFKA_USER_NAME' password='KAFKA_PASSWORD';";
        configString= configString
                .replace("KAFKA_USER_NAME", kafkaUserName)
                .replace("KAFKA_PASSWORD", kafkaPassword);
        return configString;
    }

    @Bean
    public KafkaTemplate<String, SentimentalData> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
