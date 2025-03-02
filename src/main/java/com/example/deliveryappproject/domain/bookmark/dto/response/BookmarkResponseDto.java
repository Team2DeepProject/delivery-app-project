package com.example.deliveryappproject.domain.bookmark.dto.response;

import com.example.deliveryappproject.domain.bookmark.entity.Bookmark;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BookmarkResponseDto {

    private final Long storeId;
    private final String storeName;

    public static BookmarkResponseDto fromEntity(Bookmark bookmark) {
        return new BookmarkResponseDto(bookmark.getStore().getId(), bookmark.getStore().getName());
    }
}
