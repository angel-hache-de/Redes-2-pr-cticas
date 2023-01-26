/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import model.LineItem;
import model.Product;
import network.Client;
import network.PackageNotReceivedException;
import network.PackageNotSentException;
import session.Session;
import session.SessionShippingCart;

/**
 *
 * @author Alumno
 */
public class SongsController {
    private SessionShippingCart cart;
    
    public SongsController() {
        cart = SessionShippingCart.getInstance();
    }
    
    public List<Product> getSongs() throws IOException, PackageNotSentException, PackageNotReceivedException {
        List<Product> songs = Client.sendGetSongsRequest();
        return songs;
    }

    public void addToCart(Product song) {
        LineItem item = new LineItem(song, 1);
        cart.addToCart(item);
    }

    public String getUser() {
//        TODO throw exception if not session
        if(Session.getInstance().getUser().isEmpty()) return "";
        return Session.getInstance().getUser().get().getUsername();
    }
}
