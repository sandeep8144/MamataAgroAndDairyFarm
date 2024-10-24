package com.sandeep.ecom.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import com.sandeep.ecom.model.Product;
import com.sandeep.ecom.repository.ProductRepository;

@Service
public class ProductServiceImpl implements ProductService {

	@Autowired
	private ProductRepository productRepo;

	@Override
	public Product saveProduct(Product product) {
		return productRepo.save(product);
	}

	@Override
	public List<Product> getAllProduct() {
		return productRepo.findAll();
	}

	@Override
	public Boolean deleteProduct(int id) {
		Product product = productRepo.findById(id).orElse(null);
		if (!ObjectUtils.isEmpty(product)) {
			productRepo.delete(product);
			return true;
		}
		return false;
	}

	@Override
	public Product getProductById(int id) {
		Product product = productRepo.findById(id).orElse(null);
		return product;
	}

	@Override
	public Product updateProduct(Product product, MultipartFile image) throws IOException {
		Product existingProduct = productRepo.findById(product.getId()).orElse(null);
		if (existingProduct != null) {
			String imageName = image.isEmpty() ? existingProduct.getImage() : image.getOriginalFilename();

			existingProduct.setTitle(product.getTitle());
			existingProduct.setDescription(product.getDescription());
			existingProduct.setCategory(product.getCategory());
			existingProduct.setPrice(product.getPrice());
			existingProduct.setStock(product.getStock());
			existingProduct.setImage(imageName);
			existingProduct.setIsActive(product.getIsActive());
			existingProduct.setDiscount(product.getDiscount());

			Double discount = product.getPrice() * (product.getDiscount() / 100.0);
			Double discountPrice = product.getPrice() - discount;
			existingProduct.setDiscountPrice(discountPrice);
			Product updatedProduct = productRepo.save(existingProduct);

			// Handle image file saving if necessary
			if (!image.isEmpty()) {
				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "product_img" + File.separator
						+ image.getOriginalFilename());
				Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			}

			return updatedProduct; // Return the updated product
		}
		return null; // or throw an exception if you prefer
	}

	@Override
	public List<Product> getAllActiveProducts(String category) {
		List<Product> product = null;
		if(ObjectUtils.isEmpty(category)) {
			product = productRepo.findByIsActiveTrue();
		}else {
			product = productRepo.findByCategoryAndIsActiveTrue(category);
		}
		 
		return product;
	}

}
