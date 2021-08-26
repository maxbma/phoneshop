package com.es.core.cart;

import java.math.BigDecimal;

public class CartTotal {
    private Long itemsAmount;
    private BigDecimal overallPrice;

    public BigDecimal getOverallPrice() {
        return overallPrice;
    }

    public void setOverallPrice(BigDecimal overallPrice) {
        this.overallPrice = overallPrice;
    }

    public Long getItemsAmount() {
        return itemsAmount;
    }

    public void setItemsAmount(Long itemsAmount) {
        this.itemsAmount = itemsAmount;
    }

}
