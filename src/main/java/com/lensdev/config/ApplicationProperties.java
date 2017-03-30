package com.lensdev.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Properties specific to JHipster.
 *
 * <p>
 *     Properties are configured in the application.yml file.
 * </p>
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {
    private Map<String, String> kafkaConsumer = new LinkedHashMap();
    private Map<String, String> kafkaProducer = new LinkedHashMap();

    public Map<String, String> getKafkaConsumer() {
        return kafkaConsumer;
    }

    public Map<String, String> getKafkaProducer() {
        return kafkaProducer;
    }

}
