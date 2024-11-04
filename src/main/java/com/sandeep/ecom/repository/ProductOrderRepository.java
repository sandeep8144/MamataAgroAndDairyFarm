package com.sandeep.ecom.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sandeep.ecom.model.ProductOrder;

public interface ProductOrderRepository extends JpaRepository<ProductOrder, Integer>{

	List<ProductOrder> findByUserId(Integer userId);

	List<ProductOrder> findProductsById(Integer id);

}
