package com.es.core.model.stock;

import java.util.List;
import java.util.Map;

public interface StockDao {
    void updateStocks(Map<Long,Long> items);
    List<Stock> getStockList(List<Long> idList);
    int getPhoneStockAmount(Long phoneId);
}
