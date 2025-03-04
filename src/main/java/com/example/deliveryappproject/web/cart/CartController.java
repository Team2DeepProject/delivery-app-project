package com.example.deliveryappproject.web.cart;

import com.example.deliveryappproject.common.annotation.Auth;
import com.example.deliveryappproject.common.dto.AuthUser;
import com.example.deliveryappproject.domain.cart.dto.CartItemsRequest;
import com.example.deliveryappproject.domain.cart.dto.CartItemsSaveResponse;
import com.example.deliveryappproject.domain.cart.dto.CartResponse;
import com.example.deliveryappproject.domain.cart.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;


    @PostMapping("/items")
    public ResponseEntity<CartItemsSaveResponse> addCartItems(@Auth AuthUser authUser, @RequestBody @Valid CartItemsRequest cartItemsRequest) throws InterruptedException {
        CartItemsSaveResponse cartItemsSaveResponse = cartService.addItems(authUser.getId(), cartItemsRequest);
        return ResponseEntity.ok(cartItemsSaveResponse);
    }

    @GetMapping
    public ResponseEntity<CartResponse> getCartItems(@Auth AuthUser authUser) {
        CartResponse cartResponse = cartService.getItems(authUser.getId());
        return ResponseEntity.ok(cartResponse);
    }

    @PatchMapping("/items/{itemId}/quantity")
    public ResponseEntity<Void> decreaseItemQuantity(@Auth AuthUser authUser, @PathVariable Long itemId, int quantity) {
        cartService.decreaseItemQuantity(authUser.getId(), itemId, quantity);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(@Auth AuthUser authUser) {
        cartService.clearCart(authUser.getId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> deleteCartItem(@Auth AuthUser authUser, @PathVariable Long itemId) {
        cartService.deleteCartItem(authUser.getId(), itemId);
        return ResponseEntity.noContent().build();
    }
}
