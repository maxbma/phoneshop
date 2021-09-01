package com.es.core.order;

import com.es.core.model.order.Order;
import com.es.core.model.phone.Phone;

import java.util.Map;

public interface OrderService {
    void validateStocks(Map<Long,Long> items) throws OutOfStockException;
    Order createOrder(Map<Phone,Long> phoneMap);
    Long getDeliveryPrice();
    void placeOrder(Order order, Map<Long,Long> items);
}
