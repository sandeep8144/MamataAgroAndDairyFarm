package com.sandeep.ecom.model;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class UserDtls {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private String name;
	
	private String mobileNo;
	
	private String email;
	
	private String address;
	
	private String city;
	
	private String state;
	
	private String pinCode;
	
	private String password;
	
	private String image;
	
	private String role;
	
	private Boolean isEnable; 
	
	private Boolean accountNonLocked;
	
	private Integer failedAttempt;
	
	private Date lockTime;
	
	private String resetToken;
}
