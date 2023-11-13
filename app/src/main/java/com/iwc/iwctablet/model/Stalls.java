package com.iwc.iwctablet.model;

public class Stalls {

    private String name, price_dk, price_lk, category, type, img_url;
    private String key;

    public Stalls() {
        // empty constructor required for firebase.
    }

    public Stalls(String name, String price_dk, String price_lk, String category, String img_url) {
        this.name = name;
        this.price_dk = price_dk;
        this.price_lk = price_lk;
        this.category = category;
        this.img_url = img_url;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getPrice_dk() { return price_dk; }
    public void setPrice_dk(String price_dk) { this.price_dk = price_dk; }

    public String getPrice_lk() { return price_lk; }
    public void setPrice_lk(String price_lk) { this.price_lk = price_lk; }

    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }

    public String getImg_url() {
        return img_url;
    }
    public void setImg_url(String img_url) { this.img_url = img_url; }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
}
