package com.es.core.model.order;

import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class OrderMapper implements RowMapper<Order> {

    private final static Map<Integer, OrderStatus> orderStatusMap = new HashMap<>();

    static{
        for(OrderStatus status : OrderStatus.values()){
            orderStatusMap.put(status.getStatusId(), status);
        }
    }

    @Override
    public Order mapRow(ResultSet resultSet, int i) throws SQLException {
        long id = resultSet.getLong("id");
        String firstName = resultSet.getString("firstName");
        String lastName = resultSet.getString("lastName");
        String contactPhoneNo = resultSet.getString("contactPhoneNo");
        String deliveryAddress = resultSet.getString("deliveryAddress");
        String additionalInfo = resultSet.getString("additionalInfo");
        LocalDateTime date = resultSet.getTimestamp("orderDate").toLocalDateTime();
        BigDecimal subtotal = resultSet.getBigDecimal("subtotal");
        BigDecimal deliveryPrice = resultSet.getBigDecimal("deliveryPrice");
        BigDecimal totalPrice = resultSet.getBigDecimal("totalPrice");
        OrderStatus status = orderStatusMap.get(resultSet.getInt("statusId"));

        Order order = new Order();
        order.setId(id);
        order.setFirstName(firstName);
        order.setLastName(lastName);
        order.setContactPhoneNo(contactPhoneNo);
        order.setDeliveryAddress(deliveryAddress);
        order.setAdditionalInfo(additionalInfo);
        order.setOrderDate(date);
        order.setSubtotal(subtotal);
        order.setDeliveryPrice(deliveryPrice);
        order.setTotalPrice(totalPrice);
        order.setStatus(status);
        return order;
    }
}
