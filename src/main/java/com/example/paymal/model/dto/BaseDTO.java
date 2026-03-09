package com.example.paymal.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public abstract class BaseDTO {
    private Integer id;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
