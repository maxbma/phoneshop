package com.es.core.model.order;

import com.es.core.model.phone.Phone;

public class OrderItem {
    private Phone phone;
    private Long quantity;

    @Override
    public boolean equals(Object obj){
        if(this == obj) return true;
        if(obj == null || obj.getClass() != this.getClass()) return false;
        OrderItem item = (OrderItem)obj;
        return phone.equals(item.phone) && quantity.equals(item.quantity);
    }

    @Override
    public int hashCode(){
        return phone.hashCode() + quantity.hashCode();
    }

    public Phone getPhone() {
        return phone;
    }

    public void setPhone(final Phone phone) {
        this.phone = phone;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(final Long quantity) {
        this.quantity = quantity;
    }
}
