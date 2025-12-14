package org.example.angelsburgerpos.model;

import org.example.angelsburgerpos.util.OrderRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

public class MobileOrder {

    private String orderId;
    private String customerName;
    private List<OrderRequest> items;
    private String timestamp;
    private String status;

    public MobileOrder(String customerName, List<OrderRequest> items){
        this.orderId = UUID.randomUUID().toString().substring(0, 8);
        this.customerName = customerName;
        this.items = items;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        this.status = "PENDING";


    }
    public int getItemCount(){
        return items.stream().mapToInt(item -> item.quantity).sum();
    }
    public String getOrderId() {
        return orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public List<OrderRequest> getItems() {
        return items;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getStatus() {
        return status;
    }
}
