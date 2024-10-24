package com.sandeep.ecom.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sandeep.ecom.model.Cart;

@Service
public interface CartService {
	
	public Cart saveCart(Integer productId, Integer userId);
	
	public List<Cart> getCartsByUser(Integer userId);
	
	public Integer getCountCart(Integer userId);

	public void updateCartQuantity(String sy, Integer cid);

}
