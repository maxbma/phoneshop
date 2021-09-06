package com.es.core.model.order;

import com.es.core.model.stock.StockDao;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcOrderDao implements OrderDao{
    private final static String INSERT_ORDER_STATUS = "update orders set statusId = ? where id = ?";
    private final static String INSERT_INTO_PHONE2ORDER = "insert into phone2order(phoneId,orderId,quantity,price) values(?,?,?,?)";
    private final static String SELECT_ALL_ORDERS = "select * from orders";
    private final static String SELECT_ORDER_BY_ID = "select * from orders where id = ?";
    private final static String SELECT_ORDER_ITEMS_FROM_ORDER = "select * from phone2order join phones on phone2order.phoneId = phones.id left join phone2color on phones.id = phone2color.phoneId left join colors on phone2color.colorId = colors.id where phone2order.orderId = ?";
    private final static String UPDATE_ORDER_STATUS = "update orders set statusId = ? where id = ?";
    private final static String UPDATE_STOCK_STATUS_DELIVERED = "update stocks set stock = stock - ?, reserved = reserved - ? where phoneId = ?";
    private final static String UPDATE_STOCK_STATUS_REJECTED = "update stocks set reserved = reserved - ? where phoneId = ?";

    @Resource
    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert jdbcInsert;

    private final OrderMapper orderMapper = new OrderMapper();
    private final OrderItemExtractor orderItemExtractor = new OrderItemExtractor();

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
        order.setDate(new Timestamp(System.currentTimeMillis()));
        Long orderId = (jdbcInsert.executeAndReturnKey(new BeanPropertySqlParameterSource(order))).longValue();
        order.setId(orderId);
        Object[] args = new Object[]{order.getStatus().getStatusId(), orderId};
        jdbcTemplate.update(INSERT_ORDER_STATUS, args);
        insertIntoPhone2Order(order);
    }

    @Override
    @Transactional
    public void changeStatus(Long orderId, Integer statusId) {
        Object[] args = new Object[]{statusId,orderId};
        jdbcTemplate.update(UPDATE_ORDER_STATUS, args);

        List<OrderItem> itemList = getOrderItemsById(orderId);
        List<Object[]> batchArgs = new ArrayList<>();
        if(statusId == 2){
            for(OrderItem item : itemList){
                batchArgs.add(new Object[]{item.getQuantity(),item.getQuantity(), item.getPhone().getId()});
            }
            jdbcTemplate.batchUpdate(UPDATE_STOCK_STATUS_DELIVERED,batchArgs);
        } else if(statusId == 3) {
            for(OrderItem item : itemList){
                batchArgs.add(new Object[]{item.getQuantity(),item.getPhone().getId()});
            }
            jdbcTemplate.batchUpdate(UPDATE_STOCK_STATUS_REJECTED, batchArgs);
        }
    }

    @Override
    public List<Order> getAllOrders() {
        return jdbcTemplate.query(SELECT_ALL_ORDERS, orderMapper);
    }

    @Override
    public Order getOrderById(Long id) {
        Order order;
        try{
            order =  jdbcTemplate.queryForObject(SELECT_ORDER_BY_ID, new Object[]{id}, orderMapper);
        } catch(EmptyResultDataAccessException e){
            return null;
        }
        return order;
    }

    @Override
    public List<OrderItem> getOrderItemsById(Long orderId) {
        return jdbcTemplate.query(SELECT_ORDER_ITEMS_FROM_ORDER, new Object[]{orderId}, orderItemExtractor);
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
