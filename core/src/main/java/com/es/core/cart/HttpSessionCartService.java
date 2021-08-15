package com.es.core.cart;

import com.es.core.model.phone.JdbcPhoneDao;
import com.es.core.order.OutOfStockException;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Map;

@Service
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class HttpSessionCartService implements CartService {

    @Resource
    private JdbcPhoneDao phoneDao;

    private Cart cart = new Cart();

    @Override
    public Cart getCart() {
        return cart;
    }

    @Override
    public void addPhone(Long phoneId, Long quantity) throws OutOfStockException {
        if(!isEnoughStock(phoneId, quantity)) throw new OutOfStockException("Out of stock");
        Map<Long, Long> items = cart.getItems();
        Long previousQuantity = items.get(phoneId);
        if(previousQuantity == null) previousQuantity = 0L;
        items.put(phoneId, previousQuantity + quantity);
    }

    @Override
    public void update(Map<Long, Long> items) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public void remove(Long phoneId) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public boolean isEnoughStock(Long phoneId, Long quantity){
        Long prevQuantity = cart.getItems().get(phoneId);
        if(prevQuantity == null) prevQuantity = 0L;
        return quantity + prevQuantity <= phoneDao.getPhoneStockAmount(phoneId);
    }

    @Override
    public long getItemsAmount(){
        long amount = 0L;
        Iterator<Map.Entry<Long, Long>> iterator = cart.getItems().entrySet().iterator();
        while(iterator.hasNext()){
            amount += iterator.next().getValue();
        }
        return amount;
    }

    @Override
    public int getOverallPrice(){
        BigDecimal price = new BigDecimal(0);
        for(Map.Entry<Long,Long> entry : cart.getItems().entrySet()){
            price = price.add(phoneDao.get(entry.getKey()).get().getPrice().multiply(new BigDecimal(entry.getValue())));
        }
        return price.intValue();
    }
}

