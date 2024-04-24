package com.test.product.service;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import com.test.product.dto.ApprovalQueueDto;
import com.test.product.dto.ProductDto;
import com.test.product.dto.SearchRequest;

public interface ProductService {

	
	List<ProductDto> getProducts();
	
	String createProduct(ProductDto product);
	
	boolean deleteProduct(Integer productId);

	Object searchProducts(@Valid ProductDto productDto);

	boolean updateProduct(ProductDto productDto);

	boolean approveProduct(Integer approvalId);

	boolean rejectApprovalProduct(Integer approvalId);

	List<ApprovalQueueDto> getApprovalQueueList();

	Map<String, Object> searchProducts(SearchRequest searchRequest);
	
	
}
