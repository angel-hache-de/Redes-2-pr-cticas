import dao.*;
import network.Server;

import java.io.IOException;

public class MainServer {
    public static void main(String[] args) {
        Server server = null;
        try {
            int port = 1234;
            ProductDAO productDAO = new ProductDAOImpl();
            LoginDAO loginDAO = new LoginDAOImpl();
            TransactionDAO transactionDAO = new TransactionDAOImpl();
            UserDAO userDAO = new UserDAOImpl();

            server = new Server(port, productDAO, loginDAO, transactionDAO, userDAO);
            server.listen();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
