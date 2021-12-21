package com.iam.inventory;

import com.iam.product.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/*
This class as the name denoted is an expanded inventory which has the usual inventory variables but with detailed product variables
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpandedInventory {

    private UUID id;
    private Product product;
    private int quantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
