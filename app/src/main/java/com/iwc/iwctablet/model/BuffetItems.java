package com.iwc.iwctablet.model;

public class BuffetItems {

    private String item_name, item_price_dk, item_price_lk, img_url, item_type, key;

    public BuffetItems() {
        // empty constructor required for firebase.
    }

    public BuffetItems(String item_name, String item_price_dk, String item_price_lk, String img_url, String item_type) {
        this.item_name = item_name;
        this.item_price_dk = item_price_dk;
        this.item_price_lk = item_price_lk;
        this.img_url = img_url;
        this.item_type = item_type;
    }

    public String getItem_name() {
        return item_name;
    }
    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public String getItem_price_dk() {
        return item_price_dk;
    }
    public void setItem_price_dk(String item_price_dk) {
        this.item_price_dk = item_price_dk;
    }

    public String getItem_price_lk() {
        return item_price_lk;
    }
    public void setItem_price_lk(String item_price_lk) {
        this.item_price_lk = item_price_lk;
    }

    public String getItem_type() {
        return item_type;
    }
    public void setItem_type(String item_type) {
        this.item_type = item_type;
    }
    
    public String getImg_url() { return img_url; }
    public void setImg_url(String img_url) { this.img_url = img_url; }

    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
}
