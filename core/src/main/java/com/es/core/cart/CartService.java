package com.es.core.cart;

import com.es.core.order.OutOfStockException;

import java.util.Map;

public interface CartService {

    Cart getCart();

    void addPhone(Long phoneId, Long quantity) throws OutOfStockException;
    void update(Map<Long, Long> items);
    void remove(Long phoneId);
    boolean isEnoughStock(Long phoneId, Long quantity, int stockAmount);
    long getItemsAmount();
    int getOverallPrice();

}

