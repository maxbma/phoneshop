package com.es.core.cart;

import com.es.core.model.phone.JdbcPhoneDao;
import com.es.core.model.phone.Phone;
import com.es.core.model.phone.Stock;
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
    public void update(List<CartItem> items) throws OutOfStockException{
        Map<Long, Long> toPut = new HashMap<>();
        List<Integer> itemNumbersWithException = new ArrayList<>();
        List<Long> idList = new ArrayList<>();
        for(CartItem item : items){
            idList.add(item.getPhoneId());
        }
        List<Stock> stocks = phoneDao.getStockList(idList);
        Map<Long, Stock> stockMap = new HashMap<>(stocks.size());
        stocks.forEach(stock -> stockMap.put(stock.getPhoneId(), stock));
        lock.lock();
        try{
            Map<Long, Long> prevCart = cart.getItems();
            int itemNumberInCart = 0;
            for(CartItem item : items){
                if(!prevCart.containsKey(item.getPhoneId()) || !prevCart.get(item.getPhoneId()).equals(item.getQuantity())){
                    long phoneId = item.getPhoneId(), quantity = item.getQuantity();
                    Stock stock = stockMap.get(phoneId);
                    int stockAmount = stock.getStock() - stock.getReserved();
                    if(quantity > stockAmount) {
                        itemNumbersWithException.add(itemNumberInCart);
                        itemNumberInCart++;
                        continue;
                    }
                    toPut.put(phoneId, quantity);
                }
                itemNumberInCart++;
            }
            if(itemNumbersWithException.size() != 0) throw new OutOfStockException(itemNumbersWithException, "Out of stock");
            prevCart.putAll(toPut);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void remove(Long phoneId) {
        lock.lock();
        try{
            cart.getItems().remove(phoneId);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean isEnoughStock(Long phoneId, Long quantity, int stockAmount){
        Long prevQuantity = cart.getItems().get(phoneId);
        if(prevQuantity == null) prevQuantity = 0L;
        return quantity + prevQuantity <= stockAmount;
    }

    @Override
    public CartTotal getCartTotal(Map<Long,Long> items){
        CartTotal cartTotal = new CartTotal();
        long itemsAmount = 0;
        BigDecimal overallPrice = new BigDecimal(0);
        List<Phone> phoneList = phoneDao.getPhonesById(new ArrayList<>(items.keySet()));
        for(Phone phone : phoneList){
            itemsAmount += items.get(phone.getId());
            overallPrice = overallPrice.add(phone.getPrice().multiply(new BigDecimal(items.get(phone.getId()))));
        }
        cartTotal.setItemsAmount(itemsAmount);
        cartTotal.setOverallPrice(overallPrice);
        return cartTotal;
    }

    @Override
    public Map<Phone, Long> getPhoneMap(Map<Long,Long> items) {
        Map<Phone, Long> phoneMap = new HashMap<>();
        List<Phone> phoneList = phoneDao.getPhonesById(new ArrayList<>(items.keySet()));
        for(Phone phone : phoneList){
            phoneMap.put(phone, items.get(phone.getId()));
        }
        return phoneMap;
    }
}

