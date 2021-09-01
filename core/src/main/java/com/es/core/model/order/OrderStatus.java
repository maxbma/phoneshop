package com.es.core.model.order;

public enum OrderStatus {
    NEW(1), DELIVERED(2), REJECTED(3);

    private Integer statusId;

    OrderStatus(Integer statusId){
        this.statusId = statusId;
    }

    public Integer getStatusId() {
        return statusId;
    }
}
