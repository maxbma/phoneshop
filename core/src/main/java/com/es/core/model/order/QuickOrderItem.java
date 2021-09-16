package com.es.core.model.order;

public class QuickOrderItem {
    private String phoneModel;
    private Long quantity;

    @Override
    public int hashCode(){
        return phoneModel.hashCode() + 31*quantity.intValue();
    }

    @Override
    public boolean equals(Object obj){
        if(this == obj)return true;
        if(obj == null || this.getClass()!=obj.getClass())return false;
        QuickOrderItem item = (QuickOrderItem) obj;
        return this.quantity == item.getQuantity() &&
                this.phoneModel.equals(item.getPhoneModel());
    }

    public String getPhoneModel() {
        return phoneModel;
    }

    public void setPhoneModel(String phoneModel) {
        this.phoneModel = phoneModel;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }
}
