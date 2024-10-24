package com.sandeep.ecom.service;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sandeep.ecom.model.Product;

@Service
public interface ProductService {

	public Product saveProduct(Product product);

	public List<Product> getAllProduct();

	public Boolean deleteProduct(int id);

	public Product getProductById(int id);

	public Product updateProduct(Product product, MultipartFile image) throws IOException;

	public List<Product> getAllActiveProducts(String category);
	
}
