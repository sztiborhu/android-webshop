package hu.sztibor.webshop.model;

public class Product {
    private String name;
    private String description;
    private int price;
    private int image;

    public Product(String name, String description, int price, int image) {
        this.description = description;
        this.image = image;
        this.name = name;
        this.price = price;
    }

    Product() {}
    public String getDescription() {
        return description;
    }

    public int getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }
}
