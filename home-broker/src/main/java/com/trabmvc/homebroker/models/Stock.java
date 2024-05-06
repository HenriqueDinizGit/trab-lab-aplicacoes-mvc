package com.trabmvc.homebroker.models;

import com.trabmvc.homebroker.enums.Operation;

public class Stock {
    private String name;
    private String code;
    private double price;
    private int quantity;
    private Operation operation;

    public Stock(String name, String code, double price, int quantity, Operation operation) {
        this.name = name;
        this.code = code;
        this.price = price;
        this.quantity = quantity;
        this.operation = operation;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

}
