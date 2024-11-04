package com.sandeep.ecom.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sandeep.ecom.model.Cart;
import com.sandeep.ecom.model.OrderAddress;
import com.sandeep.ecom.model.OrderRequest;
import com.sandeep.ecom.model.Product;
import com.sandeep.ecom.model.ProductOrder;
import com.sandeep.ecom.model.UserDtls;
import com.sandeep.ecom.repository.CartRepository;
import com.sandeep.ecom.repository.ProductOrderRepository;
import com.sandeep.ecom.util.CommonUtil;
import com.sandeep.ecom.util.OrderStatus;

import jakarta.mail.MessagingException;

@Service
public class ProductOrderServiceImpl implements ProductOrderService {

	@Autowired
	private ProductOrderRepository productOrderRepo;

	@Autowired
	private CartRepository cartRepo;

	@Autowired
	private CommonUtil commonUtil;

	@Override
	public String saveOrder(Integer userId, OrderRequest orderRequest) {
		List<Cart> carts = cartRepo.findByUserId(userId);
		List<ProductOrder> savedOrders = new ArrayList<>();

		double totalProductPrice = 0.0;
		final double TAX_RATE = 0.10;
		for (Cart cart : carts) {
			totalProductPrice += cart.getProduct().getDiscountPrice() * cart.getQuantity();
		}

		double taxAmount = totalProductPrice * TAX_RATE;
		double deliveryFee = 50;
		double totalOrderPrice = totalProductPrice + taxAmount + deliveryFee;
		for (Cart cart : carts) {
			ProductOrder order = new ProductOrder();
			order.setOrderId(UUID.randomUUID().toString());
			order.setOrderDate(LocalDate.now());
			order.setProduct(cart.getProduct());
			order.setPrice(cart.getProduct().getDiscountPrice());
			order.setQuantity(cart.getQuantity());
			order.setUser(cart.getUser());
			order.setStatus(OrderStatus.IN_PROGESS.getName());

			order.setPaymentType(orderRequest.getPaymentType());

			OrderAddress address = new OrderAddress();
			address.setFirstName(orderRequest.getFirstName());
			address.setLastName(orderRequest.getLastName());
			address.setEmail(orderRequest.getEmail());
			address.setMobileNo(orderRequest.getMobileNo());
			address.setAddress(orderRequest.getAddress());
			address.setCity(orderRequest.getCity());
			address.setState(orderRequest.getState());
			address.setPincode(orderRequest.getPinCode());

			order.setOrderAddress(address);

//			totalProductPrice += cart.getProduct().getDiscountPrice() * cart.getQuantity();
			order.setTotalPrice(totalProductPrice);

			productOrderRepo.save(order);
			savedOrders.add(order);

		}

		try {
			commonUtil.sendOrderSuccessEmail(orderRequest.getEmail(), orderRequest.getFirstName(), savedOrders,
					totalOrderPrice, taxAmount , deliveryFee);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "Order saved successfully";

	}

	@Override
	public Boolean updateOrderStatus(Integer id, String status) {
		Optional<ProductOrder> findById = productOrderRepo.findById(id);
		
		if(findById.isPresent()) {
			ProductOrder productOrder = findById.get();
			productOrder.setStatus(status);
			productOrderRepo.save(productOrder);
			return true;
		}
		return false;
	}

	@Override
	public List<ProductOrder> getOrdersByUser(Integer userId) {
		List<ProductOrder> productOrder = productOrderRepo.findByUserId(userId);
		return productOrder;
	}

	@Override
	public String getUserEmailById(Integer id) {
		ProductOrder order = productOrderRepo.findById(id).orElse(null);
        return (order != null && order.getUser() != null) ? order.getUser().getEmail() : null;
    }

	@Override
	public List<ProductOrder> getAllOrders() {
		return productOrderRepo.findAll();
		
	}

	@Override
    public String getUserNameById(Integer id) {
    	ProductOrder order = productOrderRepo.findById(id).orElse(null);
        return (order != null && order.getUser() != null) ? order.getUser().getName() : "Customer";
    }

	@Override
    public List<ProductOrder> getProductsById(Integer id) {
        return productOrderRepo.findProductsById(id);
    }
}
