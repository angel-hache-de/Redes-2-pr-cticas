/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import model.Product;
import model.ShippingCart;
import model.User;
import network.Client;
import network.PackageNotReceivedException;
import network.PackageNotSentException;
import session.Session;
import session.SessionShippingCart;

import java.io.IOException;
import java.util.Optional;

/**
 *
 * @author Alumno
 */
public class CartController {
    private SessionShippingCart cart;

    public CartController() {
        cart = SessionShippingCart.getInstance();
    }
    
    public ShippingCart getCart() {
        return cart.getShippingCart();
    }

    public void emptyCart() {
        cart.clearCart();
    }

    public void removeItem(Product product) {
        cart.removeItem(product, 1);
    }
    
    public void confirmTransaction() throws PackageNotSentException, PackageNotReceivedException, IOException {
        // TODO send the cart
        Optional<User> user = Session.getInstance().getUser();
        if(user.isEmpty()) return;

        int userId = user.get().getId();
        Client.sendTransactionRequest(cart.getShippingCart(), userId);
    }
}
