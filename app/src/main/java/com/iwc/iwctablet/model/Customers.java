package com.iwc.iwctablet.model;

public class Customers {
    String name, phone, email, address, city, customer_id, created_at;
    private String key;

    public void setName(String name) { this.name = name; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public void setCustomer_id(String customer_id) { this.customer_id = customer_id; }
    public void setKey(String key) {
        this.key = key;
    }

    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getEmail() {
        return email;
    }
    public String getAddress() {
        return address;
    }
    public String getCity() {
        return city;
    }
    public String getCreated_at() {
        return created_at;
    }
    public String getCustomer_id() {return customer_id; }
    public String getKey() {
        return key;
    }
}
