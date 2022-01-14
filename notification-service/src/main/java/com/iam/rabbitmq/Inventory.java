package com.iam.rabbitmq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {
    private UUID id;
    private String productId;
    private int quantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
