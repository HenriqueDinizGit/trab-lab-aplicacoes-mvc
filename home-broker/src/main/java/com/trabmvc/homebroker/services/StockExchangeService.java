package com.trabmvc.homebroker.services;

import java.util.HashMap;
import java.util.LinkedList;

import com.rabbitmq.client.*;
import com.trabmvc.homebroker.enums.Operation;
import com.trabmvc.homebroker.models.Stock;

import java.util.List;
import java.util.Map;

public class StockExchangeService {
    private final static String QUEUE_NAME = "BROKER";
    private final static String EXCHANGE_NAME = "BOLSADEVALORES";
    private final static String ROUTING_KEY = "order.*";

    private BrokerService broker = new BrokerService();
    private Map<String, List<Stock>> stockBook = new HashMap<>();

    public void listen() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("admin");
        factory.setPassword("123456");
        factory.setVirtualHost("/");
        factory.setPort(5672);
        try (Connection connection = factory.newConnection();
                Channel channel = connection.createChannel()) {

            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTING_KEY);

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                System.out.println(" [StockExchange] Received '" + message + "'");
                try {
                    processMessage(message, channel);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            };
            channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {
            });

            // ‘Loop’ para manter acordado
            while (true) {
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void processMessage(String message, Channel channel) throws Exception {
        String[] parts = message.split(" ");
        Operation operation = Operation.valueOf(parts[0]);
        int quantity = Integer.parseInt(parts[1]);
        double price = Double.parseDouble(parts[2]);
        String stockSymbol = parts[3];

        Stock stock = new Stock(stockSymbol, stockSymbol, price, quantity, operation);

        matchOrders(stock, channel);
    }

    private void matchOrders(Stock stock, Channel channel)
            throws Exception {

        List<Stock> orders = stockBook.getOrDefault(stock.getCode(), new LinkedList<>());

        if (stock.getOperation().equals(Operation.BUY.toString())) {
            for (Stock order : orders) {
                boolean isValidBuyOrder = stock.getPrice() >= order.getPrice()
                        && stock.getQuantity() == order.getQuantity();
                if (order.getOperation().toString().equals(Operation.SELL.toString()) && isValidBuyOrder) {
                    System.out.println(
                            " [StockExchange Transaction]: Compra realizada -> " + stock.getQuantity() + " ações "
                                    + stock.getCode() + " a R$" + stock.getPrice());
                    String updateMessage = "Transaction: Compra " + stock.getQuantity() + " ações " + stock.getCode()
                            + " a R$"
                            + stock.getPrice();
                    broker.sendStockInformation(stock.getCode(), updateMessage);
                    orders.remove(order);
                    return;
                }
            }

            orders.add(stock);

        } else if (stock.getOperation().equals(Operation.SELL.toString())) {
            for (Stock order : orders) {
                boolean isValidSellOrder = stock.getPrice() >= order.getPrice() && stock
                        .getQuantity() == order.getQuantity();
                if (order.getOperation().toString().equals(Operation.BUY.toString()) && isValidSellOrder) {
                    System.out.println(
                            " [StockExchange Transaction]: Venda realizada -> " + stock.getQuantity() + " ações "
                                    + stock.getCode() + " a R$" + stock.getPrice());
                    String updateMessage = "Transaction: Venda " + stock.getQuantity() + " ações " + stock.getCode()
                            + " a R$" + stock
                                    .getPrice();
                    broker.sendStockInformation(stock.getCode(), updateMessage);
                    orders.remove(order);
                    return;
                }
            }

            orders.add(stock);
        }
    }

}
