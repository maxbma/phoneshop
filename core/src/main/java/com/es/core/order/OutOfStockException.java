package com.es.core.order;

public class OutOfStockException extends Exception {
    private static final long serialVersionUID = -2795804103099775537L;

    public OutOfStockException() {
        super();
    }

    public OutOfStockException(String message) {
        super(message);
    }

    public OutOfStockException(Exception e) {
        super(e);
    }

    public OutOfStockException(String message, Exception e) {
        super(message, e);
    }
}
