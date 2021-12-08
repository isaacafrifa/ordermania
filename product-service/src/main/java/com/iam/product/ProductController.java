package com.iam.product;

import java.util.UUID;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iam.exception.ProductAlreadyExists;

@RestController
@RequestMapping("/api/v1")
public class ProductController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);
	
	private final ProductService productService;
	
	public ProductController(ProductService productService) {
		this.productService= productService;
	}
	
	
	@GetMapping(value = "/products")
	public Products getAll() {
		LOGGER.info("GETTING ALL PRODUCTS");
		return productService.getAllProducts();
	}
	
	
	

	@GetMapping(value = "/products/{id}")
	public ResponseEntity<Object> getInventoryItem(@PathVariable(value = "id") String productId){
		LOGGER.info("GETTING PRODUCT ITEM [ID= " + productId + "]");
		Product foundProduct = new Product();
		try {
		UUID idUUID = UUID.fromString(productId.trim());
		 foundProduct = productService.findProductById(idUUID);
		// if (foundProduct==null) throw new ProductNotFound(); //handled in Service
			return new ResponseEntity<>(foundProduct, HttpStatus.OK);
		}
		catch (IllegalArgumentException e) {
			LOGGER.warn(e.getClass()+" WAS CAUGHT");
			LOGGER.warn("EXCEPTION MESSAGE: "+e.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	
	
	@PostMapping(value = "/products", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> post(@Valid @RequestBody Product product) {
		LOGGER.info("INITIATING CREATION OF PRODUCT [DETAILS= " + product + "]");

		if (productService.productIdExists(product.getId())) {
			LOGGER.warn("PRODUCT WITH ID [ " + product.getId()+ " ] ALREADY EXISTS");
			throw new ProductAlreadyExists();
		}
		
		Product savedProduct = productService.createProduct(product);
		if (savedProduct == null) {
			LOGGER.warn("PRODUCT [DETAILS= " + product + "] NOT SAVED");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		LOGGER.info("PRODUCT [DETAILS= " + product + "] SAVED SUCCESSFULLY");
		return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
	}

	
	
	@PutMapping(value = "/products/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> put(@Valid @RequestBody Product newProduct,
			@PathVariable(value = "id") String productId) {
		LOGGER.info("INITIATING UPDATE OF PRODUCT [ID= " + productId + "]");

		try {
			UUID idUUID = UUID.fromString(productId.trim());
			Product foundProduct = productService.findProductById(idUUID);
			foundProduct.setName(newProduct.getName());
			foundProduct.setPrice(newProduct.getPrice());
			foundProduct.setDescription(newProduct.getDescription());

			Product updatedItem = productService.createProduct(foundProduct);
			if (updatedItem == null) {
				LOGGER.warn("PRODUCT [DETAILS= " + foundProduct + "] NOT UPDATED");
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
			LOGGER.info("PRODUCT [ID= " + productId + "]" + " UPDATED SUCCESSFULLY");
			return ResponseEntity.ok(updatedItem);
		}

		catch (IllegalArgumentException e) {
			LOGGER.warn(e.getClass() + " WAS CAUGHT");
			LOGGER.warn("EXCEPTION MESSAGE: " + e.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

	}
	
	
	 @DeleteMapping(value="/products/{id}")
	  public ResponseEntity<Object> delete(@PathVariable(value = "id") String productId){
			LOGGER.info("INITIATING DELETION OF PRODUCT [ID= " + productId + "]");
			try {
				UUID idUUID = UUID.fromString(productId.trim());
			Product foundProduct = productService.findProductById(idUUID);
			productService.deleteProduct(foundProduct);
			LOGGER.info("PRODUCT [ID= " + productId + "]" + " DELETED SUCCESSFULLY");
			return new ResponseEntity<>(foundProduct, HttpStatus.OK);
			}
			catch (IllegalArgumentException e) {
				LOGGER.warn(e.getClass() + " WAS CAUGHT");
				LOGGER.warn("EXCEPTION MESSAGE: " + e.getMessage());
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}

	  }
	


}
