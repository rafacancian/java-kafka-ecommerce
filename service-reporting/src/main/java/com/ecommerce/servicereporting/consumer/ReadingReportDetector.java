package com.ecommerce.servicereporting.consumer;

import com.ecommerce.consumer.ConsumerService;
import com.ecommerce.producer.ProducerService;
import com.ecommerce.servicereporting.models.User;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Map;

public class ReadingReportDetector {

    private ProducerService<String> producerService = new ProducerService<>();

    final static Path SOURCE = new File("src/main/resources/report.txt").toPath();

    public void execute() {
        ConsumerService consumerService = new ConsumerService("USER_GENERATE_REPORTS",
                ReadingReportDetector.class.getSimpleName(), this::parse, User.class, Map.of());
        consumerService.execute();
    }

    public void parse(final ConsumerRecord<String, User> record) {
        System.out.println(">> Processing report for");
        System.out.println("Key: " + record.key() + " | Value: " + record.value());

        User user = record.value();
        var target = new File(user.getReportPath());
        IO.copyTo(SOURCE, target);
        IO.append(target, "Created for " + user.getFullName() + " in " + LocalDate.now());
        IO.append(target, "=====================================================");

        System.out.println("File created:" + target.getAbsolutePath());
    }


}
