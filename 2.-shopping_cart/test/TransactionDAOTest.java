import dao.*;
import model.*;

import org.junit.jupiter.api.*;

import javax.swing.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TransactionDAOTest {
    private TransactionDAO transactionDAO;

    private List<Product> products;

    private int userId = -1;

    private int transactionId = -1;

    @BeforeAll
    void setUpAll (){
        transactionDAO = new TransactionDAOImpl();

        LoginDAOImpl loginDAO = new LoginDAOImpl();
        User user = new User("zxvvc", "xzxzcv");
        loginDAO.signUp(user);
        this.userId = user.getId();

        products = List.of(
            new Product("Clavado en un bar", "Bar", 2017, 21.10F, "3:01", "Maná", 5, new ImageIcon("images/mana.jpeg")),
            new Product("Corazón de acero", "Corazón de acero", 2004, 13.14F, "3:30", "Yiyo Sarante", 105, new ImageIcon("images/cda.jpeg"))
        );

        ProductDAOImpl productDAO = new ProductDAOImpl();
        products.forEach(
            product -> product.setId(productDAO.insertProduct(product, "", ""))
        );
    }

    @Test
    @Order(1)
    public void insertTransactionTest() {
        List<NetworkLineItem> items = products.stream().map(
            product -> new LineItem(product, 1)
        ).map(NetworkLineItem::new).collect(Collectors.toList());

        transactionId = transactionDAO.insertTransaction(items, userId);

        Assertions.assertNotEquals(transactionId, -1);
    }

    @Test
    @Order(2)
    public void getUserTransactionsTest() {
        List<Transaction> userTransactions = transactionDAO.getUserTransactions(userId);

        Transaction transaction = userTransactions.get(0);

        transaction.getCart().getLineItems().forEach(
            lineItem -> {
                Optional<Product> productFound =
                        products.stream()
                        .filter(product -> lineItem.getProduct().equals(product))
                        .findFirst();

                System.out.println(productFound.get());
                Assertions.assertTrue(productFound.isPresent());
            }
        );

        Assertions.assertEquals(transaction.getUser().getId(), userId);
    }

    @AfterAll
    void cleanUp (){
        System.out.println("Cleaning....");
//        UserDAO userDAO = new UserDAOImpl();
//        userDAO.deleteUser(userId);

        ProductDAOImpl productDAO = new ProductDAOImpl();
        products.forEach(
            product -> productDAO.deleteProduct(product.getId())
        );
    }
}
