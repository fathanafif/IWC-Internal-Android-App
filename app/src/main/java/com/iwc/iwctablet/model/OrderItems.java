package com.iwc.iwctablet.model;

public class OrderItems {
    String name, type, category, key;
    int price, qty;
    
    public String getName() {
        return name;
    }
    
    public String getType() {
        return type;
    }

    public String getCategory() {
        return category;
    }
    
    public int getPrice() {
        return price;
    }
    
    public int getQty() {
        return qty;
    }
    
    public OrderItems() {
        // empty constructor required for firebase.
    }
    
    public String getKey() {
        return key;
    }
}
