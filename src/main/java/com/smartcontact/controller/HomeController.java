package com.smartcontact.controller;

import java.net.http.HttpRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


import com.smartcontact.dao.UserRepository;
import com.smartcontact.helper.Message;
import com.smartcontact.model.User;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
//@RequestMapping("/home")
public class HomeController {

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired 
	private UserRepository userRepository;
	 
	
	@RequestMapping("/")
	public String Home(Model m )
	{
		m.addAttribute("title" , "Home- Smart Contact manager");
		
		return "home";
	}
	
	@RequestMapping("/about")
	public String About(Model m )
	{
		m.addAttribute("title" , "Home- Smart Contact manager");
		
		return "about";
	}
	
	@RequestMapping("/signup")
	public String signUp(Model m)
	{
		m.addAttribute("title" , "Register- Smart Contact manager");
		m.addAttribute("user", new User());
		return "signup";
	}
	
	//handler for registering user 
	@PostMapping("/do_register")
	public String registerUser(@Valid @ModelAttribute("user") User user,BindingResult bindingResult,
			@RequestParam(value="agreement", defaultValue ="false") boolean agreement, 
			Model model,HttpSession session)
	{
		try {
			
			if(bindingResult.hasErrors())
			{
				System.out.println(user);
				model.addAttribute("user",user);
				return "signup";
			}
			
			
			if(!agreement) {
				System.out.println("You have not agreed terms");
				throw new Exception("You have not agreed terms");
				
			}
			
		
			 user.setRole("ROLE_USER"); 
			 user.setEnabled(true);
			  user.setImageUrl("deafult.png");
			  user.setPassword(passwordEncoder.encode(user.getPassword()));
			 
			
			System.out.println("Agreement" + agreement);
			System.out.println("User" + user);
			
			User result = this.userRepository.save(user);	
			
			
			model.addAttribute("user",new User());
			
			//model.addAttribute("servletpath", "${servletPath}");
			session.setAttribute("message", new Message("successfully registered ",
					"alert-success"));
			
			return "signup";
			
		}catch(Exception e){
			e.printStackTrace();
			model.addAttribute("user", user);
		
			session.setAttribute("message", new Message("Something went wrong"+e.getMessage(),
					"alert-danger"));
			
			return "signup";
		}
		
	}
	
	//handler for custom login 
	@GetMapping("/signin")
	public String customLogin(Model m)
	{
		m.addAttribute("title","Contact Manager- login");
		
		return "login";
		
	}
}

