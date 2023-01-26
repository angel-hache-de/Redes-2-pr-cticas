package dao;

import model.User;
import sql.DBConnection;
import sql.ExceptionHanlder;

import java.sql.*;

public class LoginDAOImpl implements LoginDAO {
    private static final String SELECT_USER_SQL =
            "select * from user where name = ? and password = ?";

    private static final String INSERT_USER_SQL = "INSERT INTO user"
            + "  (name, password, role) VALUES " + " (?, ?, ?);";

    private static final String DELETE_USER_SQL = "delete from user where user_id = ?";

    @Override
    public void signUp(User u) {
        // try-with-resource statement will auto close the connection.
        try (Connection connection = DBConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USER_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, u.getUsername());
            preparedStatement.setString(2, u.getPassword());
            preparedStatement.setByte(3, User.USER_ROLE);

            int affectedRows = preparedStatement.executeUpdate();

//            Get the id inserted
            if(affectedRows > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int userId = generatedKeys.getInt(1);
                        u.setId(userId);
                        u.setRole(User.USER_ROLE);
                    }
                    else {
//                        TODO Undo the transaction movement
                        throw new SQLException("Error while creating transaction");
                    }
                }
            }
        } catch (SQLException exception) {
            ExceptionHanlder.printSQLException(exception);
        }
    }

    @Override
    public boolean login(User u) {
        // Step 1: Establishing a Connection
        try (Connection connection = DBConnection.getConnection();
        // Step 2:Create a statement using connection object
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USER_SQL)) {
            preparedStatement.setString(1, u.getUsername());
            preparedStatement.setString(2, u.getPassword());

            // Step 3: Execute the query or update query
            ResultSet rs = preparedStatement.executeQuery();

            // Step 4: Process the ResultSet object.
            while (rs.next()) {
                int id = rs.getInt("user_id");
                byte role = rs.getByte("role");
                u.setId(id);
                u.setRole(role);
                return true;
            }
        } catch (SQLException exception) {
            ExceptionHanlder.printSQLException(exception);
        }

        return false;
    }

    public boolean deleteUser(int userId){
        boolean rowDeleted = false;
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_USER_SQL);) {
            statement.setInt(1, userId);
            rowDeleted = statement.executeUpdate() > 0;
        } catch (SQLException e) {
            ExceptionHanlder.printSQLException(e);
        }

        return rowDeleted;
    }
}
