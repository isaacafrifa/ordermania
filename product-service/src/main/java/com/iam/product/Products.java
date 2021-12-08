package com.iam.product;


import java.util.List;

import lombok.Data;

/*
 * This class is used to hold list of Products
 * Should've named it ProductList but Products will suffice
 */

@Data
public class Products {

	List<Product> Products;
}
