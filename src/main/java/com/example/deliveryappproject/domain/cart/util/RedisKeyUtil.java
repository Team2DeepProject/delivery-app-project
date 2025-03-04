package com.example.deliveryappproject.domain.cart.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class RedisKeyUtil {

    public static final String ITEM_PREFIX = "items:";
    public static final String CART_PREFIX = "cart:users:";
    public static final String STORE_ID_KEY = "storeId";

    public static String getCartKey(Long userId) {
        return CART_PREFIX+ userId;
    }

    public static String getItemKey(Long itemId) {
        return ITEM_PREFIX + itemId;
    }

    public static Long extractItemId(String key) {
        return Long.parseLong(key.substring(ITEM_PREFIX.length()));
    }
}
