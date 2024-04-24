package com.test.product.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.test.product.dto.entity.ProductEntity;

@Repository
public interface ProductRepo extends JpaRepository<ProductEntity, Integer>{
	
	
	List<ProductEntity> findByStatus(boolean value);
	
	@Transactional
	@Modifying
	@Query("update ProductEntity as e set e.status=0 where e.productId=?1")
	int deleteByProductId(Integer productId);

	boolean deleteProuct(Integer productId);

	
	@Modifying
	@Query("update ProductEntity as e set e.status=1 where e.productId=?1")
	int activeByProductId(Integer productId);

	
	

}
