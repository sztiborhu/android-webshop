package hu.sztibor.webshop.model;

public class User {
    private String id;
    private String name;
    private String email;
    private String phone;
    private String deliveryAddress;

    User() {}

    public User(String id, String name, String email, String phone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.deliveryAddress = "";
    }

    public String getEmail() {
        return email;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }
}
