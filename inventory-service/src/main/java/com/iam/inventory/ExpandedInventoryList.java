package com.iam.inventory;

import lombok.Data;

import java.util.List;

/*
A class that subsumes a list of expandedInventory items
 */
@Data
public class ExpandedInventoryList {
	List<ExpandedInventory> expandedInventoryList;
}
