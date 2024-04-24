package com.test.product.dto.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name="approval_table")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalQueueEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="approval_Que_id")
	private Integer approvalQueId;
	
	@Column(name="product_id")
	private Integer productId;
	
	@Column(name="product_status")
	private boolean productStatus;
	
	@Column(name="approval_status")
	private boolean approvalStatus;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
	@Column(name = "created_date")
	private LocalDateTime createdDate;

	@Override
	public String toString() {
		return "ApprovalQueueEntity [approvalQueId=" + approvalQueId + ", productId=" + productId + ", productStatus="
				+ productStatus + ", approvalStatus=" + approvalStatus + ", createdDate=" + createdDate + "]";
	}

	
	
	

}
