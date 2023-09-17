package com.smartcontact.controller;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smartcontact.dao.UserRepository;
import com.smartcontact.model.User;
import com.smartcontact.service.EmailService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ForgotController 
{
	
	Random random = new Random(1000);
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	
	//email id open handler 
	@RequestMapping("/forgot")
	public String openEmailForm()
	{
		
		return "forgot_email_form";
	}
	
	@PostMapping("/send-otp")
	public String sendOTP(@RequestParam("email") String email, HttpSession httpSession)
	{
		System.out.println("email:"+ email);
		
		//generating otp 
		
		
		int otp = random.nextInt(9999);
		System.out.println("otp: " + otp);
		
		
		//code to sent otp to email 
		String subject = "OTP from SCM";
		
		String message = "<h2>OTP is " + otp + "</h2"
				+ ">";
		
		String to=email;
		
		
		boolean sendOTPEmail = this.emailService.sendEmail(subject, message, to);
		
		if(sendOTPEmail)
		{
			httpSession.setAttribute("myotp", otp);
			httpSession.setAttribute("email", email);
			return "verify_otp";
			
		}else{
			httpSession.setAttribute("message", "check your email id");
			return "forgot_email_form";
			
		}
		
		
	}

	//verify otp 
	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam("otp") int otp, HttpSession httpSession)
	{
		int myOtp = (int)httpSession.getAttribute("myotp");
		String email = (String)httpSession.getAttribute("email");
		
		System.out.println("myOtp is :" +  myOtp);
	
		// System.out.println(((Object)otp).getClass().getSimpleName());
		
		if(myOtp== otp)
		{
			User user = this.userRepository.getUserByUserName(email);
			if(user==null)
			{
				//send error message
				httpSession.setAttribute("message", "User does not exists");
				return "forgot_email_form";
				
			}else {
				//send change password form
				
			}
			return "password_change_form";
			
		} else {
			httpSession.setAttribute("message", "You have entered wrong OTP");
			return "verify_otp";
		}
		
		
	
	}
	
	
	//change password
	@PostMapping("/change-password")
	public String chnagePassword(@RequestParam("newpassword") String newPassword, HttpSession httpSession)
	{
		String email = (String)httpSession.getAttribute("email");
		User user = this.userRepository.getUserByUserName(email);
		
		user.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
		this.userRepository.save(user);
		
		//httpSession.setAttribute("message", "Your password is saved succesfully");
		
		return "redirect:/signin?change=password changed successfully..";
		
	}
}

