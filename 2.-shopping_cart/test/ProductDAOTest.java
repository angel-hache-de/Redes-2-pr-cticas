import dao.ProductDAO;
import dao.ProductDAOImpl;
import model.LineItem;
import model.NetworkLineItem;
import model.Product;


import org.junit.jupiter.api.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProductDAOTest {
    private  List<Product> products;
    private ProductDAO productDAO;

    private String[] images;
    private String[] songs;

    @BeforeAll
    void setUpAll (){
        productDAO = new ProductDAOImpl();
        products = List.of(
            new Product("Clavado en un bar", "Bar", 2017, 21.10F, "3:01", "Maná", 5, new ImageIcon("images/mana.jpeg")),
            new Product("Corazón de acero", "Corazón de acero", 2004, 13.14F, "3:30", "Yiyo Sarante", 105, new ImageIcon("images/cda.jpeg"))
        );

        images = new String[]{"mana.jpeg", "cda.jpeg"};
        songs = new String[]{"clavado en un bar.mp3", "corazon de acero.mp3"};
    }

    @Test
    @Order(1)
    public void insertProductsTest() {
        int i = 0;
        for (Product product : products) {
            product.setId(productDAO.insertProduct(product, images[i], songs[i]));
            i++;
            Assertions.assertNotEquals(-1, product.getId());
        }
    }

    @Test
    @Order(2)
    public void getProductsTest() {
        List<Product> products = this.productDAO.selectAll();

        products.stream().forEach(
            product -> System.out.println(product.getName())
        );

        assertEquals(products.size(), this.products.size());
        assertIterableEquals(products, this.products);
    }

    @Test
    @Order(3)
    public void getProductsPriceTest() {
        List<NetworkLineItem> items = createNetworkLineItemsList();

        for (NetworkLineItem networkLineItem : items)
            networkLineItem.setSingleProductPrice(0);

        productDAO.getProductsPrice(items);
        int i = 0;
        for (NetworkLineItem networkLineItem : items)
            assertEquals(networkLineItem.getSingleProductPrice(), this.products.get(i++).getPrice());
    }

    @Test
    @Order(4)
    public void getProductPathsTest() {
        Product product = products.get(0);

        String[] productPaths = productDAO.getProductPaths(product.getId());

        assertEquals(productPaths[0], images[0]);
        assertEquals(productPaths[1], songs[0]);
    }

    @Test
    @Order(5)
    public void updateProductTest() {
        Product product = products.get(0);

        product.setName("Another song");
        assertTrue(productDAO.updateProduct(product));

        assertTrue(productDAO.updateProductPaths(product, "another.jpg", "another.mp3"));
    }

    @Test
    @Order(6)
    public void deleteProductTest() {
        products.stream().map(
            Product::getId
        ).forEach(
            productId ->  {
                assertTrue(productDAO.deleteProduct(productId));
            }
        );
    }

    private List<NetworkLineItem> createNetworkLineItemsList() {
        List<NetworkLineItem> items = new ArrayList<>();

        products.forEach(
            product -> {
                items.add(
                    new NetworkLineItem(new LineItem(product, 12))
                );
            }
        );

        return items;
    }
}
