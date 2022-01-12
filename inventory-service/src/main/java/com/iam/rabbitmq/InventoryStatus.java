package com.iam.rabbitmq;

import com.iam.inventory.ExpandedInventory;
import com.iam.inventory.Inventory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;


/*  This class's obj will be sent to the broker */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryStatus implements Serializable {
    private Inventory Inventory;
    private Status status;
    private String message;
    private LocalDate messageDate;
}
