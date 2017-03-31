# kafkalens
This is a handy tool for Kafka. It has the following prominent features - 
* View all the messages in the Kafka cluster
* Search for a message - get the key, offset, partition, text.
* Publish a message into the topic
* Minimal configuration using a properties (.yml) file.

# Requirements
* Java 8
* Maven 3+
* Local MySql db - Create an admin user and a schema.

# How to use the tool

# Setup
## Run a local Kafka cluster
https://kafka.apache.org/documentation/#quickstart

## Run a local MySql server
https://dev.mysql.com/doc/mysql-getting-started/en/

Start MySql and create a new database called kafka

## Edit the config file
Edit **src/main/resources/config/application-dev.yml** file

## MySql DB
**Edit the following portion**
```
        url: jdbc:mysql://localhost:3306/kafka2?useUnicode=true&characterEncoding=utf8&useSSL=false
        username: kafka2
        password: kafka2
```
## Kafka Consumer Details
**Edit the following portion**
```
    kafkaConsumer:
        "[bootstrap.servers]": localhost:9092
        "[group.id]": test-1001
        "[enable.auto.commit]": false
        "[auto.commit.interval.ms]": 1000
        "[key.deserializer]": org.apache.kafka.common.serialization.StringDeserializer
        "[value.deserializer]": org.apache.kafka.common.serialization.StringDeserializer
        "[topic]": test
        "[partitions]": 1
        "[threads]": 1
```

## Kafka Producer Details
**Edit the following portion**
```
    kafkaProducer:
        "[bootstrap.servers]": localhost:9092
        "[acks]": 1
        "[retries]": 0
        "[batch.size]": 16384
        "[linger.ms]": 1
        "[buffer.memory]": 33554432
        "[key.serializer]": org.apache.kafka.common.serialization.StringSerializer
        "[value.serializer]": org.apache.kafka.common.serialization.StringSerializer
        "[topic]": test
```

---

## To Run the Tool
**Just run the default maven goal**

```
./mvnw
```
---

## Feedback/Suggestions
**amaresh.pat@gmail.com**

**Thank You!!**

___
