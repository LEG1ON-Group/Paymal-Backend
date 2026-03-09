package com.example.paymal.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Meta {
    private Integer page;
    private Integer size;
    private Long totalElements;
    private Integer totalPages;

    private String token;
    private String message;
    private Integer statusCode;
    private Boolean success;

    public static Meta of(int page, int size, long totalElements, int totalPages) {
        return Meta.builder()
                .page(page)
                .size(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .build();
    }
}