package com.sandeep.ecom.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sandeep.ecom.model.Cart;
import com.sandeep.ecom.model.Category;
import com.sandeep.ecom.model.UserDtls;
import com.sandeep.ecom.service.CartService;
import com.sandeep.ecom.service.CategoryService;
import com.sandeep.ecom.service.UserService;

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
		if(ObjectUtils.isEmpty(saveCart)) {
			session.setAttribute("errorMsg", "Product add to cart failed");
		}else {
			session.setAttribute("succMsg", "Product added to cart");
		}
		return "redirect:/product/" + pid;
	}
	
	@GetMapping("/cart")
	public String loadCartPage(Principal p, Model model) {
		UserDtls user = getLoggedInUserDetails(p);
		List<Cart> carts= cartService.getCartsByUser(user.getId());
		model.addAttribute("carts",carts);
		if(carts.size()>0) {
		Double totalOrderPrice = carts.get(carts.size()-1).getTotalOrderPrice();
		model.addAttribute("totalOrderPrice", totalOrderPrice);
		}
		return "/user/cart";
	}

	private UserDtls getLoggedInUserDetails(Principal p) {
		String email = p.getName();
		UserDtls userDtls = userService.getUserNameByEmail(email);
		return userDtls;
	}
	
	@GetMapping("/cartQuantityUpdate")
	public String updateCartQuantity(@RequestParam String sy, @RequestParam Integer cid) {
		cartService.updateCartQuantity(sy,cid);
		return "redirect:/user/cart";
	}
}
