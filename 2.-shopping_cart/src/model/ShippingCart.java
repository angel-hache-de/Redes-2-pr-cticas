package model;

import model.LineItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ShippingCart implements Serializable {
    private List<LineItem> lineItems;
    //private HashMap<Integer, LineItem> items = new HashMap<>();

    public ShippingCart() {
        lineItems = new ArrayList<>();
    }

    public ShippingCart(List<LineItem> lineItems) {
        this();
        lineItems.forEach(this::addLineItem);
    }

    public void addLineItem(LineItem lineItem) {
        lineItems.add(lineItem);
        //items.put(lineItem.getProduct().getId(), lineItem);
    }
    
    //removes quantity units of the gicven product
    public void removeItem(Product product, int quantity) {
        for (Iterator<LineItem> iterator = lineItems.iterator(); iterator.hasNext();) {
            LineItem item = iterator.next();

            if(!item.getProduct().equals(product))
                continue;

            int remaining = item.getQuantity() - quantity;
            if(remaining <= 0) {
                lineItems.remove(item);
            } else item.setQuantity(remaining);

            break;
        }
    }

    public List<LineItem> getLineItems() {
        return lineItems.stream()
                .map(LineItem::new)
                .collect(Collectors.toList());
    }

    public double getTotalCost() {
        return lineItems.stream()
                .mapToDouble(LineItem::getPrice)
                .sum();
    }

    @Override
    public String toString() {
        return "ShoppingCart{" +
                "lineItems=" + lineItems +
                '}';
    }
}
