package com.RestaurantManagementSystemSend.RMSS;

import com.fasterxml.jackson.databind.ObjectMapper;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.*;
import java.util.List;

public class RedisService {
    private final JedisPool jedisPool;
    private ObjectMapper objectMapper = new ObjectMapper();

    public RedisService(String host, int port) {

        this.jedisPool = new JedisPool(host, port);
        objectMapper.findAndRegisterModules();
    }

    public Order get(String key) throws IOException, ClassNotFoundException {
        try (Jedis jedis = jedisPool.getResource()) {
            byte[] data = jedis.get(key.getBytes());
            if (data != null) {
                return deserialize(data);
            }
            return null;
        }
    }

    private Order deserialize(byte[] data) throws IOException, ClassNotFoundException {

        Order obj = objectMapper.readValue(data, Order.class);
        return obj;
    }

    public void close() {
        jedisPool.close();
    }
}

