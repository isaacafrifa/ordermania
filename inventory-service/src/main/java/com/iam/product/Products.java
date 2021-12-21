package com.iam.product;

import lombok.Data;

import java.util.List;

/*
This class acts like a Feign class that mimics the List of Product class from the product-service
This class can be seen by ProductList class
 */
@Data
public class Products {
    private List<Product> products;
}
