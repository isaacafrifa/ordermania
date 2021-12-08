package com.iam.product;

import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends CrudRepository<Product, UUID>{
	

	 @Query(value = "SELECT COUNT(ID) FROM product WHERE ID = ?1",nativeQuery =true)
	 int getProductCountbyProductId(UUID productId);
	 
}
