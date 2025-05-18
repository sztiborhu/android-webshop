package hu.sztibor.webshop.model;

public class Product {
    private String id;
    private String name;
    private String description;
    private int price;
    private String image;

    public Product(String id, String name, String description, int price, String image) {
        this.id = id;
        this.description = description;
        this.image = image;
        this.name = name;
        this.price = price;
    }

    Product() {}

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }

    public int getPrice() {
        return price;
    }

    public String getImage() {
        return image;
    }


}
