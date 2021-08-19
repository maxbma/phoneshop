package com.es.phoneshop.web.controller.forms;

import com.es.core.cart.CartItem;

import javax.validation.Valid;
import java.util.List;

public class CartUpdateForm {
    @Valid
    private List<CartItem> items;

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }
}
