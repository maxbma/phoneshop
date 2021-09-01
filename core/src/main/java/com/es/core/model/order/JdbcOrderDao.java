package com.es.core.model.order;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcOrderDao implements OrderDao{
    private final static String INSERT_ORDER_STATUS = "update orders set statusId = ? where id = ?";
    private final static String INSERT_INTO_PHONE2ORDER = "insert into phone2order(phoneId,orderId,quantity,price) values(?,?,?,?)";
    private final static String SELECT_ALL_ORDERS = "select * from orders";

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
        Object[] args = new Object[]{order.getStatus().getStatusId(), orderId};
        jdbcTemplate.update(INSERT_ORDER_STATUS, args);
        insertIntoPhone2Order(order);
    }

    private void insertIntoPhone2Order(Order order){
        List<Object[]> batchArgs = new ArrayList<>();
        long orderId = order.getId();
        for(OrderItem item : order.getOrderItems()){
            batchArgs.add(new Object[]{item.getPhone().getId(), orderId, item.getQuantity(),item.getPhone().getPrice()});
        }
        jdbcTemplate.batchUpdate(INSERT_INTO_PHONE2ORDER, batchArgs);
    }
}
