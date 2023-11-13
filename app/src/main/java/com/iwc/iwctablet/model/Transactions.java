package com.iwc.iwctablet.model;

public class Transactions {
    String name, img_url, date_saved;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Transactions() {
        // empty constructor required for firebase.
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getDate_saved() {
        return date_saved;
    }

    public void setDate_saved(String date_saved) {
        this.date_saved = date_saved;
    }
}
