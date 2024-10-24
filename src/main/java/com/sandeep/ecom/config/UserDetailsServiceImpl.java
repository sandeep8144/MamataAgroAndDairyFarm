package com.sandeep.ecom.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.sandeep.ecom.model.UserDtls;
import com.sandeep.ecom.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService{

	@Autowired
	private  UserRepository userRepo;
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		UserDtls user = userRepo.findByEmail(username);
		if(user==null) {
			throw new UsernameNotFoundException("User Not Found");
		}
		return new CustomeUser(user);
	}

}
