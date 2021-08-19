package com.es.core.cart;

import com.es.core.model.phone.JdbcPhoneDao;
import com.es.core.model.phone.Phone;
import com.es.core.order.OutOfStockException;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class HttpSessionCartService implements CartService {

    private Lock lock = new ReentrantLock();

    @Resource
    private JdbcPhoneDao phoneDao;

    private final Cart cart = new Cart();

    @Override
    public Cart getCart() {
        return cart;
    }

    public HttpSessionCartService(){}
    public HttpSessionCartService(JdbcPhoneDao phoneDao){
        this.phoneDao = phoneDao;
    }

    @Override
    public void addPhone(Long phoneId, Long quantity) throws OutOfStockException {
        int stockAmount = phoneDao.getPhoneStockAmount(phoneId);
        lock.lock();
        try{
            if(!isEnoughStock(phoneId, quantity, stockAmount)) throw new OutOfStockException("Out of stock");
            Map<Long, Long> items = cart.getItems();
            Long previousQuantity = items.get(phoneId);
            if(previousQuantity == null) previousQuantity = 0L;
            items.put(phoneId, previousQuantity + quantity);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void update(Map<Long, Long> items) throws OutOfStockException{
        Map<Long, Long> prevCart = new HashMap<>(cart.getItems());
        int itemNumberInCart = 0;
        for(Map.Entry<Long,Long> entry : items.entrySet()){
            if(!prevCart.containsKey(entry.getKey()) || !prevCart.get(entry.getKey()).equals(entry.getValue())){
                long phoneId = entry.getKey(), quantity = entry.getValue();
                int stockAmount = phoneDao.getPhoneStockAmount(phoneId);
                if(quantity > stockAmount) throw new OutOfStockException("Out of stock:"+itemNumberInCart);
                cart.getItems().put(phoneId,quantity);
            }
            itemNumberInCart++;
        }
    }

    @Override
    public void remove(Long phoneId) {
        cart.getItems().remove(phoneId);
    }

    @Override
    public boolean isEnoughStock(Long phoneId, Long quantity, int stockAmount){
        Long prevQuantity = cart.getItems().get(phoneId);
        if(prevQuantity == null) prevQuantity = 0L;
        return quantity + prevQuantity <= stockAmount;
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
        Map<Long,Long> items = new HashMap<>(cart.getItems());
        List<Phone> phoneList = phoneDao.getPhonesById(new ArrayList<>(items.keySet()));
        for(Phone phone : phoneList){
            price = price.add(phone.getPrice().multiply(new BigDecimal(items.get(phone.getId()))));
        }
        return price.intValue();
    }

    @Override
    public Map<Phone, Long> getPhoneMap() {
        Map<Phone, Long> phoneMap = new HashMap<>();
        Map<Long,Long> items = cart.getItems();
        List<Phone> phoneList = phoneDao.getPhonesById(new ArrayList<>(items.keySet()));
        for(Phone phone : phoneList){
            phoneMap.put(phone, items.get(phone.getId()));
        }
        return phoneMap;
    }
}

