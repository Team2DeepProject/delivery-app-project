package com.example.deliveryappproject.domain.store.dto.request;

import com.example.deliveryappproject.domain.store.entity.Store;
import com.example.deliveryappproject.domain.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
public class StoreUpdateRequest {

    @Size(max = 20, message = "가게 이름은 20자 이하로 작성해주세요.")
    private String storeName;
    private LocalTime openAt;
    private LocalTime closeAt;
    private BigDecimal minOrderPrice;

}
