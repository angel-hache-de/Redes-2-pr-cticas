package dao;

import model.User;

public interface LoginDAO {
    public void signUp(User u);
    public boolean login(User u);
}
