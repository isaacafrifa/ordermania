package com.iam.inventory;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iam.exception.ItemNotFound;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Running Unit Test for Inventory Controller")
@ExtendWith(SpringExtension.class)
@WebMvcTest(InventoryController.class)
class InventoryControllerTest {

	@MockBean
	private InventoryService inventoryService;

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
    private ObjectMapper objectMapper;
	
	Inventory mockInventory;
	
	private String getRootUrl() {
		return "/api/v1/inventories";
	}
	
	private UUID getMockUUID() {
		return UUID.fromString("b499df00-1876-4c5f-b177-95eff6b3c512");
	}

	
	@BeforeEach
	void setUp() throws Exception {
		mockInventory= new Inventory(getMockUUID(), "P0001", 10, null, null);
	}
	
	
	@DisplayName("Checking for if no items have been saved")
	@Test
	void testGetAllEmpty() throws Exception {
		RequestBuilder requestBuilder= MockMvcRequestBuilders.get(getRootUrl());
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		assertEquals(0, result.getResponse().getContentLength());
		assertTrue(result.getResponse().getStatus()==200);
	}
	
	@Test
	void testGetAll() throws Exception {
		List<Inventory> items = Arrays.asList(mockInventory);
		InventoryList inventoryList= new InventoryList();
		inventoryList.setInventoryList(items);
		// given
		given(inventoryService.getAllItems()).willReturn(inventoryList);
		
		// when + then
				this.mockMvc.perform(get(getRootUrl()))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.inventoryList[0].id", is("b499df00-1876-4c5f-b177-95eff6b3c512")))
				.andExpect(content()
						.json("{'inventoryList':[{'id':'b499df00-1876-4c5f-b177-95eff6b3c512','productId':'P0001','quantity':10,'createdAt':null,'updatedAt':null}]}"));	
	}

	@Test
	void testGetInventoryItem() throws Exception {
		
		given(inventoryService.findItemById(getMockUUID())).willReturn(mockInventory);

		this.mockMvc.perform(get(getRootUrl() + "/"+getMockUUID())).andExpect(status().isOk())
				.andExpect(status().isOk())
				.andExpect(content()
						.json(objectMapper.writeValueAsString(mockInventory)));
	}

	/*
	 * This test works but I disabled it because I'm using the shortened approach
	 */
	@Disabled 
	@Test
	void testPostItems() throws Exception{
		
		given(inventoryService.createInventoryItem(any(Inventory.class))).willReturn(mockInventory);
		
	    String json = objectMapper.writeValueAsString(mockInventory);      
		RequestBuilder requestBuilder= MockMvcRequestBuilders.post(getRootUrl())
				.content(json)
			    .contentType(MediaType.APPLICATION_JSON)
			    .accept(MediaType.APPLICATION_JSON);
		
		MvcResult mvcResult= mockMvc.perform(requestBuilder).andReturn();
		MockHttpServletResponse response= mvcResult.getResponse();
		
		assertEquals(HttpStatus.CREATED.value(), response.getStatus());   
	}
	
	@Test
	void testPost() throws Exception{
		
			String json = objectMapper.writeValueAsString(mockInventory);      
	 
	        Mockito.when(inventoryService.createInventoryItem(Mockito.any(Inventory.class))).thenReturn(mockInventory);

	        mockMvc.perform(MockMvcRequestBuilders.post(getRootUrl())
	                .content(json)
	                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
	                .andExpect(status().isCreated())
	                .andExpect(content().contentType("application/json"))
	                .andExpect(MockMvcResultMatchers.jsonPath("$.quantity", is(10)))
	                .andExpect(MockMvcResultMatchers.jsonPath("$.productId", is("P0001")));
	   
	}

	@Test
	void testPut() throws Exception {
		Inventory updatedItem = new Inventory(mockInventory.getId(), "P1001", 5, null, null);
		
		when(inventoryService.findItemById(mockInventory.getId())).thenReturn(mockInventory);
		when(inventoryService.createInventoryItem(Mockito.any(Inventory.class))).thenReturn(updatedItem);

		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post(getRootUrl())
	            .contentType(MediaType.APPLICATION_JSON)
	            .accept(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(updatedItem));

	    mockMvc.perform(mockRequest)
	            .andExpect(status().is2xxSuccessful())
	            .andExpect(jsonPath("$", notNullValue()))
	            .andExpect(jsonPath("$.productId", is("P1001")));	    
	}
	
	@DisplayName("Checking for an inventory item's id is not a UUID")
	@Test
	public void testGetInventoryItem_whenParamNotUuid() throws Exception {
		MvcResult mvcResult = mockMvc.perform(get(getRootUrl() + "/test")).andReturn();
		assertEquals(400, mvcResult.getResponse().getStatus());		
//		.andExpect(result-> assertTrue(result.getResolvedException() instanceof IllegalArgumentException));
	}
	
	@DisplayName("Checking for an inventory item which doesn't exist")
	@Test
	public void testGetInventoryItem_whenItemNotFound() throws Exception {
		given(inventoryService.findItemById(mockInventory.getId())).willThrow(ItemNotFound.class);
		
		MvcResult mvcResult = mockMvc.perform(get(getRootUrl() + "/"+mockInventory.getId())).andReturn();
		assertEquals(404, mvcResult.getResponse().getStatus());
		assertTrue(mvcResult.getResponse().getContentAsString().isEmpty());
	}
	
}
