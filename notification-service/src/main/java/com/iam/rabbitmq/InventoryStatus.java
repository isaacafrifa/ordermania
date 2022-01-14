package com.iam.rabbitmq;


/*  This class's obj will be received from the broker */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryStatus implements Serializable {
    private Inventory Inventory;
    private Status status;
    private String message;
    private LocalDate messageDate;
}
