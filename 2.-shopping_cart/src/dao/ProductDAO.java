package dao;

import model.NetworkLineItem;
import model.Product;

import java.util.List;

public interface ProductDAO {
    public int insertProduct(Product p, String imagePath, String songPath);
    public Product selectProduct(int productId);
    public List<Product> selectAll();
    public boolean deleteProduct(int productId);
    public boolean updateProduct(Product p);
    public boolean updateProductPaths(Product p, String imagePath, String songPath);
    public void getProductsPrice(List<NetworkLineItem> items);
    public List<String[]> getProductsPaths(List<NetworkLineItem> items);
    public String[] getProductPaths(int id);
    public boolean updateDownloadsNumber(int productId);
}
