package com.example.deliveryappproject.domain.store.dto.request;

import com.example.deliveryappproject.domain.store.entity.Store;
import com.example.deliveryappproject.domain.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalTime;

@AllArgsConstructor
public class StoreCreateRequest {

    @NotBlank(message = "가게 이름은 필수 항목입니다.")
    @Size(max = 20, message = "가게 이름은 20자 이하로 작성해주세요.")
    private String storeName;

    @NotNull(message = "오픈 시간은 필수 항목입니다.")
    private LocalTime openAt;

    @NotNull(message = "닫는 시간은 필수 항목입니다.")
    private LocalTime closeAt;

    @NotNull(message = "최소 주문 금액은 필수 항목입니다.")
    private BigDecimal minOrderPrice;

    public Store toDto(User user) {
        return new Store(
                user,
                this.storeName,
                this.openAt,
                this.closeAt,
                this.minOrderPrice);
    }
}
