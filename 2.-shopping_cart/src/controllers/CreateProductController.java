package controllers;

import model.Product;
import network.Client;
import network.PackageNotReceivedException;
import network.PackageNotSentException;

import java.io.IOException;

public class CreateProductController {
    public String createProduct(Product p, String songPath) throws PackageNotSentException, PackageNotReceivedException, IOException {
        return Client.sendCreateProductRequest(p, songPath);
    }
}
