package com.iam.inventory;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

@SpringBootTest(webEnvironment =  WebEnvironment.RANDOM_PORT)
@DisplayName("Running Integration Tests for Inventory Controller")
class InventoryControllerIntegrationTest {

	@LocalServerPort
	private int port;
	
	@Autowired
	private TestRestTemplate testRestTemplate;
	
	private String getUrl() {
		return "http://localhost:"+port+"/api/v1/inventories";
	}
	
	@BeforeEach
	void setUp() throws Exception {
	}

	
//	@DisplayName("Running Get All Inventory Items")
//	@Test
//	void testForAllInventoryItems() {
//		InventoryList result = testRestTemplate.getForObject(getUrl(), InventoryList.class);
//		assertEquals(2, result.getInventoryList().size());	
//	}
	
	
	@DisplayName("Running Get Single Inventory Item")
	@Test
	void testForSingleInventoryItemUsingWrongPathVariable() {
		Inventory result = testRestTemplate.getForObject(getUrl()+"1L", Inventory.class);
		assertAll(
				()->assertNull(result.getId()),
				()->assertTrue(result.getQuantity()== 0)
				);
	}

}
