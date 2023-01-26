package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import model.NetworkLineItem;
import model.Product;
import sql.DBConnection;
import sql.ExceptionHanlder;

import javax.swing.*;

public class ProductDAOImpl implements ProductDAO {
    private static final String INSERT_PRODUCT_SQL = "INSERT INTO product"
            + "  (name, price, number_of_downloads, album, artist, year, duration, image_filename, audio_filename) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";

    private static final String SELECT_PRODUCT_BY_ID = "select * from product where product_id =?";
    private static final String SELECT_ALL_PRODUCTS = "select * from product";
    private static final String DELETE_PRODUCT_BY_ID = "delete from product where product_id = ?;";
    private static final String UPDATE_PRODUCT = "update product set name = ?, price= ?, number_of_downloads =?, album =?, artist = ?, year =?, duration = ? where product_id = ?;";
    private static final String UPDATE_PRODUCT_PATHS = "update product set image_filename = ?, audio_filename= ? where product_id = ?;";
    private static final String GET_PRICE_BY_ID =  "select product_id, price from product where product_id in (";
    private static final String GET_PATHs_BY_ID = "select image_filename, audio_filename from product where product_id in (?)";
    private static final String GET_SINGLE_PATHS_BY_ID = "select image_filename, audio_filename from product where product_id = ?";

    public ProductDAOImpl() {
    }

    @Override
    public boolean updateDownloadsNumber(int productId) {
        Product product = selectProduct(productId);

        product.setAmountOfDownloads(product.getAmountOfDownloads() + 1);
        return updateProduct(product);
    };

    @Override
    public int insertProduct(Product p, String imagePath, String songPath) {
        int id = -1;
        // try-with-resource statement will auto close the connection.
        try (Connection connection = DBConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_PRODUCT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, p.getName());
            preparedStatement.setFloat(2,  p.getPrice());
            preparedStatement.setInt(3,    p.getAmountOfDownloads());
            preparedStatement.setString(4, p.getAlbum());
            preparedStatement.setString(5, p.getArtist());
            preparedStatement.setString(6, String.valueOf(p.getYear()));
            preparedStatement.setString(7, p.getDuration());
            preparedStatement.setString(8, imagePath);
            preparedStatement.setString(9, songPath);

            int affectedRows = preparedStatement.executeUpdate();

            if(affectedRows > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next())
                        id = generatedKeys.getInt(1);
                    else {
//                        TODO Undo the transaction movement
                        throw new SQLException("Error while creating transaction");
                    }
                }
            }
        } catch (SQLException exception) {
            ExceptionHanlder.printSQLException(exception);
        }

        return id;
    }

    @Override
    public Product selectProduct(int productId) {
        Product product = null;
        // Step 1: Establishing a Connection
        try (Connection connection = DBConnection.getConnection();
            // Step 2:Create a statement using connection object
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_PRODUCT_BY_ID)) {
            preparedStatement.setLong(1, productId);
            System.out.println(preparedStatement);
            // Step 3: Execute the query or update query
            ResultSet rs = preparedStatement.executeQuery();

            // Step 4: Process the ResultSet object.
            while (rs.next()) {
                int id = rs.getInt("product_id");
                String name = rs.getString("name");
                String album = rs.getString("album");
                int year = Integer.parseInt(rs.getString("year"));
                float price = rs.getFloat("price");
                int amountOfDownloads = rs.getInt("number_of_downloads");
                String imagePath = rs.getString("image_filename");
                String duration = rs.getString("duration");
                String artist = rs.getString("artist");

//                LocalDate targetDate = rs.getDate("target_date").toLocalDate();
//                boolean isDone = rs.getBoolean("is_done");
                product = new Product(
                        id, name, album, year, price, duration, artist, amountOfDownloads, new ImageIcon(imagePath)
                );
            }
        } catch (SQLException exception) {
            ExceptionHanlder.printSQLException(exception);
        }
        return product;
    }

    @Override
    public List<Product> selectAll(){
// using try-with-resources to avoid closing resources (boiler plate code)
        List<Product> products = new ArrayList<>();

        // Step 1: Establishing a Connection
        try (Connection connection = DBConnection.getConnection();
            // Step 2:Create a statement using connection object
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_PRODUCTS)) {
            System.out.println(preparedStatement);
            // Step 3: Execute the query or update query
            ResultSet rs = preparedStatement.executeQuery();

            // Step 4: Process the ResultSet object.
            while (rs.next()) {
                int id = rs.getInt("product_id");
                String name = rs.getString("name");
                String album = rs.getString("album");
                int year = Integer.parseInt(rs.getString("year"));
                float price = rs.getFloat("price");
                int amountOfDownloads = rs.getInt("number_of_downloads");
                String imagePath = rs.getString("image_filename");
                String duration = rs.getString("duration");
                String artist = rs.getString("artist");

                products.add(new Product(
                    id, name, album, year, price, duration, artist, amountOfDownloads, new ImageIcon("images/" + imagePath)
                ));
            }
        } catch (SQLException exception) {
            ExceptionHanlder.printSQLException(exception);
        }
        return products;
    }

    @Override
    public boolean deleteProduct(int productId){
        boolean rowDeleted = false;
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_PRODUCT_BY_ID);) {
            statement.setInt(1, productId);
            rowDeleted = statement.executeUpdate() > 0;
        } catch (SQLException e) {
            ExceptionHanlder.printSQLException(e);
        }

        return rowDeleted;
    }

    @Override
    public boolean updateProduct(Product p) {
        boolean rowUpdated = false;
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_PRODUCT);) {

            statement.setString(1, p.getName());
            statement.setFloat(2, p.getPrice());
            statement.setInt(3, p.getAmountOfDownloads());
            statement.setString(4, p.getAlbum());
            statement.setString(5, p.getArtist());
            statement.setString(6, String.valueOf(p.getYear()));
            statement.setString(7, p.getDuration());
            statement.setInt(8, p.getId());

            rowUpdated = statement.executeUpdate() > 0;
        } catch (SQLException e) {
            ExceptionHanlder.printSQLException(e);
        }
        return rowUpdated;
    }

    public boolean updateProductPaths(Product p, String imagePath, String songPath) {
        boolean rowUpdated = false;
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_PRODUCT_PATHS)) {

            statement.setString(1, imagePath);
            statement.setString(2, songPath);
            statement.setInt(3, p.getId());

            rowUpdated = statement.executeUpdate() > 0;
        } catch (SQLException e) {
            ExceptionHanlder.printSQLException(e);
        }
        return rowUpdated;
    }

    /**
     * Sets the current db price to each item
     * @param items
     * @return
     */
    @Override
    public void getProductsPrice(List<NetworkLineItem> items) {
//        get the ids.
        List<Integer> ids = items
                .stream()
                .map(NetworkLineItem::getProductId)
                .collect(Collectors.toList());

        StringBuilder statement = new StringBuilder(GET_PRICE_BY_ID);

        for(NetworkLineItem item: items)
            statement.append("?,");

        statement.deleteCharAt(statement.length() - 1);
        statement.append(");");

//        items.forEach(item -> {item.setProductPrice(item.getProductPrice() + 1); });
    // Step 1: Establishing a Connection
        try (Connection connection = DBConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(statement.toString())) {

             // Step 2:Create a statement using connection object
            for (int i = 0; i < items.size(); i++) {
                NetworkLineItem item = items.get(i);
                preparedStatement.setInt(i + 1, item.getProductId());
            }

            // Step 3: Execute the query or update query
            ResultSet rs = preparedStatement.executeQuery();

            // Step 4: Process the ResultSet object.
            while (rs.next()) {
                int id = rs.getInt("product_id");
                float price = rs.getFloat("price");

                items.forEach(
                    item -> {
                        if( id == item.getProductId() )
                            item.setSingleProductPrice(price);
                    }
                );
            }
        } catch (SQLException exception) {
            ExceptionHanlder.printSQLException(exception);
        }
    }

    @Override
    @Deprecated
    public List<String[]> getProductsPaths(List<NetworkLineItem> items) {
        ArrayList<String[]> paths = new ArrayList<String[]>();
//        get the ids.
        List<Integer> ids = items
                .stream()
                .map(NetworkLineItem::getProductId)
                .collect(Collectors.toList());

        // Step 1: Establishing a Connection
        try (Connection connection = DBConnection.getConnection();
             // Step 2:Create a statement using connection object
             PreparedStatement preparedStatement = connection.prepareStatement(GET_PATHs_BY_ID)) {
            Array array = connection.createArrayOf("int", ids.toArray());
            preparedStatement.setArray(1, array);

            // Step 3: Execute the query or update query
            ResultSet rs = preparedStatement.executeQuery();

            // Step 4: Process the ResultSet object.
            while (rs.next()) {
                String[] pathsArray = { rs.getString("imagePath"), rs.getString("songPath") };
                paths.add(pathsArray);
            }
        } catch (SQLException exception) {
            ExceptionHanlder.printSQLException(exception);
        }

        return paths;
    }

    public String[] getProductPaths(int id) {
        String[] pathsArray = new String[2];
        // Step 1: Establishing a Connection
        try (Connection connection = DBConnection.getConnection();
             // Step 2:Create a statement using connection object
             PreparedStatement preparedStatement = connection.prepareStatement(GET_SINGLE_PATHS_BY_ID)) {

            preparedStatement.setInt(1, id);

            // Step 3: Execute the query or update query
            ResultSet rs = preparedStatement.executeQuery();

            // Step 4: Process the ResultSet object.
            while (rs.next()) {
                pathsArray[0] = rs.getString("image_filename");
                pathsArray[1] = rs.getString("audio_filename");
            }
        } catch (SQLException exception) {
            ExceptionHanlder.printSQLException(exception);
        }

        return pathsArray;
    }
}
