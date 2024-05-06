package com.trabmvc.homebroker.services;

import org.springframework.stereotype.Service;

import com.rabbitmq.client.*;
import com.trabmvc.homebroker.models.Stock;

@Service
public class BrokerService {

    private final static String QUEUE_NAME = "BROKER";
    private final static String EXCHANGE_NAME = "BOLSADEVALORES";
    private final static String UPDATE_ROUTING_KEY = "update.#";

    private void listenToUpdates(String code) throws Exception {
        Thread listenerThread = new Thread(() -> {
            try {
                ConnectionFactory factory = new ConnectionFactory();
                factory.setHost("localhost");
                factory.setUsername("admin");
                factory.setPassword("123456");
                factory.setVirtualHost("/");
                factory.setPort(5672);
                try (Connection connection = factory.newConnection();
                        Channel channel = connection.createChannel()) {

                    String routingKey = UPDATE_ROUTING_KEY + code;
                    channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
                    String queueName = channel.queueDeclare().getQueue();
                    channel.queueBind(queueName, EXCHANGE_NAME, routingKey);

                    DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                        String message = new String(delivery.getBody(), "UTF-8");
                        System.out.println(" [Broker] Update Received: '" + message + "'");
                    };
                    channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
                    });

                    // Keep listening for updates
                    while (!Thread.currentThread().isInterrupted()) {
                        Thread.sleep(1000);
                    }
                }
            } catch (Exception e) {
                System.out.println("Error in listener thread: " + e.getMessage());
            }
        });

        listenerThread.start();
    }

    private void sendBuyOrder(Stock stock) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("admin");
        factory.setPassword("123456");
        factory.setVirtualHost("/");
        factory.setPort(5672);
        try (Connection connection = factory.newConnection();
                Channel channel = connection.createChannel()) {
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            String buyOrder = stock.getOperation() + " " + stock.getQuantity() + " " + stock.getPrice() + " "
                    + stock.getCode();
            channel.basicPublish("", QUEUE_NAME, null, buyOrder.getBytes());
            System.out.println(" [Broker] Sent '" + buyOrder + "'");
        }
    }

    private void sendSellOrder(Stock stock) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("admin");
        factory.setPassword("123456");
        factory.setVirtualHost("/");
        factory.setPort(5672);
        try (Connection connection = factory.newConnection();
                Channel channel = connection.createChannel()) {
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            String sellOrder = stock.getOperation() + " " + stock.getQuantity() + " " + stock.getPrice() + " "
                    + stock.getCode();
            channel.basicPublish("", QUEUE_NAME, null, sellOrder.getBytes());
            System.out.println(" [Broker] Sent '" + sellOrder + "'");
        }
    }

    public void buyStock(Stock stock) {
        try {
            sendBuyOrder(stock);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sellStock(Stock stock) {
        try {
            sendSellOrder(stock);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void listen(String code) {
        try {
            listenToUpdates(code);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendStockInformation(String stockSymbol, String message) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("admin");
        factory.setPassword("123456");
        factory.setVirtualHost("/");
        factory.setPort(5672);
        try (Connection connection = factory.newConnection();
                Channel channel = connection.createChannel()) {
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            channel.basicPublish(EXCHANGE_NAME, "update." + stockSymbol, null, message.getBytes());
        }
    }
}
