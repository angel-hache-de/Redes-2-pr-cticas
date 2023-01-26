package dao;

import model.*;
import sql.DBConnection;
import sql.DateHandler;
import sql.ExceptionHanlder;

import javax.swing.*;
import java.sql.*;

import java.util.ArrayList;
import java.util.List;

public class TransactionDAOImpl implements TransactionDAO {
    private static final String INSERT_TRANSACTION_SQL = "INSERT INTO transaction"
            + "  (total, user_id) VALUES " + " (?, ?);";

    private static final String INSERT_PRODUCT_IN_TRANSACTION_SQL = "INSERT INTO products_in_transaction"
            + "  (transaction_id, product_id, quantity, product_price) VALUES " + " (?, ?, ?, ?);";

    private static final String GET_USER_TRANSACTIONS_SQL = "SELECT * FROM transaction where"
            + " user_id = ?;";

    private static final String GET_PRODUCTS_IN_TRANSACTION_SQL = "SELECT * FROM products_in_transaction natural join product where"
            + " transaction_id = ?;";

    private int doTransaction(int userId, ShippingCart cart, String date) {
        int insertedId = -1;
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_TRANSACTION_SQL, Statement.RETURN_GENERATED_KEYS)) {
            //          Inserts the transaction
//            preparedStatement.setDate(1, DateHandler.getSQLDate(date));
            preparedStatement.setDouble(1, cart.getTotalCost());
            preparedStatement.setInt(2, userId);

//            System.out.println(preparedStatement);
            int affectedRows = preparedStatement.executeUpdate();

//            Insert the lineItems
            if(affectedRows > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        insertedId = generatedKeys.getInt(1);
                        insertProductsInTransaction(connection, insertedId, cart);
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

        return insertedId;
    }

    @Override
    public void insertTransaction(Transaction t) {
//        System.out.println(INSERT_TRANSACTION_SQL);
        // try-with-resource statement will auto close the connection.
        doTransaction(t.getUser().getId(), t.getCart(), t.getDate());
    }

    private void insertProductsInTransaction(Connection con, int transactionId, ShippingCart cart) {
        cart.getLineItems().forEach(
            item -> {
                try(PreparedStatement preparedStatement = con.prepareStatement(INSERT_PRODUCT_IN_TRANSACTION_SQL)) {
                    preparedStatement.setInt(1, transactionId);
                    preparedStatement.setInt(2, item.getProduct().getId());
                    preparedStatement.setInt(3, item.getQuantity());
                    preparedStatement.setFloat(4, item.getProduct().getPrice());

                    preparedStatement.executeUpdate();
                } catch (SQLException e) {
                    ExceptionHanlder.printSQLException(e);
                }
            }
        );
    }

    @Override
    public int insertTransaction(List<NetworkLineItem> items, int userId) {
        ShippingCart sc = new ShippingCart();
        items.forEach(
            item -> {
                Product p = new Product(item.getProductId(), item.getSingleProductPrice());
                LineItem li = new LineItem(p, item.getQuantity());
                sc.addLineItem(li);
            }
        );
        return doTransaction(userId, sc, "");
    }

    @Override
    public List<Transaction> getUserTransactions(int userId) {
        List<Transaction> transactions = new ArrayList<>();

        // using try-with-resources to avoid closing resources (boiler plate code)
        // Step 1: Establishing a Connection
        try (Connection connection = DBConnection.getConnection();
             // Step 2:Create a statement using connection object
             PreparedStatement preparedStatement = connection.prepareStatement(GET_USER_TRANSACTIONS_SQL)) {

            preparedStatement.setInt(1, userId);

            // Step 3: Execute the query or update query
            ResultSet rs = preparedStatement.executeQuery();

            // Step 4: Process the ResultSet object.
            while (rs.next()) {
                int transactionId = rs.getInt("transaction_id");
                String date = rs.getString("date");
                float total = rs.getFloat("total");

                ShippingCart sc = new ShippingCart(getProductsInTransaction(transactionId, connection));

                transactions.add(new Transaction(
                    sc, new User(userId), date, total
                ));
            }
        } catch (SQLException exception) {
            ExceptionHanlder.printSQLException(exception);
        }
        return transactions;
    };

    private List<LineItem> getProductsInTransaction(int transactionId, Connection connection) {
        List<LineItem> items = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(GET_PRODUCTS_IN_TRANSACTION_SQL)) {
            preparedStatement.setInt(1, transactionId);

            // Step 3: Execute the query or update query
            ResultSet rs = preparedStatement.executeQuery();

            // Step 4: Process the ResultSet object.
            while (rs.next()) {
                int id = rs.getInt("product_id");
                String name = rs.getString("name");
                String album = rs.getString("album");
                int year = Integer.parseInt(rs.getString("year"));
                int quantity = Integer.parseInt(rs.getString("quantity"));
                float price = rs.getFloat("product_price");
                int amountOfDownloads = rs.getInt("number_of_downloads");
                String imagePath = rs.getString("image_filename");
                String duration = rs.getString("duration");
                String artist = rs.getString("artist");

                items.add(new LineItem(
                        new Product(id, name, album, year, price, duration, artist, amountOfDownloads, new ImageIcon("images/" + imagePath)),
                        quantity
                ));
            }
        } catch (SQLException exception) {
            ExceptionHanlder.printSQLException(exception);
        }

        return items;
    }
}
