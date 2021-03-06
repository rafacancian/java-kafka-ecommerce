package com.ecommerce.applicationmanager.adapters;


import com.ecommerce.model.Order;
import com.ecommerce.model.OrderStatus;
import com.ecommerce.utils.GenerateRandomNumber;

import java.time.LocalDate;
import java.util.UUID;

public class OrderAdapter {

    public static Order createMock() {
        final String id = UUID.randomUUID().toString().substring(0, 15);
        return Order.builder()
                .code("order-" + id)
                .description("Mock order with the description order-" + id)
                .orderStatus(OrderStatus.CREATED)
                .creationDate(LocalDate.now())
                .price(GenerateRandomNumber.execute(50, 400))
                .email("mock." + id + "@gmail.com")
                .build();
    }
}
