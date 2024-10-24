package com.sandeep.ecom.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sandeep.ecom.model.UserDtls;

@Service
public interface UserService {

	public UserDtls saveUser(UserDtls user);
	
	public UserDtls getUserNameByEmail(String email);
	
	public List<UserDtls> getAllUser(String role);

	public Boolean updateAccountStatus(Boolean status, Integer id);

    public void increaseFailedAttempt(UserDtls user);
    
    public void userAccountLock(UserDtls user);
    
    public boolean unlockAccountTimeExpired(UserDtls user);
    
    public void resetAttempt(int userId);

	public void updateUserResetToken(String email, String resetToken);

	public UserDtls getUserByToken(String token);
	
	public UserDtls updateUser(UserDtls user);

}
