package com.es.core.order;

import com.es.core.model.order.Order;
import com.es.core.model.order.OrderItem;
import com.es.core.model.phone.Phone;
import com.es.core.model.phone.PhoneDao;
import com.es.core.model.stock.Stock;
import com.es.core.model.stock.StockDao;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
//@PropertySource("classpath:application.properties")
public class OrderServiceImpl implements OrderService {

    @Value("${delivery.price}")
    private Long deliveryPrice;

    @Resource
    private PhoneDao phoneDao;
    @Resource
    private StockDao stockDao;

    @Override
    public void validateStocks(Map<Long, Long> items) throws OutOfStockException {
        List<Long> phoneIdWithException = new ArrayList<>();
        List<Stock> stocks = stockDao.getStockList(new ArrayList<>(items.keySet()));
        Map<Long, Stock> stockMap = new HashMap<>();
        stocks.forEach(stock -> stockMap.put(stock.getPhoneId(), stock));
        for (Map.Entry<Long,Long> entry : items.entrySet()){
            long phoneId = entry.getKey(), quantity = entry.getValue();
            Stock stock = stockMap.get(phoneId);
            int stockAmount = stock.getStock() - stock.getReserved();
            if(quantity > stockAmount){
                phoneIdWithException.add(phoneId);
            }
        }
        if(phoneIdWithException.size() > 0) throw new OutOfStockException(phoneIdWithException);
    }

    @Override
    public Order createOrder(Map<Phone,Long> phoneMap) {
        Order order = new Order();
        List<OrderItem> itemList = new ArrayList<>();
        for(Map.Entry<Phone,Long> entry : phoneMap.entrySet()){
            OrderItem item = new OrderItem();
            item.setQuantity(entry.getValue());
            item.setPhone(entry.getKey());
            itemList.add(item);
        }
        order.setOrderItems(itemList);
        return order;
    }

    @Override
    public void placeOrder(Order order){

    }

    public Long getDeliveryPrice() {
        return deliveryPrice;
    }
}
