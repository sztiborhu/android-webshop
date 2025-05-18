package hu.sztibor.webshop.model;

import java.util.ArrayList;

public class Cart {
    private String userId;
    private ArrayList<CartItem> items;

    private int totalPrice;

    public Cart() { }

    public Cart(String userId, ArrayList<CartItem> items, int totalPrice) {
        this.userId = userId;
        this.items = items;
        this.totalPrice = totalPrice;
    }

    public String getUserId() {
        return userId;
    }

    public ArrayList<CartItem> getItems() {
        return items;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setItems(ArrayList<CartItem> items) {
        this.items = items;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
