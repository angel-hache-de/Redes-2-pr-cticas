package dao;

import model.User;

public interface UserDAO {
    public void insertUser(User u);

    public boolean deleteUser(int userId);
}
