package com.sandeep.ecom.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.sandeep.ecom.model.Category;
import com.sandeep.ecom.model.Product;
import com.sandeep.ecom.model.ProductOrder;
import com.sandeep.ecom.model.UserDtls;
import com.sandeep.ecom.service.CartService;
import com.sandeep.ecom.service.CategoryService;
import com.sandeep.ecom.service.ProductOrderService;
import com.sandeep.ecom.service.ProductService;
import com.sandeep.ecom.service.UserService;
import com.sandeep.ecom.util.OrderStatus;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private ProductService productService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private CartService cartService;
	
	@Autowired
	private ProductOrderService productOrderService;
	
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
		return "admin/index";
	}

	@GetMapping("/loadAddProduct")
	public String loadAddProduct(Model model) {
		List<Category> categories = categoryService.getAllCategory();
		model.addAttribute("categories", categories);
		return "admin/add_product";
	}

	@GetMapping("/category")
	public String category(Model model) {
		model.addAttribute("categorys", categoryService.getAllCategory());
		return "admin/category";
	}

	@PostMapping("/saveCategory")
	public String saveCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file,
			HttpSession session) throws IOException {
		String imageName = file != null ? file.getOriginalFilename() : "default.jpg";
		category.setImageName(imageName);
		Boolean existCategory = categoryService.existCategory(category.getName());
		if (existCategory) {
			session.setAttribute("errorMsg", "Category Name Already Exist");
		} else {
			Category saveCategory = categoryService.saveCategory(category);
			if (ObjectUtils.isEmpty(saveCategory)) {
				session.setAttribute("errorMsg", "Not Saved ! internal server error");
			} else {
				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsoluteFile() + File.separator + "category" + File.separator
						+ file.getOriginalFilename());
				System.out.println(path);
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				session.setAttribute("succMsg", "Saved successful");
			}
		}
		return "redirect:/admin/category";
	}

	@GetMapping("/deleteCategory/{id}")
	public String deleteCategory(@PathVariable int id, HttpSession session) {
		Boolean deleteCategory = categoryService.deleteCategory(id);
		if (deleteCategory) {
			session.setAttribute("succMsg", "category delete success");
		} else {
			session.setAttribute("errorMsg", "something wrong");
		}
		return "redirect:/admin/category";
	}

	@GetMapping("/loadEditCategory/{id}")
	public String loadEditCategory(@PathVariable int id, Model model) {
		model.addAttribute("category", categoryService.getCategoryById(id));
		return "admin/edit_category";
	}

	@PostMapping("/updateCategory")
	public String updateCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file,
			HttpSession session) throws IOException {

		Category categoryId = categoryService.getCategoryById(category.getId());
		String imageName = file.isEmpty() ? categoryId.getImageName() : file.getOriginalFilename();
		if (!ObjectUtils.isEmpty(categoryId)) {
			categoryId.setName(category.getName());
			categoryId.setIsActive(category.getIsActive());
			categoryId.setImageName(imageName);
		}
		Category updateCategory = categoryService.saveCategory(categoryId);

		if (!ObjectUtils.isEmpty(updateCategory)) {
			if (!file.isEmpty()) {
				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsoluteFile() + File.separator + "category" + File.separator
						+ file.getOriginalFilename());
//				  System.out.println(path);
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			}

			session.setAttribute("succMsg", "Update successful");
		} else {
			session.setAttribute("errorMsg", "Not Saved ! internal server error");
		}

		return "redirect:/admin/loadEditCategory/" + category.getId();
	}

	@PostMapping("/saveProduct")
	public String saveProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile image,
			HttpSession session) throws IOException {

		String imageName = image.isEmpty() ? "default.jpg" : image.getOriginalFilename();
		product.setImage(imageName);
		product.setDiscount(0);
		product.setDiscountPrice(product.getPrice());
		Product saveProduct = productService.saveProduct(product);
		if (!ObjectUtils.isEmpty(saveProduct)) {
			File saveFile = new ClassPathResource("static/img").getFile();
			Path path = Paths.get(saveFile.getAbsoluteFile() + File.separator + "product_img" + File.separator
					+ image.getOriginalFilename());
			System.out.println(path);
			Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			session.setAttribute("succMsg", "Product Saved successful");
		} else {
			session.setAttribute("errorMsg", "Not Saved ! internal server error");
		}
		return "redirect:/admin/loadAddProduct";
	}

	@GetMapping("/viewProducts")
	public String loadViewProducts(Model model) {
		model.addAttribute("products", productService.getAllProduct());
		return "admin/products";
	}

	@GetMapping("/loadEditProduct/{id}")
	public String loadEditProduct(@PathVariable int id, Model model) {
		model.addAttribute("product", productService.getProductById(id));
		model.addAttribute("categories", categoryService.getAllCategory());
		return "admin/edit_product";
	}

	@PostMapping("/updateProduct")
	public String updateProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile image,
			HttpSession session) throws IOException {
		if (product.getDiscount() < 0 || product.getDiscount() > 100) {
			session.setAttribute("errorMsg", "Invalid Discount");
		} else {
			Product updatedProduct = productService.updateProduct(product, image);

			if (updatedProduct != null) {
				session.setAttribute("succMsg", "Update successful");
			} else {
				session.setAttribute("errorMsg", "Not Saved! Internal server error");
			}
		}
		return "redirect:/admin/loadEditProduct/" + product.getId();
	}

	@GetMapping("/deleteProduct/{id}")
	public String deleteProduct(@PathVariable int id, HttpSession session) {
		Boolean deleteProduct = productService.deleteProduct(id);
		if (deleteProduct) {
			session.setAttribute("succMsg", "Product delete success");
		} else {
			session.setAttribute("errorMsg", "something wrong");
		}
		return "redirect:/admin/viewProducts";
	}
	
	@GetMapping("/users")
	public String getAllUsers(Model model) {
		List<UserDtls> users = userService.getAllUser("ROLE_USER");
		model.addAttribute("users", users);
		return "/admin/users";
	}
	
	@GetMapping("/updateStatus")
	public String updateAccountStatus(@RequestParam Boolean status, @RequestParam Integer id, HttpSession session) {
		Boolean f = userService.updateAccountStatus(status,id);
		if(f) {
			session.setAttribute("succMsg", "Account Status Updated");	
		} else {
			session.setAttribute("errorMsg", "Something Wrong on Server");	
		}
		return "redirect:/admin/users";
	}
	
	@GetMapping("/orders")
	public String GetAllOrders(Model model) {
		List<ProductOrder> allOrders = productOrderService.getAllOrders();
		model.addAttribute("orders", allOrders);
		return "/admin/orders";
	}
	
	@PostMapping("/update-order-status")
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
		}else {
			session.setAttribute("errorMsg", "Something Wrong");
		}
		
		return "redirect:/admin/orders";
	}
}
