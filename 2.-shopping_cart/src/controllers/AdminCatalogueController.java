package controllers;

import model.Product;
import network.Client;
import network.PackageNotReceivedException;
import network.PackageNotSentException;

import java.io.IOException;
import java.util.List;

public class AdminCatalogueController {
    public String delteProduct(Product product) throws IOException, PackageNotSentException, PackageNotReceivedException {
//        TODO call client
        String response = Client.sendDeleteProductRequest(product.getId());
        return response;
    }

    public List<Product> getSongs() throws PackageNotSentException, PackageNotReceivedException, IOException {
        List<Product> products = Client.sendGetSongsRequest();
        return products;
    }
}
