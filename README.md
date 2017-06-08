# kafkalens
This is a handy tool for Kafka. It has the following prominent features - 
* View all the messages in the Kafka cluster
* Search for a message - get the key, offset, partition, text.
* Publish a message into the topic
* Minimal configuration using a properties (.yml) file.

# Requirements
* Java 8
* Maven 3+

# How to use the tool

# Setup
## Run a local Kafka cluster or point to an existing cluster
### Refer to this document for creating a local cluster
https://kafka.apache.org/documentation/#quickstart


---

## To Run the Tool
**Just run the default maven goal**

```
Option 1
-------------------
The easiest way is to open the project in an IDE
Run the main class - KafkalensApp
use the following vm options (customize based on your needs)
-Dbootstrap.servers=dal-kafka-broker00-cp.prod.walmart.com:9092,dal-kafka-broker01-cp.prod.walmart.com:9092,dal-kafka-broker02-cp.prod.walmart.com:9092  -Dtopic=limo_offer_ingestion_prod  -Dpartitions=10  -Dthreads=1  -Dmax.count=10
Run 
KafkalensApp

Option 2
--------------------------
Do this from command line.
go the directory where you cloned the project

./mvnw -DskipTests -Dbootstrap.servers=dal-kafka-broker00-cp.prod.walmart.com:9092,dal-kafka-broker01-cp.prod.walmart.com:9092,dal-kafka-broker02-cp.prod.walmart.com:9092  -Dtopic=limo_offer_ingestion_prod  -Dpartitions=10  -Dthreads=1  -Dmax.count=10

Adjust the parameters based on your requirements

Option 3
----------------------------------
OR you can also run the war file.
clone the repo.
cd <kafka-lens dir>

-- Generate the runnable war
mvn -DskipTests clean install

JAVA_OPTS="-Dbootstrap.servers=dal-kafka-broker00-cp.prod.walmart.com:9092,dal-kafka-broker01-cp.prod.walmart.com:9092,dal-kafka-broker02-cp.prod.walmart.com:9092  -Dtopic=limo_offer_ingestion_prod  -Dpartitions=10  -Dthreads=1  -Dmax.count=10"
./target/kafka-lens-0.0.1-SNAPSHOT.war

unset JAVA_OPTS

```
---

## Feedback/Suggestions
**amaresh.pat@gmail.com**

**Thank You!!**

___
