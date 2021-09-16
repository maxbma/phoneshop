package com.es.core.cart;

import com.es.core.model.order.QuickOrderItem;
import com.es.core.model.phone.JdbcPhoneDao;
import com.es.core.model.phone.Phone;
import com.es.core.model.stock.JdbcStockDao;
import com.es.core.model.stock.Stock;
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

    @Resource
    private JdbcStockDao stockDao;

    private final Cart cart = new Cart();

    @Override
    public Cart getCart() {
        return cart;
    }

    public HttpSessionCartService(){}
    public HttpSessionCartService(JdbcPhoneDao phoneDao, JdbcStockDao stockDao){
        this.phoneDao = phoneDao;
        this.stockDao = stockDao;
    }

    @Override
    public void addPhone(Long phoneId, Long quantity) throws OutOfStockException {
        int stockAmount = stockDao.getPhoneStockAmount(phoneId);
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
        List<Stock> stocks = stockDao.getStockList(idList);
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
    public void quickAdd(List<QuickOrderItem> items) throws OutOfStockException{
        Map<Long, Long> toPut = new HashMap<>();
        Map<Long, QuickOrderItem> itemMap = new LinkedHashMap<>();
        List<Stock> stocks = new ArrayList<>();
        List<Integer> itemNumbersWithException = new ArrayList<>();
        for(QuickOrderItem item : items){
            if(item.getQuantity() == null && item.getPhoneModel().isBlank()) continue;
            stocks.add(stockDao.getStockByModel(item.getPhoneModel()));
        }
        Map<Long, Stock> stockMap = new HashMap<>(stocks.size());
        stocks.forEach(stock -> stockMap.put(stock.getPhoneId(), stock));
        List<Phone> phoneList = phoneDao.getPhonesById(new ArrayList<>(stockMap.keySet()));
        phoneList.forEach(phone -> itemMap.put(phone.getId(), items.stream().filter(p -> p.getPhoneModel().equals(phone.getModel())).findFirst().get()));
        lock.lock();
        try{
            Map<Long, Long> prevCart = cart.getItems();
            int itemNumberInCart = 0;
            for(Map.Entry<Long, QuickOrderItem> entry : itemMap.entrySet()){
                long phoneId = entry.getKey();
                long quantity = entry.getValue().getQuantity();
                int stockAmount = stockMap.get(entry.getKey()).getStock() - stockMap.get(entry.getKey()).getReserved();
                if(!isEnoughStock(phoneId, quantity, stockAmount)){
                    itemNumbersWithException.add(itemNumberInCart);
                    itemNumberInCart++;
                    continue;
                }
                Long previousQuantity = prevCart.get(phoneId);
                if(previousQuantity == null) previousQuantity = 0L;
                toPut.put(phoneId, previousQuantity + quantity);
                items.remove(entry.getValue());
            }
            prevCart.putAll(toPut);
            if(itemNumbersWithException.size() != 0) throw new OutOfStockException(itemNumbersWithException, "Out of stock");
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void updateStocks(Map<Long,Long> items) {
        stockDao.updateStocks(items);
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
    public void cleanCart(Map<Long,Long> items) {
        lock.lock();
        try{
            for(Map.Entry<Long,Long> entry : items.entrySet()){
                long left = cart.getItems().get(entry.getKey()) - entry.getValue();
                if(left <= 0){
                    cart.getItems().remove(entry.getKey());
                } else{
                    cart.getItems().replace(entry.getKey(), left);
                }
            }
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

