package com.RestaurantManagementSystemSend.RMSS;

import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
public class Controller {

    RabbitServiceSendOrder rabbitService = new RabbitServiceSendOrder();
    RabbitServiceChangeStatus rabbitServiceChangeStatus = new RabbitServiceChangeStatus();
    RabbitServicePayment rabbitServicePayment = new RabbitServicePayment();
    MongoDBService mongoDBService = new MongoDBService();
    RedisService redisService = new RedisService("localhost", 6379);

    @CrossOrigin
    @PostMapping("/add")
    public void addOrder(@RequestBody Order order) throws Exception {
        order.setOrderTime(LocalDateTime.now());
        order.setStatus("not ready");
        order.setOrderId(mongoDBService.createOrderId());
        rabbitService.sendObjectToQueue(order);
    }

    @CrossOrigin
    @PostMapping("/status")
    public void changeStatus(String orderId, String status) throws Exception {
        rabbitServiceChangeStatus.changeStatus(orderId, status);
    }

    @CrossOrigin
    @GetMapping("/getAll")
    public List<Order> getAllOrders(){
        return mongoDBService.getAll();
    }

    @CrossOrigin
    @GetMapping("/getReady")
    public List<Order> getAllOrdersReady(){
        return mongoDBService.getAll();
    }

    @CrossOrigin
    @GetMapping("/getById")
    public Order getById(String id) throws IOException, ClassNotFoundException {
        Order order = redisService.get(id);

        if(order == null){
            return mongoDBService.getOrderById(id);
        }
        return redisService.get(id);
    }

    @PostMapping("/orders/{orderId}/payment")
    public String acceptPayment(@PathVariable String orderId, String paymentMethod, double totalPrice) throws Exception {
        rabbitServicePayment.acceptPayment(orderId, paymentMethod, totalPrice);
        return "Payment successfull!";
    }


}
