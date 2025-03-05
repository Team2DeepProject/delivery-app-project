package com.example.deliveryappproject.domain.review.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewUpdateRequest {

    private String content;
    private int rating;

    public ReviewUpdateRequest(String content, int rating) {
        this.content = content;
        this.rating = rating;
    }
}
