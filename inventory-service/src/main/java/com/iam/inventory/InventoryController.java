package com.iam.inventory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.iam.product.Product;
import com.iam.product.Products;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iam.exception.ItemNotFound;
import com.iam.exception.ProductIdAlreadyExists;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class InventoryController {

	private static final Logger LOGGER = LoggerFactory.getLogger(InventoryController.class);
	private final InventoryService inventoryService;
	public InventoryController(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}


	//Endpoint to check if product is in stock
	@GetMapping("/{productCode}")
	public Boolean isInStock(@PathVariable String productCode) {
		LOGGER.info("Checking inventory stock for product with productCode - " + productCode);
		return inventoryService.isInStock(productCode);
	}


	@GetMapping(value = "/inventories")
	public ExpandedInventoryList getAll() {
		LOGGER.info("GETTING ALL INVENTORY ITEMS");
		return inventoryService.getAllItems();
	}

	
	
	@GetMapping(value = "/inventories/{id}")
	public ResponseEntity<Object> getExpandedInventoryItem(@PathVariable(value = "id") String inventoryId){
		LOGGER.info("GETTING INVENTORY ITEM [ID= " + inventoryId + "]");
		ExpandedInventory foundItem = new ExpandedInventory();
		try {
		UUID idUUID = UUID.fromString(inventoryId.trim());
		 foundItem = inventoryService.findItemById(idUUID);
		// if (foundItem==null) throw new ItemNotFound(); //handled in Service
			return new ResponseEntity<>(foundItem, HttpStatus.OK);
		}
		catch (IllegalArgumentException e) {
			LOGGER.warn(e.getClass()+" WAS CAUGHT");
			LOGGER.warn("EXCEPTION MESSAGE: "+e.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	
	
	@PostMapping(value = "/inventories", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> post(@Valid @RequestBody Inventory inventory) {
		LOGGER.info("INITIATING CREATION OF INVENTORY ITEM [DETAILS= " + inventory + "]");

		if (inventoryService.productIdExists(inventory.getProductId())) {
			LOGGER.warn("INVENTORY ITEM WITH PRODUCT [ID= " + inventory.getProductId() + "] ALREADY EXISTS");
			throw new ProductIdAlreadyExists();
		}

		Inventory savedItem = inventoryService.createInventoryItem(inventory);
		if (savedItem == null) {
			LOGGER.warn("INVENTORY ITEM [DETAILS= " + inventory + "] NOT SAVED");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		LOGGER.info("INVENTORY ITEM [DETAILS= " + inventory + "] SAVED SUCCESSFULLY");
		return new ResponseEntity<>(savedItem, HttpStatus.CREATED);
	}

	
	//---will work on this later
	
//	@PutMapping(value = "/inventories/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
//	public ResponseEntity<Object> put(@Valid @RequestBody Inventory newItem,
//			@PathVariable(value = "id") String inventoryId) {
//		LOGGER.info("INITIATING UPDATE OF INVENTORY ITEM [ID= " + inventoryId + "]");
//
//		UUID idUUID = UUID.fromString(inventoryId.trim());
//		Inventory foundItem = inventoryService.findItemById(idUUID);
//		/*
//		 * We don't want inventoryItems with duplicate productIDs. So throw exception if
//		 * new productId already exists in system
//		 */
//		// Checking productId for inventory items other than the found Item
//		if (inventoryService.isNewProductId(newItem.getProductId(), foundItem.getProductId())) {
//
//			if (inventoryService.productIdExists(newItem.getProductId())) {
//				LOGGER.warn("INVENTORY ITEM HAS PRODUCT ID [" + newItem.getProductId() + "] WHICH ALREADY EXISTS AND CANT BE UPDATED");
//				throw new ProductIdAlreadyExists();
//			}
//		}
//
//		foundItem.setProductId(newItem.getProductId());
//		foundItem.setQuantity(newItem.getQuantity());
//
//		Inventory updatedItem = inventoryService.createInventoryItem(foundItem);
//		if (updatedItem == null) {
//			LOGGER.warn("INVENTORY ITEM [DETAILS= " + foundItem + "] NOT UPDATED");
//			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//		LOGGER.info("INVENTORY ITEM [ID= " + inventoryId + "]" + " UPDATED SUCCESSFULLY");
//		return ResponseEntity.ok(updatedItem);
//	}

}
