package com.test.product.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.test.product.constant.ProductConstants;
import com.test.product.dto.ApprovalQueueDto;
import com.test.product.dto.ProductDto;
import com.test.product.dto.SearchRequest;
import com.test.product.dto.entity.ApprovalQueueEntity;
import com.test.product.dto.entity.ProductEntity;
import com.test.product.exception.CustomException;
import com.test.product.exception.ResourceNotFoundException;
import com.test.product.repo.ApprovalQueueRepo;
import com.test.product.repo.ProductRepo;
import com.test.product.service.ProductService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

	private ProductRepo productRepo;
	private ApprovalQueueRepo approvalQueueRepo;
	private EntityManager entityManager;

	@Override
	public List<ProductDto> getProducts() {
		log.info("getProduct method called");
		List<ProductEntity> productes = productRepo.findByStatus(true);
		if (productes != null) {
			return productes.stream()
					.map(prd -> new ProductDto(prd.getProductId(), prd.getName(), prd.getPrice(), prd.isStatus()))
					.collect(Collectors.toList());
		} else {
			log.info(ProductConstants.NO_DATA);
			return null;
		}
	}

	@Override
	public Map<String, Object> searchProducts(SearchRequest searchRequest) {
		
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<ProductEntity> query = cb.createQuery(ProductEntity.class);
		Root<ProductEntity> rootCust = null;
		
			rootCust = query.from(ProductEntity.class);
			List<Predicate> predicates = new ArrayList<>();
			
			if (!searchRequest.getProductName().isBlank()) {
				predicates.add(cb.equal(rootCust.get("name"), searchRequest.getProductName()));
			}
			if (searchRequest.getMinPrice() != null && searchRequest.getMaxPrice()!=null) {
				predicates.add(cb.between(rootCust.get("price"), searchRequest.getMinPrice(), searchRequest.getMaxPrice()));
				
			}
			if (searchRequest.getMinPostedDate() != null && searchRequest.getMaxPostedDate()!=null) {
				predicates.add(cb.between(rootCust.get("createdDate"),searchRequest.getMinPostedDate(),searchRequest.getMaxPostedDate()));
			}
			query.select(rootCust).where(predicates.toArray(new Predicate[predicates.size()]));

							query.orderBy(cb.desc(rootCust.get("createdDate")));
			try {
			TypedQuery<ProductEntity> typedQuery = entityManager.createQuery(query);
			typedQuery.setFirstResult((searchRequest.getPage() - 1) * searchRequest.getSize());
			typedQuery.setMaxResults(searchRequest.getSize());

			Map<String, Object> obj = new HashMap<>();

			List<ProductEntity> resultList = typedQuery.getResultList();
			obj.put("data", resultList);
			obj.put("count", count(predicates));
			log.debug("count method executed");
			return obj;
		} catch (Exception e) {
			log.debug(e.getMessage());
			throw new CustomException(ProductConstants.SEARCH_ERROR);
		}
	}
	
	private Long count(List<Predicate> predicates) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<ProductEntity> rootCust = countQuery.from(ProductEntity.class);
		countQuery.select(cb.count(rootCust));
		countQuery.where(predicates.toArray(new Predicate[predicates.size()]));
		return entityManager.createQuery(countQuery).getSingleResult();
	}

	@Override
	public String createProduct(ProductDto product) {
		ProductEntity productObj = new ProductEntity();
		productObj.setName(product.getName());
		productObj.setPrice(product.getPrice());
		productObj.setStatus(product.isStatus());
		try {
			productObj = productRepo.save(productObj);
			if(productObj.getPrice()>5000) {
				saveApprovalQueue(productObj.getProductId());
			}
		} catch (Exception e) {
			log.debug(e.getMessage());
		}
		return ProductConstants.MESSAGE_201;
	}

	@Override
	public boolean deleteProduct(Integer productId) {
		int updated = 0;
		ProductEntity product = productRepo.findById(productId)
				.orElseThrow(() -> new ResourceNotFoundException(ProductConstants.NO_DATA + productId));

		try {
			updated = productRepo.deleteByProductId(productId);
		} catch (Exception e) {
			log.debug(e.getMessage());
			throw new CustomException(ProductConstants.MESSAGE_417_DELETE_PRODUCT);
		}
		if (updated > 0) {
			// calling saveApprovalQueue for saving the Approval Queue table
			saveApprovalQueue(productId);
		}
		return true;
	}

	@Override
	public boolean updateProduct(ProductDto productDto) {
		boolean isUpdated = false;
		ProductEntity productEntity = productRepo.findById(productDto.getProductId())
				.orElseThrow(() -> new ResourceNotFoundException(ProductConstants.NO_DATA));

		Double newprice = productDto.getPrice();
		Double oldPrice = productEntity.getPrice();
		Double priceOfPercentage = oldPrice + (oldPrice * 0.5);
		if (newprice > priceOfPercentage) {
			// calling saveApprovalQueue for saving the Approval Queue table
			saveApprovalQueue(productEntity.getProductId());
		}
		try {
			BeanUtils.copyProperties(productDto, productEntity);
			productRepo.save(productEntity);
		} catch (Exception e) {
			log.debug(e.getMessage());
			throw new CustomException(ProductConstants.PRODUCT_SAVING_ERROR);
		}
		isUpdated = true;
		return isUpdated;
	}

	@Override
	public Object searchProducts(@Valid ProductDto productDto) {

		return null;
	}

	private String saveApprovalQueue(Integer productId) {
		log.info("saveApprovalQueue method called");
		String msg = "Success";
		ApprovalQueueEntity approvalQueueEntity = new ApprovalQueueEntity();
		approvalQueueEntity.setApprovalStatus(false);
		approvalQueueEntity.setCreatedDate(LocalDateTime.now());
		approvalQueueEntity.setProductId(productId);
		approvalQueueEntity.setProductStatus(false);
		try {
			approvalQueueRepo.save(approvalQueueEntity);
		} catch (Exception e) {
			throw new CustomException(ProductConstants.APPROVAL_SAVING_ERROR);
		}
		return msg;

	}

	@Override
	public boolean approveProduct(Integer approvalId) {
		
		boolean isUpdated = false;
		int updated=0;
		ApprovalQueueEntity approvalQueueObj = approvalQueueRepo.findById(approvalId)
				.orElseThrow(() -> new ResourceNotFoundException(ProductConstants.NO_DATA));

		if(approvalQueueObj!=null) {
			
			try {
				updated = productRepo.activeByProductId(approvalQueueObj.getProductId());
				if(updated>0) {
					try {
						approvalQueueRepo.deleteById(approvalId);
					}catch (Exception e) {
						throw new CustomException(ProductConstants.MESSAGE_417_DELETE_APPROVAL);
					}
				}
			} catch (Exception e) {
				log.debug(e.getMessage());
				throw new CustomException(ProductConstants.MESSAGE_417_DELETE_APPROVAL);
			}
		}
		isUpdated = true;
		return isUpdated;
	}

	@Override
	public List<ApprovalQueueDto> getApprovalQueueList() {
		
		log.info("getApprovalQueue method called");
		List<ApprovalQueueEntity> approvalQueList = approvalQueueRepo.findAll();
		if (!approvalQueList.isEmpty()) {
			 approvalQueList.stream()
					.map(prd -> new ApprovalQueueDto(prd.getApprovalQueId(), prd.getProductId(), prd.isApprovalStatus(),prd.isProductStatus(),prd.getCreatedDate()))
							.sorted(Comparator.comparing(ApprovalQueueDto::getCreatedDate).reversed()).collect(Collectors.toList());
		} else {
			log.info(ProductConstants.NO_DATA);
			return null;
		}
		
		return null;
	}

	

	@Override
	public boolean rejectApprovalProduct(Integer approvalId) {
		boolean msg = false;
			if(approvalQueueRepo.deleteByApprovalQueId(approvalId)>0){
				msg=true;
			}
			return msg;
	}

}
