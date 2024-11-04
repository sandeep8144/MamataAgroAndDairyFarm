package com.sandeep.ecom.controller;

import java.security.Principal;
import java.text.DecimalFormat;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sandeep.ecom.model.Cart;
import com.sandeep.ecom.model.Category;
import com.sandeep.ecom.model.OrderRequest;
import com.sandeep.ecom.model.ProductOrder;
import com.sandeep.ecom.model.UserDtls;
import com.sandeep.ecom.service.CartService;
import com.sandeep.ecom.service.CategoryService;
import com.sandeep.ecom.service.ProductOrderService;
import com.sandeep.ecom.service.UserService;
import com.sandeep.ecom.util.CommonUtil;
import com.sandeep.ecom.util.OrderStatus;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;
	@Autowired
	private CategoryService categoryService;

	@Autowired
	private CartService cartService;

	@Autowired
	private ProductOrderService productOrderService;
	
	@Autowired
	private CommonUtil commonUtil;


	@GetMapping("/")
	public String home() {
		return "user/home";
	}

	@ModelAttribute
	public void getUserDetails(Principal p, Model model) {
		if (p != null) {
			String email = p.getName();
			UserDtls userDtls = userService.getUserNameByEmail(email);
			model.addAttribute("user", userDtls);
			Integer countCart = cartService.getCountCart(userDtls.getId());
			model.addAttribute("countCart", countCart);
		}
		List<Category> allActiveCategory = categoryService.getAllActiveCategory();
		model.addAttribute("categories", allActiveCategory);
	}

	@GetMapping("/addCart")
	public String addToCart(@RequestParam Integer pid, @RequestParam Integer uid, HttpSession session) {
		Cart saveCart = cartService.saveCart(pid, uid);
		if (ObjectUtils.isEmpty(saveCart)) {
			session.setAttribute("errorMsg", "Product add to cart failed");
		} else {
			session.setAttribute("succMsg", "Product added to cart");
		}
		return "redirect:/product/" + pid;
	}

	@GetMapping("/cart")
	public String loadCartPage(Principal p, Model model) {
		UserDtls user = getLoggedInUserDetails(p);
		List<Cart> carts = cartService.getCartsByUser(user.getId());
		model.addAttribute("carts", carts);
		if (carts.size() > 0) {
			Double totalOrderPrice = carts.get(carts.size() - 1).getTotalOrderPrice();
			model.addAttribute("totalOrderPrice", totalOrderPrice);
		}
		return "/user/cart";
	}

	@GetMapping("/cartQuantityUpdate")
	public String updateCartQuantity(@RequestParam String sy, @RequestParam Integer cid) {
		cartService.updateCartQuantity(sy, cid);
		return "redirect:/user/cart";
	}

	@GetMapping("/orders")
	public String orderPage(Principal p, Model model) {
		UserDtls user = getLoggedInUserDetails(p);
		List<Cart> carts = cartService.getCartsByUser(user.getId());
		model.addAttribute("carts", carts);

		if (!carts.isEmpty()) {
			double totalProductPrice = 0.0;
			for (Cart cart : carts) {
				totalProductPrice += cart.getProduct().getDiscountPrice() * cart.getQuantity();
			}
			final double TAX_RATE = 0.10;
			double taxAmount = totalProductPrice * TAX_RATE;
			double deliveryFee = 50;
			double totalOrderPrice = totalProductPrice + taxAmount + deliveryFee;
			DecimalFormat df = new DecimalFormat("#.00");

			model.addAttribute("totalProductPrice", totalProductPrice);
			model.addAttribute("taxAmount", df.format(taxAmount));
			model.addAttribute("deliveryFee", deliveryFee);
			model.addAttribute("totalOrderPrice", totalOrderPrice);
		}
		return "/user/order";
	}

	@PostMapping("/save-orders")
	public String saveOrder(@ModelAttribute OrderRequest request, Principal p) {
		UserDtls user = getLoggedInUserDetails(p);
		productOrderService.saveOrder(user.getId(), request);
		cartService.clearCart(user.getId());
		return "redirect:/user/success";
	}

	@GetMapping("/success")
	public String loadSuccess() {
		return "/user/success";
	}

	@GetMapping("/user-orders")
	public String myOrder(Model model, Principal p) {
		UserDtls loginUser = getLoggedInUserDetails(p);
		List<ProductOrder> orders = productOrderService.getOrdersByUser(loginUser.getId());
		model.addAttribute("orders", orders);
		return "/user/my_orders";
	}

	@GetMapping("/update-status")
	public String updateOrderStatus(@RequestParam Integer id, @RequestParam Integer st, HttpSession session) {
		
		OrderStatus[] values = OrderStatus.values();
		String status = null;
		for(OrderStatus orderSt: values) {
			if(orderSt.getId().equals(st)) {
				status = orderSt.getName();
			}
		}
		Boolean updateOrder = productOrderService.updateOrderStatus(id, status);
		if(updateOrder) {
			session.setAttribute("succMsg", "Status Update");
			 if ("Cancelled".equalsIgnoreCase(status)) {
		            String userEmail = productOrderService.getUserEmailById(id);
		            String userName = productOrderService.getUserNameById(id);
		            List<ProductOrder> canceledProducts = productOrderService.getProductsById(id);
		            try {
						commonUtil.sendOrderCancellationEmail(userEmail, userName, id, canceledProducts);
					} catch (MessagingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        }
		}else {
			session.setAttribute("errorMsg", "Something Wrong");
		}
		
		return "redirect:/user/user-orders";
	}

	private UserDtls getLoggedInUserDetails(Principal p) {
		String email = p.getName();
		UserDtls userDtls = userService.getUserNameByEmail(email);
		return userDtls;
	}
}
