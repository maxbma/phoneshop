package com.es.core.cart;

import com.es.core.model.phone.Phone;
import com.es.core.order.OutOfStockException;

import java.util.List;
import java.util.Map;

public interface CartService {

    Cart getCart();
    void addPhone(Long phoneId, Long quantity) throws OutOfStockException;
    void update(List<CartItem> items) throws OutOfStockException;
    void remove(Long phoneId);
    boolean isEnoughStock(Long phoneId, Long quantity, int stockAmount);
    CartTotal getCartTotal(Map<Long,Long> itemsCopy);
    Map<Phone, Long> getPhoneMap(Map<Long,Long> itemsCopy);
}

