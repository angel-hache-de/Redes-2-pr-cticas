package controllers;

import model.Product;
import network.Client;
import network.PackageNotReceivedException;
import network.PackageNotSentException;

import java.io.IOException;

public class UpdateProductController {
    public String updateProduct(Product product, String filePath, int option) throws PackageNotSentException, PackageNotReceivedException, IOException {
        return Client.sendUpdateProductRequest(product, filePath, option);
    }
}
