package com.lensdev.kafka;

import com.lensdev.domain.Kafkaevent;
import com.lensdev.repository.KafkaeventRepository;
import com.lensdev.repository.search.KafkaeventSearchRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.utils.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * Created by apatta2 on 3/30/17.
 */
public class LensKafkaConsumer implements Runnable {
    Logger logger = LoggerFactory.getLogger(LensKafkaConsumer.class);

    KafkaConsumer<String, String> consumer;
    KafkaeventRepository repository;
    KafkaeventSearchRepository kafkaeventSearchRepository;
    private static ZoneId ZONE = ZoneId.systemDefault();

    public LensKafkaConsumer(Map<String, String> propMap, KafkaeventRepository repository, KafkaeventSearchRepository kafkaeventSearchRepository) {
        this.repository = repository;
        this.kafkaeventSearchRepository = kafkaeventSearchRepository;
        String topic = propMap.get("topic");

        Properties properties = new Properties();
        properties.putAll(propMap);
        consumer = new KafkaConsumer<>(properties);

        //consumer.subscribe(Arrays.asList(topic));
        int startPartition = Integer.parseInt(propMap.get("startPartition"));
        int endPartition = Integer.parseInt(propMap.get("endPartition"));
        List<TopicPartition> topicPartitions = new ArrayList<>();
        for(int i = startPartition; i <= endPartition; i++) {
            TopicPartition topicPartition = new TopicPartition(topic, i);
            topicPartitions.add(topicPartition);
        }
        consumer.assign(topicPartitions);
        consumer.seekToBeginning(topicPartitions);
    }

    public void run() {
        while (true) {
            try {
                ConsumerRecords<String, String> records = consumer.poll(1000);
                //Thread.sleep(1000L);
                if(records.count() > 0) {
                    for (ConsumerRecord<String, String> record : records) {
                        logger.info("Received message: key: {}, value: {}", record.key(), record.value());
                        List<Kafkaevent> kafkaeventList = repository.findByKafkakey(record.key());
                        if(kafkaeventList != null && kafkaeventList.size() > 0) {
                            continue;
                        }
                        Kafkaevent kafkaevent = new Kafkaevent();
                        kafkaevent.setKafkakey(record.key());
                        kafkaevent.setKpartition(record.partition());
                        kafkaevent.setKoffset(record.offset());
                        kafkaevent.setBody(record.value());
                        Instant instant = Instant.ofEpochMilli(record.timestamp());
                        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant( instant, ZONE );
                        kafkaevent.setEventtime(zonedDateTime);
                        this.repository.save(kafkaevent);
                        this.kafkaeventSearchRepository.save(kafkaevent);
                    }
                }
            } catch (Exception e) {
                logger.error("Error consuming message", e);
            }
        }
    }
}
