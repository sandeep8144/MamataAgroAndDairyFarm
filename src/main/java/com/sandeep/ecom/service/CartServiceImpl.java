package com.sandeep.ecom.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.sandeep.ecom.model.Cart;
import com.sandeep.ecom.model.Product;
import com.sandeep.ecom.model.UserDtls;
import com.sandeep.ecom.repository.CartRepository;
import com.sandeep.ecom.repository.ProductRepository;
import com.sandeep.ecom.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class CartServiceImpl implements CartService {

	@Autowired
	private CartRepository cartRepo;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private ProductRepository productRepo;

	@Override
	public Cart saveCart(Integer productId, Integer userId) {

		Product product = productRepo.findById(productId).get();
		UserDtls userDtls = userRepo.findById(userId).get();

		Cart cartStatus = cartRepo.findByProductIdAndUserId(productId, userId);
		System.out.println("cartstaus1 " + cartStatus);

		Cart cart = null;

		if (ObjectUtils.isEmpty(cartStatus)) {
			System.out.println("cartStatus2" + cartStatus);
			cart = new Cart();
			cart.setProduct(product);
			cart.setUser(userDtls);
			cart.setQuantity(1);
			cart.setTotalPrice(1 * product.getDiscountPrice());
			System.out.println("New 2" + cart);
		} else {
			cart = cartStatus;
			System.out.println("cartStatus " + cartStatus);
			System.out.println("cart" + cart);
			cart.setQuantity(cart.getQuantity() + 1);
			cart.setTotalPrice(cart.getQuantity() * cart.getProduct().getDiscountPrice());
			System.out.println("existing" + cart);
		}
		Cart saveCart = cartRepo.save(cart);
		System.out.println(saveCart);

		return saveCart;
	}

	@Override
	public List<Cart> getCartsByUser(Integer userId) {
		List<Cart> carts = cartRepo.findByUserId(userId);

		Double totalOrderPrice = 0.0;
		List<Cart> updateCarts = new ArrayList<>();
		for (Cart c : carts) {
			Double totalPrice = (c.getProduct().getDiscountPrice() * c.getQuantity());
			c.setTotalPrice(totalPrice);
			totalOrderPrice = totalOrderPrice + totalPrice;
			c.setTotalOrderPrice(totalOrderPrice);
			updateCarts.add(c);
		}
		
		return updateCarts;
	}

	@Override
	public Integer getCountCart(Integer userId) {
		Integer countByUserId = cartRepo.countByUserId(userId);
		return countByUserId;
	}

	@Override
	public void updateCartQuantity(String sy, Integer cid) {
		Cart cart = cartRepo.findById(cid).get();
		Integer updateQuantity;
		
		if(sy.equalsIgnoreCase("de")) {
			updateQuantity = cart.getQuantity()-1;
			if(updateQuantity<=0) {
				cartRepo.delete(cart);
			}else {
				cart.setQuantity(updateQuantity);
				cartRepo.save(cart);
			}
		}else {
			updateQuantity = cart.getQuantity()+1;
			cart.setQuantity(updateQuantity);
			cartRepo.save(cart);
		}
		
	}
	
	@Transactional
	public void clearCart(Integer userId) {
	    cartRepo.deleteByUserId(userId);
	}

}
