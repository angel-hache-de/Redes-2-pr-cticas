/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.io.IOException;
import java.util.Optional;
import model.User;
import network.Client;
import network.PackageNotReceivedException;
import network.PackageNotSentException;
import session.Session;

/**
 *
 * @author Alumno
 */
public class SignupController {
    public Optional<User> signup(String username, String password) throws PackageNotSentException, PackageNotReceivedException, IOException {
        Optional<User> user = Client.sendSignupRequest(new User(username, password));

        user.ifPresent(this::createSession);

        return user;
    }

    private void createSession(User user) {
        Session session = Session.getInstance();
        session.createSession(user);
    }
}
