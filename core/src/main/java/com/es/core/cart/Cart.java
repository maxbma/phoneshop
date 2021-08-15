package com.es.core.cart;

import java.util.HashMap;
import java.util.Map;

public class Cart {
    private Map<Long, Long> items = new HashMap<>();

    public Map<Long, Long> getItems() {
        return items;
    }

    public void setItems(Map<Long, Long> items) {
        this.items = items;
    }
}
