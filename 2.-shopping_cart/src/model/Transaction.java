package model;

import java.io.Serializable;

public class Transaction implements Serializable {
    private final User user;
    private final ShippingCart cart;
    private final String date;
    private final float total;
//    private final PaymentIntf payment;

//    public Order(Customer customer, ShoppingCart cart, PaymentIntf payment) {
//        this.customer = customer;
//        this.cart = cart;
//        this.payment = payment;
//    }
    public Transaction(ShippingCart cart, User user, String date, float total) {
        this.cart = cart;
        this.user = user;
        this.date = date;
        this.total = total;
    }

    public ShippingCart getCart() {
        return cart;
    }

    public User getUser() { return user; }

    public String getDate() { return date; }

    public float getTotal() { return total; }
//    @Override
//    public String toString() {
//        return "Order{" +
//                "\n customer=" + customer +
//                ",\n cart=" + cart +
//                ",\n payment=" + payment +
//                "\n}";
//    }
}
