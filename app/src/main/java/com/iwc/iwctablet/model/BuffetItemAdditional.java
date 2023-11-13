package com.iwc.iwctablet.model;

public class BuffetItemAdditional {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    private String name, category, type;
    private int price, qty, sequence;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private String key;


    public BuffetItemAdditional() {
        // empty constructor required for firebase.
    }

    public BuffetItemAdditional(String name, String category, String type, int price, int qty, int sequence) {
        this.name = name;
        this.category = category;
        this.type = type;
        this.price = price;
        this.sequence = sequence;
        this.qty = qty;
    }
}
