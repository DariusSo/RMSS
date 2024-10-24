package com.RestaurantManagementSystemSend.RMSS;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MongoDBService {
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> collection;

    public MongoDBService() {
        // Connect to MongoDB (default localhost:27017)
        mongoClient = MongoClients.create("mongodb://localhost:27017");
        database = mongoClient.getDatabase("OrderDB");
        collection = database.getCollection("orders");
    }

    private Order documentToOrder(Document doc) {
        Order order = new Order();
        order.setOrderId(doc.getString("orderId"));
        order.setClient(doc.getString("client"));
        order.setTable(doc.getInteger("table"));
        order.setStatus(doc.getString("status"));
        order.setDishes(doc.getList("dishes", String.class));
        order.setOrderTime(convertToLocalDateTimeViaInstant(doc.getDate("orderTime")));
        order.setProccessingTime(convertToLocalDateTimeViaInstant(doc.getDate("proccessingTime")));
        //System.out.println("Rado: " + order.getOrderId());
        return order;
    }

    public Order getOrderById(String id) {
        Document doc = collection.find(Filters.eq("orderId", id)).first();
        return doc != null ? documentToOrder(doc) : null;
    }

    public List<Order> getAll(){
        FindIterable<Document> doc = collection.find();
        List<Order> orderList = new ArrayList<>();
        for(Document d : doc){
            Order order = documentToOrder(d);
            if(!order.getStatus().equals("ready")){
                orderList.add(order);
            }
            //System.out.println("Found all: " + order.getOrderId());
        }
        return orderList;
    }

    public void close() {
        mongoClient.close();
    }
    public LocalDateTime convertToLocalDateTimeViaInstant(Date dateToConvert) {
        if(dateToConvert == null){
            return null;
        }
        return dateToConvert.toInstant()
                .atZone(ZoneId.of("Europe/Vilnius"))
                .toLocalDateTime();
    }
}

