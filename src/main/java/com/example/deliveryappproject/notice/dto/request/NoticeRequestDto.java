package com.example.deliveryappproject.notice.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class NoticeRequestDto {

    private final String title;
    private final String contents;
}
