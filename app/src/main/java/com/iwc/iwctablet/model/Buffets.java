package com.iwc.iwctablet.model;

public class Buffets {

    private String buffet_name, buffet_price_dk, buffet_price_lk, buffet_required_menu, buffet_choice_menu, img_url, buffet_category;
    private String key;

    public Buffets() {
        // empty constructor required for firebase.
    }

    public Buffets(String buffet_name, String buffet_price_dk, String buffet_price_lk, String buffet_required_menu, String buffet_choice_menu, String img_url, String buffet_category) {
        this.buffet_name = buffet_name;
        this.buffet_price_dk = buffet_price_dk;
        this.buffet_price_lk = buffet_price_lk;
        this.buffet_required_menu = buffet_required_menu;
        this.buffet_choice_menu = buffet_choice_menu;
        this.buffet_category = buffet_category;
        this.img_url = img_url;
    }

    public String getBuffet_name() {
        return buffet_name;
    }
    public void setBuffet_name(String buffet_name) { this.buffet_name = buffet_name; }

    public String getBuffet_price_dk() {
        return buffet_price_dk;
    }
    public void setBuffet_price_dk(String buffet_price_dk) { this.buffet_price_dk = buffet_price_dk; }

    public String getBuffet_price_lk() {
        return buffet_price_lk;
    }
    public void setBuffet_price_lk(String buffet_price_lk) { this.buffet_price_lk = buffet_price_lk; }

    public String getBuffet_required_menu() {
        return buffet_required_menu;
    }
    public void setBuffet_required_menu(String buffet_required_menu) { this.buffet_required_menu = buffet_required_menu; }

    public String getBuffet_choice_menu() {
        return buffet_choice_menu;
    }
    public void setBuffet_choice_menu(String buffet_choice_menu) { this.buffet_choice_menu = buffet_choice_menu; }

    public String getImg_url() {
        return img_url;
    }
    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getBuffet_category() {
        return buffet_category;
    }
    public void setBuffet_category(String buffet_category) { this.buffet_category = buffet_category; }

    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
}
