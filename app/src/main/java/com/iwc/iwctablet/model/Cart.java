package com.iwc.iwctablet.model;

public class Cart {

    private String name, category, type, img_url;
    private int price, qty;
    private String key;

    public Cart() {
        // empty constructor required for firebase.
    }

    public Cart(String name, String category, String type, String img_url, int price, int qty) {
        this.name = name;
        this.type = type;
        this.category = category;
        this.price = price;
        this.qty = qty;
        this.img_url = img_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCategory() {
        return category;
    }

    public String getType() {
        return type;
    }

    public int getPrice() {
        return price;
    }

    public int getQty() {
        return qty;
    }

    public String getKey() {
        return key;
    }

    public String getImg_url() { return img_url; }

    public void setImg_url(String img_url) { this.img_url = img_url; }
}
