package com.ecommerce.email;

import com.ecommerce.email.consumer.ConsumerSendEmail;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ServiceEmailApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceEmailApplication.class, args);

        ConsumerSendEmail.execute();
    }

}
