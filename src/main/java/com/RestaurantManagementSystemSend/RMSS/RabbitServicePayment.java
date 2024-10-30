package com.RestaurantManagementSystemSend.RMSS;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.HashMap;
import java.util.Map;

public class RabbitServicePayment {
    private static final String QUEUE_NAME = "Payment_queue";
    private static final String HOST = "localhost";
    private final ConnectionFactory factory;
    private final ObjectMapper objectMapper;

    public RabbitServicePayment() {
        this.factory = new ConnectionFactory();
        factory.setHost(HOST);
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");
        this.objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    public void acceptPayment(String orderId, String paymentMethod, double totalPrice) throws Exception {
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            Map<String, String> info = new HashMap<>();
            info.put("orderId", orderId);
            info.put("paymentMethod", paymentMethod);
            info.put("totalPrice", String.valueOf(totalPrice));

            String jsonMessage = objectMapper.writeValueAsString(info);

            channel.basicPublish("", QUEUE_NAME, null, jsonMessage.getBytes());
        }
    }
}
