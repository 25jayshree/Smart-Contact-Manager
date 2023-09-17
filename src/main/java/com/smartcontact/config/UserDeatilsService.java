package com.smartcontact.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.smartcontact.dao.UserRepository;
import com.smartcontact.model.User;

@Component
public class UserDeatilsService implements UserDetailsService
{
	@Autowired
	private UserRepository repository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		User user = repository.getUserByUserName(username);
		
	
		
		if(user== null)
		{
			System.out.println("no user");
			throw new UsernameNotFoundException("Could not found user");
		}
		
		
		System.out.println(user.getEmail());
		System.out.println(user.getRole());
		
		CustomUserDetails customUserDetails = new CustomUserDetails(user);
		
		return customUserDetails;
		
	}
	

}
