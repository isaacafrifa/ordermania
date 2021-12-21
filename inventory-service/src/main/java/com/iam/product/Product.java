package com.iam.product;

/*
This class acts like a Feign class that mimics the Product class from the  product-service*
 */

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
public class Product {
    private UUID id;
    private String name;
    private BigDecimal price;
    private String description;
}
