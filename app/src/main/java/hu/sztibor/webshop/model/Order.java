package hu.sztibor.webshop.model;

import com.google.firebase.Timestamp;
import java.util.ArrayList;

public class Order {
    private String id;
    private String userId;
    private String deliveryAddress;
    private String paymentMethod;
    private ArrayList<CartItem> items;
    private int totalPrice;
    private Timestamp orderDate;
    private String status;

    public Order() {
    }

    public Order(String id, String userId, String deliveryAddress, String paymentMethod, ArrayList<CartItem> items, int totalPrice) {
        this.id = id;
        this.userId = userId;
        this.deliveryAddress = deliveryAddress;
        this.paymentMethod = paymentMethod;
        this.items = items;
        this.totalPrice = totalPrice;
        this.orderDate = Timestamp.now();
        this.status = "Feldolgozás alatt";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public ArrayList<CartItem> getItems() {
        return items;
    }

    public void setItems(ArrayList<CartItem> items) {
        this.items = items;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Timestamp getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Timestamp orderDate) {
        this.orderDate = orderDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
