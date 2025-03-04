package com.example.deliveryappproject.common.exception;

public class BadRequestException extends CustomException {


    public BadRequestException() {
        super(ErrorCode.BAD_REQUEST);
    }

    public BadRequestException(String message) {
        super(ErrorCode.BAD_REQUEST, message);
    }
}
