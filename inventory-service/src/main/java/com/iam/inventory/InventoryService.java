package com.iam.inventory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.iam.product.Product;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iam.exception.ItemNotFound;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private static final String RESILIENCE4J_INSTANCE_NAME = "ProductServiceCB";
    private static final String FALLBACK_PRODUCT_ID="e0848fa6-8207-43f9-91df-ef5bfb4df206";

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(InventoryService.class);
    @Autowired
    WebClient webClient; //@Bean is located in MyBeans class


    @CircuitBreaker(name = RESILIENCE4J_INSTANCE_NAME, fallbackMethod = "getAllItemsFallback")
    public ExpandedInventoryList getAllItems() {
        List<Inventory> inventoryList = new ArrayList<>();
        inventoryRepository.findAll().forEach(inventory -> inventoryList.add(inventory));
        //change from list of Inventory to list of ExpandedInventory
        List<ExpandedInventory> refinedList = inventoryList.stream()
                .map(inventory -> {
                    // use Inventory item's productID to get the product from the product-service
                    UUID inventoryProductId = UUID.fromString(inventory.getProductId());
                    Product responseProduct = getProductFromProductService(inventoryProductId);
                    // create and return expandedInventory object with inventory details and product
                    ExpandedInventory expandedInventory= new ExpandedInventory(
                            inventory.getId(), responseProduct,
                            inventory.getQuantity(), inventory.getCreatedAt(), inventory.getUpdatedAt());
                    return expandedInventory;
                })
                .collect(Collectors.toList());
        ExpandedInventoryList inventories = new ExpandedInventoryList();
        inventories.setExpandedInventoryList(refinedList);
        return inventories;
    }

    @CircuitBreaker(name = RESILIENCE4J_INSTANCE_NAME, fallbackMethod = "findItemByIdFallback")
    public ExpandedInventory findItemById(UUID id) {
        Optional<Inventory> optionalItem = this.inventoryRepository.findById(id);
        if (optionalItem.isPresent()) {
            LOGGER.info("INVENTORY ITEM [ID= " + id + "] FOUND");
            //Get Inventory obj from Optional
            Inventory foundItem = optionalItem.get();
            // Use Inventory item's productID to get the product details from the product-service
            UUID inventoryProductId = UUID.fromString(foundItem.getProductId());
            Product product = getProductFromProductService(inventoryProductId);
            // create expandedInventory object with inventory details and product
            ExpandedInventory expandedInventory = new ExpandedInventory(foundItem.getId(),
                    product,
                    foundItem.getQuantity(), foundItem.getCreatedAt(), foundItem.getUpdatedAt());
            LOGGER.info("Expanded Inventory: " + expandedInventory);
            return expandedInventory;
        }
        LOGGER.warn("INVENTORY ITEM [ID= " + id + "] NOT FOUND");
        throw new ItemNotFound();
    }

    public boolean productIdExists(String productId) {
        int count = inventoryRepository.getInventoryCountbyProductId(productId);
        return count > 0;
    }

    public Inventory createInventoryItem(Inventory item) {
        return this.inventoryRepository.save(item);
    }

    public void deleteInventoryItem(UUID itemId){
        this.inventoryRepository.deleteById(itemId);
    }


    public boolean isNewProductId(String newProductId, String foundProductId) {
        if (newProductId.contentEquals(foundProductId))
            return false;
        return true;
    }

    //Check if product is in stock
    public Boolean isInStock(String productId) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> {
                            LOGGER.warn("Cannot Find Inventory Item with productId [" + productId + " ]");
                            return new ItemNotFound("Inventory item not found");
                        }
                );
        return inventory.getQuantity() > 0;
    }

    //Remote call to Product Service
    public Product getProductFromProductService(UUID productId) {
        Product product= this.webClient
                .get()
                .uri("/" + productId)
                .retrieve()
                .bodyToMono(Product.class)
                .block();
        return product;
    }

    /* ----- FALLBACK METHODS ----- */

    //Fallback for getAllItems
    public ExpandedInventoryList getAllItemsFallback(Exception e) {
        LOGGER.warn("-- GET ALL INVENTORY ITEMS FALLBACK EXECUTED--");
        List<Inventory> inventoryList = new ArrayList<>();
        inventoryRepository.findAll().forEach(inventoryList::add);
        //change from list of Inventory to list of ExpandedInventory
        List<ExpandedInventory> refinedList = inventoryList.stream()
                .map(inventory -> {
                    // create fallbackProduct to be used in place of actual Product from product-service
                    Product fallbackProduct = new Product(UUID.fromString(FALLBACK_PRODUCT_ID), "fallback productName", BigDecimal.ZERO, "fallback description");
                    // create and return expandedInventory object with inventory details and fallback product
                    ExpandedInventory expandedInventory= new ExpandedInventory(
                            inventory.getId(), fallbackProduct,
                            inventory.getQuantity(), inventory.getCreatedAt(), inventory.getUpdatedAt());
                    return expandedInventory;
                })
                .collect(Collectors.toList());
        ExpandedInventoryList inventories = new ExpandedInventoryList();
        inventories.setExpandedInventoryList(refinedList);
        return inventories;
    }

    //Fallback for findItemById
    public ExpandedInventory findItemByIdFallback(UUID id, Exception exception) {
        LOGGER.warn("-- GET INVENTORY ITEM BY ID FALLBACK EXECUTED--");
        Optional<Inventory> optionalItem = this.inventoryRepository.findById(id);
        if (optionalItem.isPresent()) {
            //Get Inventory obj from Optional
            Inventory foundItem = optionalItem.get();
            // create fallbackProduct to be used in place of actual Product from product-service
            Product fallbackProduct = new Product(UUID.fromString(FALLBACK_PRODUCT_ID), "fallback productName", BigDecimal.ZERO, "fallback description");
            // create and return expandedInventory object with inventory details and product
            ExpandedInventory expandedInventory = new ExpandedInventory(foundItem.getId(), fallbackProduct,
                    foundItem.getQuantity(), foundItem.getCreatedAt(), foundItem.getUpdatedAt());
            LOGGER.info("Expanded Inventory: " + expandedInventory);
            return expandedInventory;
        }
        throw new ItemNotFound();
    }

}
