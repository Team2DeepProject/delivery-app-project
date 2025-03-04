package com.example.deliveryappproject.common.exception;

public class NotFoundException extends CustomException {
    public NotFoundException() {
        super(ErrorCode.NOT_FOUND);
    }

    public NotFoundException(String message) {
        super(ErrorCode.NOT_FOUND, message);
    }
}

