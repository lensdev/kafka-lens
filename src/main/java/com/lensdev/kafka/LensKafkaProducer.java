package com.lensdev.kafka;

import com.lensdev.domain.Kafkaevent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.utils.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * Created by apatta2 on 3/30/17.
 */
public class LensKafkaProducer {
    Logger logger = LoggerFactory.getLogger(LensKafkaProducer.class);

    Producer<String, String> producer;
    Properties kProperties;
    String topic;

    public LensKafkaProducer(Map<String, String> propMap) {

        topic = propMap.get("topic");
        Properties properties = new Properties();
        properties.putAll(propMap);
        kProperties = properties;
        producer = new KafkaProducer<String, String>(properties);
    }

    public LensKafkaProducer() {}

    public Kafkaevent publish(Kafkaevent kafkaevent) {
        try {
            producer.send(new ProducerRecord<String, String>(topic, kafkaevent.getKafkakey(), kafkaevent.getBody()) ).get();
        } catch (InterruptedException e) {
            logger.error("Error producing message", e);
        } catch (ExecutionException e) {
            logger.error("Error producing message", e);
        }
        producer.flush();
        return kafkaevent;
    }

    public void publish(String userTopic, String key, String message) {
        producer.send(new ProducerRecord<String, String>(userTopic, key, message) );
        producer.flush();
    }

    public static int randInt(int min, int max) {

        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

}
