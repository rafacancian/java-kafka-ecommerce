package com.ecommerce.consumer;

import com.ecommerce.gson.MessageDeserializer;
import com.ecommerce.model.Message;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

public class ConsumerService<T> {

    private KafkaConsumer<String, Message<T>> kafkaConsumer;
    private ConsumerFunction consumerFactory;

    private ConsumerService(final String className, final ConsumerFunction parse, final Map<String, String> propertiesOverride) {
        kafkaConsumer = new KafkaConsumer<>(getProperties(className, propertiesOverride));
        consumerFactory = parse;
    }

    public ConsumerService(final String topic, final String className, final ConsumerFunction parse, final Map<String, String> propertiesOverride) {
        this(className, parse, propertiesOverride);
        kafkaConsumer.subscribe(Collections.singletonList(topic));
    }

    public ConsumerService(final Pattern topic, final String className, final ConsumerFunction parse, final Map<String, String> propertiesOverride) {
        this(className, parse, propertiesOverride);
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

    private Properties getProperties(final String className, final Map<String, String> propertiesOverride) {
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9091");
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, MessageDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, className);
        properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "1");
        properties.putAll(propertiesOverride);
        return properties;
    }
}
