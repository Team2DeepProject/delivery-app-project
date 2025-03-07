package com.example.deliveryappproject.domain.storenotice.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class NoticeResponseDto {

    private final Long id;
    private final String title;
    private final String contents;
}
