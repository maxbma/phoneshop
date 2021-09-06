package com.es.core.model.order;

import java.util.List;

public interface OrderDao {
    void insertOrder(Order order);
    void changeStatus(Long orderId, Integer statusId);
    List<Order> getAllOrders();
    Order getOrderById(Long id);
    List<OrderItem> getOrderItemsById(Long orderId);
}
