package com.example.deliveryappproject.common.response;

import lombok.Getter;

@Getter
public class Response<T> {

    private final boolean success;
    private final String message;
    private final T data;

    private Response(boolean success, T data, String message) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public static <T> Response<T> of(T data, String message) {
        return new Response<>(true, data, message);
    }

    public static <T> Response<T> error(String message) {
        return new Response<>(false, null, message);
    }

    public static <T> Response<T> empty(String message) {
        return new Response<>(true, null, message);
    }
}
