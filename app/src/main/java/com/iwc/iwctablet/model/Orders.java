package com.iwc.iwctablet.model;

public class Orders {
    String
            key,
            created_at,
            event_date,
            id,
            total_price,
            total_item,
            time_start,
            time_end,
            event_location,
            number_of_guests,
            theme,
            carpet_heading,
            dp_1_amount,
            dp_2_amount,
            dp_3_amount,
            dp_4_amount,
            dp_1_date,
            dp_2_date,
            dp_3_date,
            dp_4_date,
            customer_service,
            order_note;

    public Orders() {
        // empty constructor required for firebase.
    }
    
    public String getKey() { return key; }
    
    public String getCustomer_service() { return customer_service; }
    
    public String getCreated_at() {
        return created_at;
    }
    
    public String getEvent_date() {
        return event_date;
    }
    
    public String getId() {
        return id;
    }
    
    public String getTotal_price() {
        return total_price;
    }
    
    public String getTotal_item() {
        return total_item;
    }
    
    public String getTime_start() {
        return time_start;
    }
    
    public String getTime_end() {
        return time_end;
    }
    
    public String getEvent_location() {
        return event_location;
    }
    
    public String getNumber_of_guests() {
        return number_of_guests;
    }
    
    public String getTheme() {
        return theme;
    }
    
    public String getCarpet_heading() {
        return carpet_heading;
    }
    
    public String getDp_1_amount() {
        return dp_1_amount;
    }
    
    public String getDp_2_amount() {
        return dp_2_amount;
    }
    
    public String getDp_3_amount() {
        return dp_3_amount;
    }

    public String getDp_4_amount() {
        return dp_4_amount;
    }
    
    public String getDp_1_date() {
        return dp_1_date;
    }
    
    public String getDp_2_date() {
        return dp_2_date;
    }
    
    public String getDp_3_date() { return dp_3_date; }

    public String getDp_4_date() { return dp_4_date; }

    public String getOrder_note() { return order_note; }

    public void setOrder_note(String order_note) { this.order_note = order_note; }
}
