package dao;

import model.User;
import sql.DBConnection;
import sql.ExceptionHanlder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserDAOImpl implements UserDAO {
    private static final String DELETE_USER_BY_ID = "delete from user where user_id = ?;";

    @Override
    public void insertUser(User u) {
//        TODO implement db connection
        return;
    }

    @Override
    public boolean deleteUser(int userId) {
        boolean rowDeleted = false;
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_USER_BY_ID);) {
            statement.setInt(1, userId);
            rowDeleted = statement.executeUpdate() > 0;
        } catch (SQLException e) {
            ExceptionHanlder.printSQLException(e);
        }

        return rowDeleted;
    }
}
