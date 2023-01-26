package model;

import java.io.Serializable;

/**
 * Product (info)  sent from client to server
 * to register a transaction
 */
public class NetworkLineItem implements Serializable {
    private int productId;
    private int quantity;
    private float singleProductPrice;

    public NetworkLineItem(LineItem li) {
        this.productId = li.getProduct().getId();
        this.quantity = li.getQuantity();
        this.singleProductPrice = li.getProduct().getPrice();
    }

    public int getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public float getSingleProductPrice() {
        return singleProductPrice;
    }

    public void setSingleProductPrice(float singleProductPrice) {
        this.singleProductPrice = singleProductPrice;
    }

    @Override
    public String toString() {
        return "Product: " + productId
                + "\nquantity: " + quantity
                + "\nprice: " + singleProductPrice;
    }
}
