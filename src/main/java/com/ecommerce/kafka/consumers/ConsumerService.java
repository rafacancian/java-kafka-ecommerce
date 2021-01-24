package com.ecommerce.kafka.consumers;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.regex.Pattern;

public class ConsumerService<T> {

    KafkaConsumer<String, T> kafkaConsumer;
    private ConsumerFunction consumerFactory;

    public ConsumerService(String className, ConsumerFunction parse) {
        kafkaConsumer = new KafkaConsumer<>(getProperties(className));
        consumerFactory = parse;
    }

    public ConsumerService(String topic, String className, ConsumerFunction parse) {
        this(className, parse);
        kafkaConsumer.subscribe(Collections.singletonList(topic));
    }

    public ConsumerService(Pattern topic, String className, ConsumerFunction parse) {
        this(className, parse);
        kafkaConsumer.subscribe(topic);
    }


    public void execute() {
        Thread thread = new Thread(() -> {
            while (true) {
                var records = kafkaConsumer.poll(Duration.ofSeconds(1));
                if (!records.isEmpty()) {
                    records.forEach(record -> consumerFactory.parse(record));
                }
            }
        });
        thread.start();
    }


    private static Properties getProperties(String className) {
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9091");
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, className);
        properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "1");
        return properties;
    }
}
