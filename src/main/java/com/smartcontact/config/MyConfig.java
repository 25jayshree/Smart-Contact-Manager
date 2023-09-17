package com.smartcontact.config;

import java.util.function.Function;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class MyConfig 
{
	
	@Bean
	public UserDeatilsService createUserDetailsManager()
	{
		
		return new UserDeatilsService();
	}
	
	
	/*  @Bean
	  public InMemoryUserDetailsManager createUserDetailsManager() {
	  
	  UserDetails userDetails = createNewUser2("jaya", "dummy");
	  UserDetails userDetails2 = createNewUser2("laxmi", "dummy");
	  UserDetails userDetails3 =createNewUser2("surekha", "dummy");
	  
	  
	  
	  return new InMemoryUserDetailsManager(userDetails, userDetails2,
	  userDetails3); }
	 
*/
	/**
	 * @param username
	 * @param password
	 * @return
	 */
	/*private UserDetails createNewUser(String username, String password) {
		Function<String, String> passwordEncoder= input-> passwordEncoder().encode(input);
		
		UserDetails userDetails = User.builder().passwordEncoder(passwordEncoder)
		.username(username)
		.password(password)
		.roles("ADMIN")
		.build();
		return userDetails;
	}
	
	private UserDetails createNewUser2(String username, String password) {
		Function<String, String> passwordEncoder= input-> passwordEncoder().encode(input);
		
		UserDetails userDetails = User.builder().passwordEncoder(passwordEncoder)
		.username(username)
		.password(password)
		.roles("USER")
		.build();
		return userDetails;
	}*/
	
//	@Bean
//	public PasswordEncoder passwordEncoder()
//	{
//		return new BCryptPasswordEncoder();
//	}
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
	    return new BCryptPasswordEncoder();
	}
	
	//all urls are protected
	//login form is shown to unauth users 
	//csrf disabled 
	//frames allow
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception
	{
		
		
	httpSecurity.csrf().disable().authorizeHttpRequests().requestMatchers("/user/**").hasRole("USER")
	.requestMatchers("/**").permitAll().anyRequest().authenticated().and().formLogin()
	.loginPage("/signin")
	.loginProcessingUrl("/doLogin")
	.defaultSuccessUrl("/user/index");
	//.failureUrl("/login_fail"); 

	
	
//	httpSecurity.csrf().disable().authorizeHttpRequests().requestMatchers("/user/**").hasRole("USER") 
//	.requestMatchers("/admin/**").hasRole("ADMIN").requestMatchers("/home/**").permitAll().anyRequest().authenticated().and().formLogin();
//	

		return httpSecurity.build();
	}
	
	@Bean
	public AuthenticationProvider  authenticationProvider()
	{
		DaoAuthenticationProvider authenticationProviders = new DaoAuthenticationProvider();
		authenticationProviders.setUserDetailsService(createUserDetailsManager());
		authenticationProviders.setPasswordEncoder(passwordEncoder());
		return authenticationProviders;
		
	}

}
