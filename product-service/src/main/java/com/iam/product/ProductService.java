package com.iam.product;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.iam.exception.ProductNotFound;

@Service
public class ProductService {

	private final ProductRepository productRepository;

	public ProductService(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);

	public Products getAllProducts() {
		List<Product> productList = new ArrayList<>();
		productRepository.findAll().forEach(product -> productList.add(product));
		Products products = new Products();
		products.setProducts(productList);
		return products;
	}

	public Product findProductById(UUID id) {

		Optional<Product> optionalProduct = productRepository.findById(id);
		if (optionalProduct.isPresent()) {
			LOGGER.info("PRODUCT [ID= " + id + "] FOUND");
			return optionalProduct.get();
		}
		LOGGER.warn("PRODUCT [ID= " + id + "] NOT FOUND");
		throw new ProductNotFound();
	}

	public Product createProduct(Product item) {
		return this.productRepository.save(item);
	}

	public boolean productIdExists(UUID productId) {
		int stockCount = productRepository.getProductCountbyProductId(productId);
		if (stockCount > 0) {
			return true;
		}
		return false;
	}

	public void deleteProduct(Product product) {
		this.productRepository.delete(product);
	}

}
