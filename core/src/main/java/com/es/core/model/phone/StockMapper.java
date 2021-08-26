package com.es.core.model.phone;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StockMapper implements RowMapper<Stock> {

    @Override
    public Stock mapRow(ResultSet resultSet, int i) throws SQLException {
        int stockAmount = resultSet.getInt("stock");
        int reserved = resultSet.getInt("reserved");
        long phoneId = resultSet.getLong("phoneId");
        Stock stock = new Stock();
        stock.setStock(stockAmount);
        stock.setReserved(reserved);
        stock.setPhoneId(phoneId);
        return stock;
    }
}
