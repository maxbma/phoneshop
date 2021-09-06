package com.es.core.model.order;

import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class OrderMapper implements RowMapper<Order> {

    private final OrderStatus[] statusValues = OrderStatus.values();
    @Override
    public Order mapRow(ResultSet resultSet, int i) throws SQLException {
        long id = resultSet.getLong("id");
        String firstName = resultSet.getString("firstName");
        String lastName = resultSet.getString("lastName");
        String contactPhoneNo = resultSet.getString("contactPhoneNo");
        String deliveryAddress = resultSet.getString("deliveryAddress");
        String additionalInfo = resultSet.getString("additionalInfo");
        Timestamp date = resultSet.getTimestamp("orderDate");
        BigDecimal subtotal = resultSet.getBigDecimal("subtotal");
        BigDecimal deliveryPrice = resultSet.getBigDecimal("deliveryPrice");
        BigDecimal totalPrice = resultSet.getBigDecimal("totalPrice");
        OrderStatus status = statusValues[resultSet.getInt("statusId") - 1];

        Order order = new Order();
        order.setId(id);
        order.setFirstName(firstName);
        order.setLastName(lastName);
        order.setContactPhoneNo(contactPhoneNo);
        order.setDeliveryAddress(deliveryAddress);
        order.setAdditionalInfo(additionalInfo);
        order.setDate(date);
        order.setSubtotal(subtotal);
        order.setDeliveryPrice(deliveryPrice);
        order.setTotalPrice(totalPrice);
        order.setStatus(status);
        return order;
    }
}
