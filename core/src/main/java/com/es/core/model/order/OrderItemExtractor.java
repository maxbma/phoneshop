package com.es.core.model.order;

import com.es.core.model.phone.Color;
import com.es.core.model.phone.ColorMapper;
import com.es.core.model.phone.Phone;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderItemExtractor implements ResultSetExtractor<List<OrderItem>> {

    private RowMapper<Phone> phoneMapper = new BeanPropertyRowMapper<>(Phone.class);
    private ColorMapper colorMapper = new ColorMapper();

    @Override
    public List<OrderItem> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
        Map<Long, OrderItem> itemMap = new HashMap<>();
        Map<Long, Phone> phoneMap = new HashMap<>();
        while(resultSet.next()){
            long phoneId = resultSet.getLong("phones.id");
            OrderItem item = itemMap.get(phoneId);
            Phone phone = phoneMap.get(phoneId);
            if(phone == null){
                phone = phoneMapper.mapRow(resultSet, resultSet.getRow());
                phone.setPrice(resultSet.getBigDecimal("phone2order.price"));
                phone.setId(phoneId);
                phoneMap.put(phoneId, phone);
            }

            String colorCode = resultSet.getString("code");
            long colorId = resultSet.getLong("colorId");

            if(colorId != 0 && colorCode != null) {
                Color color = colorMapper.mapRow(resultSet, resultSet.getRow());
                phone.getColors().add(color);
            }

            if(item == null){
                item = new OrderItem();
                item.setPhone(phone);
                item.setQuantity(resultSet.getLong("quantity"));
                itemMap.put(phoneId,item);
            }
        }
        return new ArrayList<>(itemMap.values());
    }
}
