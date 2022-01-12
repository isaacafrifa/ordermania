package com.iam.inventory;

import java.time.LocalDate;
import java.util.UUID;
import com.iam.rabbitmq.InventoryStatus;
import com.iam.rabbitmq.RabbitMQSender;
import com.iam.rabbitmq.Status;
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
import com.iam.exception.ProductIdAlreadyExists;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class InventoryController {

    private static final Logger LOGGER = LoggerFactory.getLogger(InventoryController.class);
    private final InventoryService inventoryService;
    private final RabbitMQSender rabbitMQSender;

    public InventoryController(InventoryService inventoryService, RabbitMQSender sender) {
        this.inventoryService = inventoryService;
        this.rabbitMQSender = sender;
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
    public ResponseEntity<Object> getExpandedInventoryItem(@PathVariable(value = "id") String inventoryId) {
        LOGGER.info("GETTING INVENTORY ITEM [ID= " + inventoryId + "]");
        ExpandedInventory foundItem = new ExpandedInventory();
        try {
            UUID idUUID = UUID.fromString(inventoryId.trim());
            foundItem = inventoryService.findItemById(idUUID);
            // if (foundItem==null) throw new ItemNotFound(); //handled in Service
            return new ResponseEntity<>(foundItem, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            LOGGER.warn(e.getClass() + " WAS CAUGHT");
            LOGGER.warn("EXCEPTION MESSAGE: " + e.getMessage());
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
        //send Message to broker service
        InventoryStatus inventoryStatus = new InventoryStatus(savedItem, Status.CREATED, "Inventory Created", LocalDate.now());
        rabbitMQSender.send(inventoryStatus);
        LOGGER.info("INVENTORY ITEM [DETAILS= " + inventory + "] SAVED SUCCESSFULLY");
        return new ResponseEntity<>(savedItem, HttpStatus.CREATED);
    }


    @PutMapping(value = "/inventories/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> put(@Valid @RequestBody Inventory newItem,
                                      @PathVariable(value = "id") String inventoryId) {
        LOGGER.info("INITIATING UPDATE OF INVENTORY ITEM [ID= " + inventoryId + "]");

        UUID idUUID = UUID.fromString(inventoryId.trim());
        ExpandedInventory foundExpandedItem = inventoryService.findItemById(idUUID);
        //Get original inventory item from ExpandedInventory obj
        Inventory foundItem = new Inventory(foundExpandedItem.getId(), String.valueOf(foundExpandedItem.getProduct().getId()),
                foundExpandedItem.getQuantity(), foundExpandedItem.getCreatedAt(), foundExpandedItem.getUpdatedAt());
        foundItem.setProductId(newItem.getProductId());
        foundItem.setQuantity(newItem.getQuantity());

        Inventory updatedItem = inventoryService.createInventoryItem(foundItem);
        if (updatedItem == null) {
            LOGGER.warn("INVENTORY ITEM [DETAILS= " + foundItem + "] NOT UPDATED");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        //send Message to broker service
        InventoryStatus inventoryStatus = new InventoryStatus(updatedItem, Status.UPDATED, "Inventory Updated", LocalDate.now());
        rabbitMQSender.send(inventoryStatus);
        LOGGER.info("INVENTORY ITEM [ID= " + inventoryId + "]" + " UPDATED SUCCESSFULLY");
        return ResponseEntity.ok(updatedItem);
    }

}
