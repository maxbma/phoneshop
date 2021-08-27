package com.es.core.model.order;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

public class JdbcOrderDao implements OrderDao{
    private final static String INSERT_ORDER = "insert into orders(id,subtotal,deliveryPrice,totalPrice,firstName,lastName,deliveryAddress,contactPhoneNo,statusId) values (?,?,?,?,?,?,?,?,?)";
    private final static String INSERT_INTO_PHONE2ORDER = "insert into phone2order(phoneId,orderId) values(?,?)";

    @Resource
    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert jdbcInsert;

    @PostConstruct
    private void initJdbcInsert(){
        jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName("orders").
                usingGeneratedKeyColumns("id").
                compile();
    }

    @Override
    @Transactional
    public void insertOrder(Order order) {
        Long orderId = (jdbcInsert.executeAndReturnKey(new BeanPropertySqlParameterSource(order))).longValue();
        order.setId(orderId);
        Object[] args = new Object[]{orderId,order.getSubtotal(),order.getDeliveryPrice(),order.getTotalPrice(),
                order.getFirstName(),order.getLastName(),order.getDeliveryAddress(),order.getContactPhoneNo(),
                (order.getStatus().ordinal()+1)};
        jdbcTemplate.update(INSERT_ORDER, args);
        insertIntoPhone2Order(order);
    }

    private void insertIntoPhone2Order(Order order){
        List<Object[]> batchArgs = new ArrayList<>();
        long orderId = order.getId();
        for(OrderItem item : order.getOrderItems()){
            batchArgs.add(new Object[]{item.getPhone().getId(), orderId});
        }
        jdbcTemplate.batchUpdate(INSERT_INTO_PHONE2ORDER, batchArgs);
    }
}
