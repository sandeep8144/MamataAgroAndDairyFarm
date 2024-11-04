package com.sandeep.ecom.util;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.sandeep.ecom.model.Product;
import com.sandeep.ecom.model.ProductOrder;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class CommonUtil {

	@Autowired
	private JavaMailSender mailSender;

	public Boolean sendMail(String url, String reciepentEmail) throws UnsupportedEncodingException, MessagingException {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);

		helper.setFrom("sandeepbarik8144@gmail.com", "Mamata AGro & Dairy Farm");
		helper.setTo(reciepentEmail);
		String content = "<p>Hello,</p>" + "<p>You have requested to reset your password.</p>"
				+ "<p>Click the link below to change your password:</p>" + "<p><a href=\"" + url
				+ "\">Change my password</a></p>";
		helper.setSubject("Password Reset");
		helper.setText(content, true);
		mailSender.send(message);
		return true;
	}

	public void sendRegistrationSuccessEmail(String toEmail, String userName) throws MessagingException {
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

		helper.setTo(toEmail);
		helper.setSubject("Welcome to Mamata Agro & Dairy Farm Family! Your Registration is Successful!");
		helper.setFrom("sandeepbarik8144@gmail.com");

		// HTML email content with logo
		String message = "<h1>Welcome to Mamata Agro & Dairy Farm, " + userName + "!</h1>"
				+ "<p>We are thrilled to have you with us! 🌿🐄</p>"
				+ "<p>Your registration is now complete, and we are excited to have you onboard. "
				+ "At Mamata Agro & Dairy Farm, we are committed to providing fresh, quality products directly from our farm to your doorstep.</p>"
				+ "<h3>What's next?</h3>" + "<ul>" + "<li>Explore our wide range of organic and dairy products.</li>"
				+ "<li>Enjoy exclusive member benefits and discounts on your favorite items.</li>"
				+ "<li>Be the first to know about upcoming offers and farm-fresh updates!</li>" + "</ul>"
				+ "<p>If you have any questions or need assistance, feel free to reach out to us. We're here to help!</p>"
				+ "<br>"
				+ "<p><strong>Thank you</strong> for choosing us to be part of your healthy lifestyle journey.</p>"
				+ "<p>Warm regards,</p>" + "<p><strong>The Mamata Agro & Dairy Farm Team</strong><br>"
				+ "mamatadairyagro@gmail.com</p>" + "<br>"
				+ "<img src='cid:logoImage' alt='Mamata Agro & Dairy Farm Logo'/>";

		helper.setText(message, true);

		// Attach the logo image as an inline resource
		ClassPathResource logo = new ClassPathResource("static/img/sss(1).jpg");
		helper.addInline("logoImage", logo);

		mailSender.send(mimeMessage);
	}

	public void sendOrderSuccessEmail(String toEmail, String userName, List<ProductOrder> productOrders,
			double totalOrderPrice, double taxAmount, double deliveryFee) throws MessagingException {
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

		helper.setTo(toEmail);
		helper.setSubject("Your Order is Successfully Placed!");
		helper.setFrom("sandeepbarik8144@gmail.com");

		StringBuilder productDetails = new StringBuilder();
		productDetails.append("<table style='width:100%; border-collapse:collapse;'>")
				.append("<tr><th style='border:1px solid #ddd; padding:8px;'>Product Name</th>")
				.append("<th style='border:1px solid #ddd; padding:8px;'>Price</th>")
				.append("<th style='border:1px solid #ddd; padding:8px;'>Quantity</th>")
				.append("<th style='border:1px solid #ddd; padding:8px;'>Total</th></tr>");

		for (ProductOrder order : productOrders) {
			String productName = order.getProduct().getTitle(); // Assuming ProductOrder has a Product reference
			double price = order.getProduct().getDiscountPrice(); // Assuming ProductOrder can access price through
																	// Product
			int quantity = order.getQuantity(); // Assuming you have a quantity field in ProductOrder
			double totalPrice = price * quantity;

			productDetails.append("<tr>").append("<td style='border:1px solid #ddd; padding:8px;; text-align:center'>")
					.append(productName).append("</td>")
					.append("<td style='border:1px solid #ddd; padding:8px;; text-align:center'>₹").append(price)
					.append("</td>").append("<td style='border:1px solid #ddd; padding:8px;; text-align:center'>")
					.append(quantity).append("</td>")
					.append("<td style='border:1px solid #ddd; padding:8px; text-align:center'>₹").append(totalPrice)
					.append("</td>").append("</tr>");
		}

		productDetails.append("</table>").append("<p><strong>Tax Rate: ₹").append(taxAmount).append("</strong></p>");
		productDetails.append("</table>").append("<p><strong>Delivery fee: ₹").append(deliveryFee)
				.append("</strong></p>");
		productDetails.append("</table>").append("<p><strong>Total Order Price: ₹").append(totalOrderPrice)
				.append("</strong></p>");

		String message = "<h1>Thank you for your order, " + userName + "!</h1>"
				+ "<p>Your order has been successfully placed. Here are the details:</p>" + productDetails
				+ "<p>We will notify you once your order is on its way.</p>" + "<p>Thank you for shopping with us!</p>"
				+ "<br><p>Best regards,</p>" + "<p><strong>The Mamata Agro & Dairy Farm Team</strong><br>"
				+ "mamatadairyagro@gmail.com</p>";

		helper.setText(message, true);

		// Attach the logo image as an inline resource
//		ClassPathResource logo = new ClassPathResource("static/img/sss(1).jpg");
//		helper.addInline("logoImage", logo);

		mailSender.send(mimeMessage);
	}

	public void sendOrderCancellationEmail(String toEmail, String userName, Integer orderId, List<ProductOrder> products)
			throws MessagingException {
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

		helper.setTo(toEmail);
		helper.setSubject("Order Cancellation Confirmation – Mamata Agro & Dairy Farm");
		helper.setFrom("sandeepbarik8144@gmail.com");

		// Build product details table
		StringBuilder productDetails = new StringBuilder("<table style='width:100%; border-collapse:collapse;'>");
		productDetails.append("<tr><th style='border:1px solid #dddddd; padding:8px;'>Product Name</th>")
				.append("<th style='border:1px solid #dddddd; padding:8px;'>Price</th>")
				.append("<th style='border:1px solid #dddddd; padding:8px;'>Quantity</th></tr>");

		for (ProductOrder product : products) {
			productDetails.append("<tr>").append("<td style='border:1px solid #dddddd; padding:8px;'>")
					.append(product.getProduct().getTitle()).append("</td>")
					.append("<td style='border:1px solid #dddddd; padding:8px;'>").append(product.getPrice())
					.append("</td>").append("<td style='border:1px solid #dddddd; padding:8px;'>")
					.append(product.getQuantity()).append("</td>").append("</tr>");
		}
		productDetails.append("</table>");

		// HTML email content with product details
		String message = "<h1>Order Canceled Successfully</h1>" + "<p>Dear " + userName + ",</p>"
				+ "<p>Your order with ID: <strong>" + orderId + "</strong> has been successfully canceled.</p>"
				+ "<p><strong>Cancelled Product Details:</strong></p>" + productDetails.toString()
				+ "<h3>Need Assistance?</h3>"
				+ "<p>If you have any questions, feel free to contact us. We’re here to help!</p>" + "<br>"
				+ "<p>Thank you for being part of Mamata Agro & Dairy Farm. We hope to serve you again soon.</p>"
				+ "<p>Warm regards,</p>" + "<p><strong>The Mamata Agro & Dairy Farm Team</strong><br>"
				+ "mamatadairyagro@gmail.com</p>" + "<br>"
				+ "<img src='cid:logoImage' alt='Mamata Agro & Dairy Farm Logo' style='width:150px; height:auto;'/>";

		helper.setText(message, true);

		// Attach the logo image as an inline resource
		ClassPathResource logo = new ClassPathResource("static/img/sss(1).jpg");
		helper.addInline("logoImage", logo);

		mailSender.send(mimeMessage);
	}

	public static String generateUrl(HttpServletRequest request) {
		String siteUrl = request.getRequestURL().toString();
		return siteUrl.replace(request.getServletPath(), "");
	}

}
