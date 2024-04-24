package com.test.product.controller;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.test.product.constant.ProductConstants;
import com.test.product.dto.ApprovalQueueDto;
import com.test.product.dto.ProductDto;
import com.test.product.dto.ResponseDto;
import com.test.product.dto.SearchRequest;
import com.test.product.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;

@RequestMapping("/api/products")
@AllArgsConstructor
public class ProductController {

	ProductService productService;
	//getProducts,searchProducts,createProduct,updateProduct,deleteProduct,approveProduct,rejectApprovalProduct
	
	//1.List Active Product
	@GetMapping("")
    public ResponseEntity<List<ProductDto>> getActiveProducts() {
        return ResponseEntity.status(HttpStatus.OK.value()).body(productService.getProducts());
    }
	
	
	//2.API search Products
	@PostMapping("/search")
    public ResponseEntity<Map<String, Object>> searchProducts(@Valid @RequestBody SearchRequest searchRequest) {
		return ResponseEntity.status(HttpStatus.OK.value()).body(productService.searchProducts(searchRequest));
    }
	
	
	//3.Create Product
	@PostMapping("")
    public ResponseEntity<ResponseDto> createProduct(@Valid @RequestBody ProductDto productDto) {
		if(productService.createProduct(productDto).equals("Success")) {
			return ResponseEntity
					.status(HttpStatus.CREATED)
					.body(new ResponseDto(ProductConstants.STATUS_201, ProductConstants.MESSAGE_201));
		}
		return null;
    }
	
	
	//4.updateProduct
	@PutMapping("")
    public ResponseEntity<ResponseDto> updateProduct(@Valid @RequestBody ProductDto productDto) {
        boolean isDeleted = productService.updateProduct(productDto);
        if(isDeleted) {
            return ResponseEntity
                    .status(HttpStatus.OK.value())
                    .body(new ResponseDto(ProductConstants.STATUS_200, ProductConstants.MESSAGE_200));
        }else{
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseDto(ProductConstants.STATUS_417, ProductConstants.MESSAGE_417_DELETE_PRODUCT));
        }
    }
	
	
	
	@Operation(
            summary = "Fetch Product Details REST API",
            description = "REST API to fetch Active Products"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "HTTP Status OK"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ResponseDto.class)
                    )
            )
    }
    )
	
	//5:delete Product
	@DeleteMapping("/delete")
    public ResponseEntity<ResponseDto> deleteProductById(Integer productId) {
        boolean isDeleted = productService.deleteProduct(productId);
        if(isDeleted) {
            return ResponseEntity
                    .status(HttpStatus.OK.value())
                    .body(new ResponseDto(ProductConstants.STATUS_200, ProductConstants.MESSAGE_200));
        }else{
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseDto(ProductConstants.STATUS_417, ProductConstants.MESSAGE_417_DELETE_PRODUCT));
        }
    }
	
	//6: get all Approval Queue
	@GetMapping("/approval-queue")
    public ResponseEntity<List<ApprovalQueueDto>> getApprovalQueueList() {
        return ResponseEntity.status(HttpStatus.OK.value()).body(productService.getApprovalQueueList());
    }
	
	
	//7.approve Product
	@PutMapping("/approval-queue/{approvalId}")
    public ResponseEntity<ResponseDto> approveProduct(@PathVariable(required = true,value="approvalId")Integer approvalId) {
        boolean isDeleted = productService.approveProduct(approvalId);
        if(isDeleted) {
            return ResponseEntity
                    .status(HttpStatus.OK.value())
                    .body(new ResponseDto(ProductConstants.STATUS_200, ProductConstants.MESSAGE_200));
        }else{
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseDto(ProductConstants.STATUS_417, ProductConstants.MESSAGE_417_DELETE_APPROVAL));
        }
    }
	
	//8.reject Approval Product
	@PutMapping("/approval-queue/{approvalId}/reject")
    public ResponseEntity<ResponseDto> rejectApprovalProduct(@PathVariable(required = true,value="approvalId")Integer approvalId) {
        boolean isDeleted = productService.rejectApprovalProduct(approvalId);
        if(isDeleted) {
            return ResponseEntity
                    .status(HttpStatus.OK.value())
                    .body(new ResponseDto(ProductConstants.STATUS_200, ProductConstants.MESSAGE_200));
        }else{
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseDto(ProductConstants.STATUS_417, ProductConstants.MESSAGE_417_DELETE_APPROVAL));
        }
    }
	
	
	
	
	
	
	
	

}
