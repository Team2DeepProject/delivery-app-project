package com.example.deliveryappproject.notice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NoticeResponseDto {

    private Long id;
    private String title;
    private String contents;
}
