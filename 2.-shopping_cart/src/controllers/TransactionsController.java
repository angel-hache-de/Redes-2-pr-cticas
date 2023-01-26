package controllers;

import model.Transaction;
import network.Client;
import network.PackageNotReceivedException;
import network.PackageNotSentException;
import session.Session;

import java.io.IOException;
import java.util.List;

public class TransactionsController {
    public List<Transaction> getTransactions() throws PackageNotSentException, PackageNotReceivedException, IOException {
        if(Session.getInstance().getUser().isEmpty()) return List.of();

        int userId = Session.getInstance().getUser().get().getId();
        return Client.sendGetTransactionsRequest(userId);
    }
}
