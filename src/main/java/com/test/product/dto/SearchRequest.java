package com.test.product.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequest {
	
	private String productName;
	private Double minPrice;
	private Double maxPrice;
	private LocalDateTime minPostedDate;
	private LocalDateTime maxPostedDate;
	private Integer page;
	private Integer size;

}
