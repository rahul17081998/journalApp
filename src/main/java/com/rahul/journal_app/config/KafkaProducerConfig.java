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

    private static final String SASL_MECHANISM = "sasl.mechanism";
    private static final String SASL_JAAS_CONFIG = "sasl.jaas.config";
    private static final String SECURITY_PROTOCOL = "security.protocol";

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    @Value("${kafka.config.userName}")
    private String kafkaUserName;
    @Value("${kafka.config.password}")
    private String kafkaPassword;

    @Bean
    public ProducerFactory<String, SentimentalData> producerFactory() {
        log.info("--------bootstrap.server: {}", bootstrapServers);
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(SECURITY_PROTOCOL, "SASL_SSL");
        configProps.put(SASL_MECHANISM, "PLAIN");
        configProps.put(SASL_JAAS_CONFIG, buildKafkaJaasConfig());
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

    private String buildKafkaJaasConfig() {
        return String.format(
                "org.apache.kafka.common.security.plain.PlainLoginModule required username='%s' password='%s';",
                kafkaUserName, kafkaPassword
        );
    }

    @Bean
    public KafkaTemplate<String, SentimentalData> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
