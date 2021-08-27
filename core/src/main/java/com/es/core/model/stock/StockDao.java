package com.es.core.model.stock;

import java.util.List;

public interface StockDao {
    void updateStocks(List<Stock> stocks);
    List<Stock> getStockList(List<Long> idList);
    int getPhoneStockAmount(Long phoneId);
}
