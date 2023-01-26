package controllers;

import model.User;
import network.Client;
import network.PackageNotReceivedException;
import network.PackageNotSentException;
import session.Session;

import java.io.IOException;
import java.util.Optional;

public class LoginController {
    public Optional<User> login(String username, String password) throws PackageNotSentException, PackageNotReceivedException, IOException {
        Optional<User> user = Client.sendLoginRequest(new User(username, password));

        user.ifPresent(this::createSession);

        return user;
    }

    private void createSession(User user) {
        Session session = Session.getInstance();
        session.createSession(user);
    }
}
