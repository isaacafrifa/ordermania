package com.iam.inventory;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iam.exception.ItemNotFound;

@Service
public class InventoryService {
	@Autowired
	private InventoryRepository inventoryRepository;
	private static final Logger LOGGER = LoggerFactory.getLogger(InventoryService.class);

	public Inventory findItemById(UUID id) {
		Optional<Inventory> optionalItem = this.inventoryRepository.findById(id);
		if (optionalItem.isPresent()) {
			LOGGER.info("INVENTORY ITEM [ID= " + id + "] FOUND");
			return optionalItem.get();
		}
		LOGGER.warn("INVENTORY ITEM [ID= " + id + "] NOT FOUND");
		throw new ItemNotFound();
	}

	public boolean productIdExists(String productId) {
		int stockCount = inventoryRepository.getInventoryCountbyProductId(productId);
		if (stockCount > 0) {
			return true;
		}
		return false;
	}
	
	public Inventory createInventoryItem(Inventory item) {
		return this.inventoryRepository.save(item); 
	}
	
	
	public boolean isNewProductId(String newProductId,String foundProductId) {
		if (newProductId.contentEquals(foundProductId))
			return false;
		return true;		
	}

}
