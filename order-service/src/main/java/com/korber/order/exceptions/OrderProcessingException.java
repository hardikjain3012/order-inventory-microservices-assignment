package com.korber.order.exceptions;

public class OrderProcessingException extends RuntimeException {
    public OrderProcessingException(String message) {
        super(message);
    }
}
