package com.es.phoneshop.web.controller.forms;

import com.es.core.model.order.QuickOrderItem;

import java.util.List;

public class QuickOrderForm {
    private List<QuickOrderItem> quickOrderItems;

    public List<QuickOrderItem> getQuickOrderItems() {
        return quickOrderItems;
    }

    public void setQuickOrderItems(List<QuickOrderItem> quickOrderItems) {
        this.quickOrderItems = quickOrderItems;
    }
}
