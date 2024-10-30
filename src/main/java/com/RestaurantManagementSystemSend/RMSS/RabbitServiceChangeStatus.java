package com.RestaurantManagementSystemSend.RMSS;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class RabbitServiceChangeStatus {
    private static final String QUEUE_NAME = "Status_queue";
    private static final String HOST = "localhost";
    private final ConnectionFactory factory;
    private final ObjectMapper objectMapper;

    public RabbitServiceChangeStatus() {
        this.factory = new ConnectionFactory();
        factory.setHost(HOST);
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");
        this.objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    public void changeStatus(String orderId, String status) throws Exception {
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            Order order = new Order();
            order.setOrderId(orderId);
            order.setStatus(status);

            String jsonMessage = objectMapper.writeValueAsString(order);

            channel.basicPublish("", QUEUE_NAME, null, jsonMessage.getBytes());
        }
    }
}
