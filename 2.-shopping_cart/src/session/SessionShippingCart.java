package session;

import model.LineItem;
import model.Product;
import model.ShippingCart;

/**
 * Shipping cart of the current session
 */
public class SessionShippingCart {
    ShippingCart cart = new ShippingCart();
    static SessionShippingCart instance = null;

    public SessionShippingCart() {
    }

    public static SessionShippingCart getInstance() {
        if(instance == null) {
            synchronized (SessionShippingCart.class) {
                if(instance == null)
                    instance = new SessionShippingCart();
            }
        }

        return instance;
    }

    public void setShippingCart(ShippingCart cart) {
        this.cart = cart;
    }
    
    public void addToCart(LineItem item) {
        this.cart.addLineItem(item);
    }

    public void removeItem(Product product, int quantity) {
        this.cart.removeItem(product, quantity);
    }
    
    public void clearCart() {
        this.cart = new ShippingCart();
    }

    public ShippingCart getShippingCart() {
        ShippingCart cart = new ShippingCart();
        this.cart.getLineItems().stream().forEach(
                item -> cart.addLineItem(item)
        );

        return cart;
    }
}
