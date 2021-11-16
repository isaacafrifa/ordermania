package com.iam.inventory;

import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface InventoryRepository extends CrudRepository<Inventory, UUID>{
	
	
	 @Query(value = "SELECT COUNT(PRODUCT_ID) FROM inventory WHERE PRODUCT_ID = ?1",nativeQuery =true)
	 int getInventoryCountbyProductId(String productId);

}
