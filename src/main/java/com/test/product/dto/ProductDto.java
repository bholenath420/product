package com.test.product.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Schema(
        name = "Product",
        description = "Schema to hold product information"
)
public class ProductDto {
	
	private Integer productId;
	private String name;
	@NotEmpty(message = "Name can not be a null or empty")
    @Size(min = 1, max = 10000, message = "The price of the product must be between 1-10000")
	private Double price;
	private boolean status;

}
