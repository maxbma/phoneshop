package com.es.core.model.stock;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Component
public class JdbcStockDao implements StockDao{
    private static final String UPDATE_PHONE_STOCK = "update stocks set reserved = reserved + ? where phoneId = ?";
    private static final String SELECT_STOCK_LIST = "select stock, reserved, phoneId from stocks where phoneId in (%s)";
    private static final String SELECT_PHONE_STOCK = "select stock - reserved from stocks where phoneId = ?";

    @Resource
    private JdbcTemplate jdbcTemplate;

    private StockMapper stockMapper = new StockMapper();

    @Override
    @Transactional
    public void updateStocks(List<Stock> stocks) {
        List<Object[]> batchArgs = new ArrayList<>();
        for (Stock stock : stocks){
            batchArgs.add(new Object[]{stock.getReserved(), stock.getPhoneId()});
        }
        jdbcTemplate.batchUpdate(UPDATE_PHONE_STOCK, batchArgs);
    }

    @Override
    public List<Stock> getStockList(List<Long> idList) {
        if(idList.isEmpty()){
            return Collections.emptyList();
        } else{
            StringBuilder sb = new StringBuilder();
            Iterator<Long> iterator = idList.listIterator();
            while(iterator.hasNext()){
                sb.append(iterator.next());
                if(iterator.hasNext()) sb.append(",");
            }
            String sql = String.format(SELECT_STOCK_LIST, sb);
            return jdbcTemplate.query(sql, stockMapper);
        }
    }

    @Override
    public int getPhoneStockAmount(Long phoneId) {
        return jdbcTemplate.queryForObject(SELECT_PHONE_STOCK, new Object[]{phoneId}, Integer.class);
    }
}
