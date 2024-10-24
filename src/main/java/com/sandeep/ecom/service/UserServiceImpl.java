package com.sandeep.ecom.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sandeep.ecom.model.UserDtls;
import com.sandeep.ecom.repository.UserRepository;
import com.sandeep.ecom.util.AppConstant;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Override
	public UserDtls saveUser(UserDtls user) {
		user.setRole("ROLE_USER");
		user.setIsEnable(true);
		user.setAccountNonLocked(true);
		user.setFailedAttempt(0);
		user.setLockTime(null);
		String encodePassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodePassword);
		UserDtls saveUser = userRepo.save(user);
		return saveUser;
	}

	@Override
	public UserDtls getUserNameByEmail(String email) {
		return userRepo.findByEmail(email);
	}

	@Override
	public List<UserDtls> getAllUser(String role) {
		return userRepo.findByRole(role);
	}

	@Override
	public Boolean updateAccountStatus(Boolean status, Integer id) {
		Optional<UserDtls> findById = userRepo.findById(id);
		
		if(findById.isPresent()) {
			UserDtls userDtls = findById.get();
			userDtls.setIsEnable(status);
			userRepo.save(userDtls);
			return true;
		}
		return false;
	}

	@Override
	public void increaseFailedAttempt(UserDtls user) {
		int attempt = user.getFailedAttempt() + 1;
		user.setFailedAttempt(attempt);
		userRepo.save(user);
	}

	@Override
	public void userAccountLock(UserDtls user) {
	  user.setAccountNonLocked(false);
		user.setLockTime(new Date());
		userRepo.save(user);
	}

	@Override
	public boolean unlockAccountTimeExpired(UserDtls user) {
		long lockTime = user.getLockTime().getTime();
		long unLockTIme = lockTime + AppConstant.UNLOCK_DURATION_TIME;
		long currentTime = System.currentTimeMillis();
		
		if(unLockTIme<currentTime) {
			user.setAccountNonLocked(true);
			user.setFailedAttempt(0);
			user.setLockTime(null);
			userRepo.save(user);
			return true;
		}
		return false;
	}

	@Override
	public void resetAttempt(int userId) {
	}

	@Override
	public void updateUserResetToken(String email, String resetToken) {
		UserDtls findByEmail = userRepo.findByEmail(email);
		findByEmail.setResetToken(resetToken);
		userRepo.save(findByEmail);
	}

	@Override
	public UserDtls getUserByToken(String token) {
		return userRepo.findByResetToken(token);
	}

	@Override
	public UserDtls updateUser(UserDtls user) {
		return userRepo.save(user);
	}
	
	

}
