package com.lensdev.config;

import com.lensdev.kafka.LensKafkaConsumer;
import com.lensdev.kafka.LensKafkaProducer;
import com.lensdev.repository.KafkaeventRepository;
import com.lensdev.repository.search.KafkaeventSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Created by apatta2 on 3/30/17.
 */


@Configuration
@AutoConfigureBefore(value = { MetricsConfiguration.class, WebConfigurer.class, DatabaseConfiguration.class })
public class KafkaConfiguration {
    private final Logger log = LoggerFactory.getLogger(KafkaConfiguration.class);

    @Autowired
    KafkaeventRepository kafkaeventRepository;

    @Autowired
    KafkaeventSearchRepository kafkaeventSearchRepository;

    Map<String, String> kafkaConsumerPropsMap;
    Map<String, String> kafkaProducerPropsMap;

    public KafkaConfiguration(ApplicationProperties applicationProperties) {
        kafkaConsumerPropsMap = applicationProperties.getKafkaConsumer();
        kafkaProducerPropsMap = applicationProperties.getKafkaProducer();
/*
        for(String key : kafkaConsumerPropsMap.keySet()) {
            log.info("key: " + key + ", value: " + kafkaConsumerPropsMap.get(key));
        }
*/

        if(System.getProperty("bootstrap.servers") != null) {
            String bootstrapServers = System.getProperty("bootstrap.servers");
            kafkaConsumerPropsMap.put("bootstrap.servers", bootstrapServers);
        }
        if(System.getProperty("topic") != null) {
            String topic = System.getProperty("topic");
            kafkaConsumerPropsMap.put("topic", topic);
        }
        if(System.getProperty("partitions") != null) {
            String partitions = System.getProperty("partitions");
            kafkaConsumerPropsMap.put("partitions", partitions);
        }
        if(System.getProperty("threads") != null) {
            String threads = System.getProperty("threads");
            kafkaConsumerPropsMap.put("threads", threads);
        }
        if(System.getProperty("max.count") != null) {
            String maxCount = System.getProperty("max.count");
            kafkaConsumerPropsMap.put("max.count", maxCount);
        }
        if(System.getProperty("filter") != null) {
            String filter = System.getProperty("filter");
            kafkaConsumerPropsMap.put("filter", filter);
        }

    }

    @Bean
    LensKafkaConsumer lensKafkaConsumer() {
        LensKafkaConsumer lensKafkaConsumer = null;
        int partitions = Integer.parseInt(kafkaConsumerPropsMap.get("partitions"));
        int threads = Integer.parseInt(kafkaConsumerPropsMap.get("threads"));
        if(threads > partitions) {
            threads = partitions;
        }
        Double partitionsPerThreadDouble = ((double) partitions) / ((double) threads);
        partitionsPerThreadDouble = Math.ceil(partitionsPerThreadDouble);
        int partitionsPerThreadInt = partitionsPerThreadDouble.intValue();
        int startPartition = 0, endPartition = 0;

        for(int i = 0; i < threads; i++) {
            startPartition = (i * partitionsPerThreadInt);
            endPartition = (i * partitionsPerThreadInt) + partitionsPerThreadInt - 1;
            if(endPartition > (partitions-1)) {
                endPartition = partitions - 1;
            }
            kafkaConsumerPropsMap.put("startPartition", String.valueOf(startPartition));
            kafkaConsumerPropsMap.put("endPartition", String.valueOf(endPartition));

            lensKafkaConsumer = new LensKafkaConsumer(kafkaConsumerPropsMap, kafkaeventRepository, kafkaeventSearchRepository);
            Thread thread = new Thread(lensKafkaConsumer);
            thread.start();
        }
        return lensKafkaConsumer;
    }

    @Bean
    LensKafkaProducer lensKafkaProducer() {
        //LensKafkaProducer lensKafkaProducer = new LensKafkaProducer(kafkaProducerPropsMap);
        LensKafkaProducer lensKafkaProducer = new LensKafkaProducer();
        return lensKafkaProducer;
    }

}
