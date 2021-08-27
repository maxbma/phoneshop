package com.es.core.order;

import java.util.ArrayList;
import java.util.List;

public class OutOfStockException extends Exception {
    private static final long serialVersionUID = -2795804103099775537L;
    private List<Integer> itemNumbersList = new ArrayList<>();
    private List<Long> phoneIdList = new ArrayList<>();

    public OutOfStockException(String message) {
        super(message);
    }
    public OutOfStockException(List<Integer> itemNumbersList, String message){
        super(message);
        this.itemNumbersList = itemNumbersList;
    }

    public OutOfStockException(List<Long> phoneIdList){
        this.phoneIdList = phoneIdList;
    }

    public List<Integer> getItemNumbersList() {
        return itemNumbersList;
    }

    public List<Long> getPhoneIdList() {
        return phoneIdList;
    }
}
