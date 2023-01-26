package model;

import java.io.Serializable;

public class LineItem implements Serializable {
    private final Product product;
    private int quantity;

    public LineItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public LineItem(LineItem li) {
        this(li.product, li.quantity);
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public float getPrice() {
        return product.getPrice() * quantity;
    }

    @Override
    public String toString() {
        return "\n\t" + "LineItem{" +
                "product=" + product +
                ", quantity=" + quantity +
                ", price=" + getPrice() +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null)
            return false;

        if (getClass() != obj.getClass())
            return false;

        final LineItem other = (LineItem) obj;
        if (this.product.getId() != other.getProduct().getId())
            return false;

        return true;
    }

//    @Override
//    public int compareTo(LineItem item) {
//        return com
//        if(amountOfDownloads > otherProduct.amountOfDownloads)
//            return 1;
//        if(amountOfDownloads < otherProduct.amountOfDownloads)
//            return -1;
//        return 0;
//    }
}
