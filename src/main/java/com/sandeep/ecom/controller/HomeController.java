package com.sandeep.ecom.controller;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.sandeep.ecom.model.Category;
import com.sandeep.ecom.model.Product;
import com.sandeep.ecom.model.UserDtls;
import com.sandeep.ecom.service.CartService;
import com.sandeep.ecom.service.CategoryService;
import com.sandeep.ecom.service.ProductService;
import com.sandeep.ecom.service.UserService;
import com.sandeep.ecom.util.CommonUtil;

@Controller
public class HomeController {

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private ProductService productService;

	@Autowired
	private UserService userService;

	@Autowired
	private CommonUtil commonUtil;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private CartService cartService;

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

	@GetMapping("/")
	public String index(Model model) {
		return "index";
	}

	@GetMapping("/signin")
	public String login() {
		return "login";
	}

	@GetMapping("/register")
	public String register() {
		return "register";
	}

	@GetMapping("/products")
	public String products(Model model, @RequestParam(value = "category", defaultValue = "") String category) {
		List<Category> allActiveCategory = categoryService.getAllActiveCategory();
		List<Product> allActiveProducts = productService.getAllActiveProducts(category);
		model.addAttribute("categories", allActiveCategory);
		model.addAttribute("products", allActiveProducts);
		model.addAttribute("paramValue", category);
		return "products";
	}

	@GetMapping("/product/{id}")
	public String product(@PathVariable("id") int id, Model model) {
		Product productById = productService.getProductById(id);
		model.addAttribute("p", productById);
		return "product";
	}

	@GetMapping("/category/{id}")
	public String getCategory(@PathVariable int id, Model model) {
		Category category = categoryService.getCategoryById(id);
		model.addAttribute("category", category);
		return "product";
	}

	@PostMapping("/saveUser")
	public String saveUser(@ModelAttribute UserDtls user, @RequestParam("img") MultipartFile file, HttpSession session)
			throws IOException {
		String imageName = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();
		user.setImage(imageName);
		UserDtls saveUser = userService.saveUser(user);
		if (!ObjectUtils.isEmpty(saveUser)) {
			if (!file.isEmpty()) {
				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsoluteFile() + File.separator + "profile_img" + File.separator
						+ file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			}
			session.setAttribute("sussMsg", "User Created Successfully");
		} else {
			session.setAttribute("errorMsg", "Something Wrong! internal Server Error");
		}
		return "redirect:/register";
	}

	// forget password

	@GetMapping("/forget_password")
	public String showForgetPassword() {
		return "forget_password";
	}

	@PostMapping("/forget_password")
	public String processForgetPassword(@RequestParam String email, HttpSession session, HttpServletRequest request)
			throws UnsupportedEncodingException, MessagingException {

		UserDtls userNameByEmail = userService.getUserNameByEmail(email);
		if (ObjectUtils.isEmpty(userNameByEmail)) {
			session.setAttribute("errorMsg", "Invalid Email");
		} else {
			String resetToken = UUID.randomUUID().toString();
			userService.updateUserResetToken(email, resetToken);

			// generate url

			String url = CommonUtil.generateUrl(request) + "/reset_password?token=" + resetToken;
			Boolean sendMail = commonUtil.sendMail(url, email);
			if (sendMail) {
				session.setAttribute("succMsg", "Please check your email..Password Reset link sent");
			} else {
				session.setAttribute("errorMsg", "Somthing Wrong On Server");
			}
		}
		return "redirect:/forget_password";
	}

	@GetMapping("/reset_password")
	public String showResetPassword(@RequestParam String token, HttpSession session, Model model) {
		UserDtls userByToken = userService.getUserByToken(token);
		if (userByToken == null) {
			model.addAttribute("msg", "Your link is invalid or expired");
			return "message";
		}
		model.addAttribute("token", token);
		return "reset_password";
	}

	@PostMapping("/reset_password")
	public String resetPassword(@RequestParam String token, @RequestParam String password, HttpSession session,
			Model model) {
		UserDtls userByToken = userService.getUserByToken(token);
		if (userByToken == null) {
			model.addAttribute("msg", "Your link is invalid or expired");
			return "message";
		} else {
			userByToken.setPassword(passwordEncoder.encode(password));
			userByToken.setResetToken(null);
			userService.updateUser(userByToken);
			session.setAttribute("succMsg", "Password Change successfully");
			model.addAttribute("msg", "Password Change successfully");
			return "message";
		}

	}

}
