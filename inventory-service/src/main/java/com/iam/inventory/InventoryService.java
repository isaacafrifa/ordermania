package com.iam.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.iam.product.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iam.exception.ItemNotFound;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class InventoryService {

    private InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(InventoryService.class);
    @Autowired
    WebClient webClient; //@Bean is located in MyBeans class

//	public InventoryList getAllItems(){
//		 List<Inventory> inventoryList = new ArrayList<>();
//			inventoryRepository.findAll().forEach(inventory -> inventoryList.add(inventory));
//			InventoryList inventories = new InventoryList();
//			inventories.setInventoryList(inventoryList);
//			return inventories;
//		}

    public ExpandedInventoryList getAllItems() {
        List<Inventory> inventoryList = new ArrayList<>();
        inventoryRepository.findAll().forEach(inventory -> inventoryList.add(inventory));
        // use Inventory item's productID to get the product from the product-service
        List<ExpandedInventory> refinedList = inventoryList.stream()
                .map(inventory -> {
                    Product responseProduct = this.webClient.get().uri("/" + inventory.getProductId())
                            .retrieve()
                            .bodyToMono(Product.class).block();
                    // create expandedInventory object with inventory details and product
                    ExpandedInventory expandedInventory = new ExpandedInventory(
                            inventory.getId(),responseProduct,
                            inventory.getQuantity(),inventory.getCreatedAt(),inventory.getUpdatedAt()
                    );
                    return expandedInventory;
                })
                .collect(Collectors.toList());
        ExpandedInventoryList inventories = new ExpandedInventoryList();
        inventories.setExpandedInventoryList(refinedList);
        return inventories;
    }


    public ExpandedInventory findItemById(UUID id) {
        Optional<Inventory> optionalItem = this.inventoryRepository.findById(id);
        if (optionalItem.isPresent()) {
            LOGGER.info("INVENTORY ITEM [ID= " + id + "] FOUND");
            //Get Inventory obj from Optional
            Inventory foundItem = optionalItem.get();
            // Use Inventory item's productID to get the product details from the product-service
            UUID inventoryProductId = UUID.fromString(foundItem.getProductId());
            Product product = this.webClient
                    .get()
                    .uri("/" + inventoryProductId)
                    .retrieve()
                    .bodyToMono(Product.class)
                    .block();
            // create expandedInventory object with inventory details and product
            ExpandedInventory expandedInventory = new ExpandedInventory(foundItem.getId(),
                    product,
                    foundItem.getQuantity(),foundItem.getCreatedAt(),foundItem.getUpdatedAt());
            LOGGER.info("Expanded Inventory: " + expandedInventory);
            return expandedInventory;
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


    public boolean isNewProductId(String newProductId, String foundProductId) {
        if (newProductId.contentEquals(foundProductId))
            return false;
        return true;
    }

}
