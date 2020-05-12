package com.example.shoppinglistapplication.Model;

public class Data {
    String name;
    int price;
    int qty;
    String type;
    String id;
    String date;

    public Data(){
    }

    public Data(String name, int price, int qty, String type, String id, String date) {
        this.name = name;
        this.price = price;
        this.qty = qty;
        this.type = type;
        this.id = id;
        this.date = date;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
