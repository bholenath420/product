package com.test.product.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.test.product.dto.entity.ApprovalQueueEntity;

public interface ApprovalQueueRepo extends JpaRepository<ApprovalQueueEntity, Integer>{
	
	int deleteByApprovalQueId(Integer approvalId);

}
