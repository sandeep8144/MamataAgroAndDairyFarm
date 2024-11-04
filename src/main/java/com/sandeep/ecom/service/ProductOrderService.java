package com.sandeep.ecom.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sandeep.ecom.model.OrderRequest;
import com.sandeep.ecom.model.ProductOrder;

@Service
public interface ProductOrderService {

	public String saveOrder(Integer userId, OrderRequest orderRequest);

	public List<ProductOrder> getOrdersByUser(Integer userId);

	public Boolean updateOrderStatus(Integer id, String status);
	
	public String getUserEmailById(Integer id);
	
	public String getUserNameById(Integer id);
	
	public List<ProductOrder> getProductsById(Integer id);
	
	public List<ProductOrder> getAllOrders();
}
