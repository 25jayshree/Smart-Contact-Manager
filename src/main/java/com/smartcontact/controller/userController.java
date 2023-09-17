package com.smartcontact.controller;

import java.io.File;
import java.io.IOException;
import java.net.http.HttpRequest;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.smartcontact.dao.MyOrderRepository;
import com.smartcontact.dao.UserRepository;
import com.smartcontact.dao.contactRepository;
import com.smartcontact.helper.Message;
import com.smartcontact.model.Contact;
import com.smartcontact.model.User;
import com.smartcontact.model.myOrder;

import ch.qos.logback.core.net.server.Client;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import com.razorpay.*;

@Controller
@RequestMapping("/user")
public class userController {
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private UserRepository repository;

	@Autowired
	private contactRepository contactRepository;
	
	@Autowired
	private MyOrderRepository myOrderRepository;

	// method for adding common data
	@ModelAttribute
	public void addCommomdata(Model m, Principal principal) {

		String username = principal.getName();
		System.out.println(username);

		User user = this.repository.getUserByUserName(username);
		// System.out.println("User " + user);

		m.addAttribute("user", user);
	}

	// dashboard home

	@RequestMapping("/index")
	public String dashboard(Model m, Principal principal) {
		m.addAttribute("title", "User Dashboard");
		return "normal/user_dashboard";

	}

	// open add contact form controller
	@GetMapping("/add-contact")
	public String openAddContactForm(Model m) {
		m.addAttribute("title", "Add contact");
		m.addAttribute("contact", new Contact());

		return "normal/add_contact_form";
	}

	// processing add contact for
	@PostMapping("/process-contact")
	public String processContact(@Valid @ModelAttribute("contact") Contact contact, BindingResult bindingResult,
			Model model, Principal principal, @RequestParam("image") MultipartFile file, HttpSession httpSession) {
		// String filenameString=
		// StringUtils.cleanPath(multipartFile.getOriginalFilename());
		// contact.setImage(filenameString);

		try {
			String name = principal.getName();
			User user = this.repository.getUserByUserName(name);

			contact.setUser(user);

			// processing and uploading file
			if (file.isEmpty()) {
				// if file is empty then show msg
				contact.setImage("default.png");
			} else {
				// upload a file to folder and update a name to contact

				contact.setImage(file.getOriginalFilename());
				File savefile = new ClassPathResource("/static/img").getFile();

				Path path = Paths.get(
						savefile.getAbsolutePath() + File.separator + file.getOriginalFilename() + contact.getCid());

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				//Files.delete(file.getInputStream())
			}

			user.getContacts().add(contact);

			this.repository.save(user);

			// System.out.println("data" + contact);
			System.out.println("contact added to user");

			// sucess message
			httpSession.setAttribute("message", new Message("Your contact is added!!", "success"));

		} catch (Exception e) {

			System.out.println(e.getMessage());
			// message error
			httpSession.setAttribute("message", new Message("something went wrong!!", "danger"));

		}

		return "normal/add_contact_form";
	}

	// handler for show contact

	// per page - 5 contacts = 5[n]
	// current page = 0[page]
	@GetMapping("/show-contacts/{page}")
	public String showContact(@PathVariable("page") Integer page, Model m, Principal principal) {
		m.addAttribute("title", "Contacts");

		String username = principal.getName();

		User user = this.repository.getUserByUserName(username);

		Pageable pageable = PageRequest.of(page, 3);

		Page<Contact> contactByUser = this.contactRepository.findContactByUser(user.getId(), pageable);

		m.addAttribute("contacts", contactByUser);
		m.addAttribute("currentPage", page);
		m.addAttribute("totalPages", contactByUser.getTotalPages());

		return "normal/show_contacts";
	}

	// showing particular contact details
	@RequestMapping("/{cId}/contact")
	public String showContactDetail(@PathVariable("cId") Integer cId, Model m, Principal principal) {
		System.out.println(cId);

		String name = principal.getName();

		User user = this.repository.getUserByUserName(name);

		Optional<Contact> optional = this.contactRepository.findById(cId);

		Contact contact = optional.get();

		if (user.getId() == contact.getUser().getId()) {
			m.addAttribute("contact_det", contact);
			m.addAttribute("title", contact.getName());
		}

		return "normal/contact_detail";
	}

	// delet cntact handler
	@GetMapping("/delete/{cid}")
	public String deleteContact(@PathVariable("cid") Integer cid, Model m, Principal principal,HttpSession httpSession) {
		String name = principal.getName();

		User user = this.repository.getUserByUserName(name);

		Contact contact = this.contactRepository.findById(cid).get();

		// check current contact is connected with current user
		
		

		if (user.getId() == contact.getUser().getId()) {
			
			contact.setUser(null);
			
			////remove image 
			
			this.contactRepository.delete(contact);
			httpSession.setAttribute("message", new Message("Contact deleted succesfully","success"));
		}

		return "redirect:/user/show-contacts/0";

	}
	
	//open update contact handler
	@PostMapping("/update-contact/{cid}")
	public String updateForm(@PathVariable("cid") Integer cid ,Model m)
	{
		
		m.addAttribute("title", "Update Contact");
		Contact contact = this.contactRepository.findById(cid).get();
		m.addAttribute("contact",contact);
		return "normal/update_form";
	}
	
	//update the contact 
	@PostMapping("/process-update")
	public String updateContact(@Valid @ModelAttribute("contact") Contact contact, BindingResult bindingResult
			, @RequestParam("image") MultipartFile multipartFile
			, HttpSession httpSession,Principal principal) 
	{
		try {
			
			Contact oldContact = this.contactRepository.findById(contact.getCid()).get();
			//image updating
			if(!multipartFile.isEmpty()) {
				//rewrite file
				
				//delete old photo
				File deletefile = new ClassPathResource("/static/img").getFile();
				File file1 = new File(deletefile, oldContact.getImage());
				file1.delete();
				
				//update new photo
				

				File savefile = new ClassPathResource("/static/img").getFile();

				Path path = Paths.get(
						savefile.getAbsolutePath() + File.separator + multipartFile.getOriginalFilename() + contact.getCid());
			
				
				Files.copy(multipartFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				
				contact.setImage(multipartFile.getOriginalFilename());
				
			} else {
				contact.setImage(oldContact.getImage());
			}
			
			User user = this.repository.getUserByUserName(principal.getName());
			contact.setUser(user);
			
		//update contact
			
			 @Valid
			Contact contact2 = this.contactRepository.save(contact);
			System.out.println(contact2);
			
			httpSession.setAttribute("message", new Message("Your contact is updated!!","success"));
			
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
		
		System.out.println(contact.getName());
		
		//return "redirect:/user/" + contact.getCid()+"/contact";
		return "redirect:/user/show-contacts/0";
	}
	
	@GetMapping("/profile")
	public String showProfile()
	{
		
		return "normal/profile";
	}
	
	//open setting handler 
	@GetMapping("/settings")
	public String openSettings()
	{
		return "normal/settings";
	}
	
	//change-password handler 
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldPassword") String oldPassword,
			@RequestParam("newPassword") String newPassword,
			Principal principal, HttpSession httpSession)
	{
		System.out.println(oldPassword);
		System.out.println(newPassword);
		
		String userName = principal.getName();
		User currentUser = this.repository.getUserByUserName(userName);
		
		
		
		if( this.bCryptPasswordEncoder.matches(oldPassword,currentUser.getPassword())) {
			currentUser.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
			this.repository.save(currentUser);
			httpSession.setAttribute("message", new Message("Your contact is updated!!","success"));
			
			
		} else {
			//error
			httpSession.setAttribute("message", new Message("Please eter correct old password","danger"));
			
			return "redirect:settings";
			
		}
		
		
		return "redirect:index";
	}
	
	//creating oredr for payment
	@PostMapping("/create_order")
	@ResponseBody
	public String createOredr(@RequestBody Map<String, Object> data,Principal principal) throws RazorpayException
	{
		
		System.out.println("order function is executed");
		System.out.println(data);
		int amt = Integer.parseInt(data.get("amount").toString());
		
		RazorpayClient razorpayClient = new RazorpayClient("rzp_test_ZiRmy1KOJYIEvv", "Pbkrg3lPw96MISRdOAJg6HQt");
		
		JSONObject ob = new JSONObject();
		ob.put("amount", amt*100);//passing in paisa
		ob.put("currency", "INR");
		ob.put("receipt", "txn_123");
		
		//creating new order
		Order order = razorpayClient.Orders.create(ob);
		System.out.println(order);
		//if want you can save this to your database
		
		myOrder myOrder = new myOrder();
		myOrder.setAmount(order.get("amount")+"");
		myOrder.setOrder_id(order.get("order_id"));
		myOrder.setPayment_id(null);
		myOrder.setStatus("Created");
		myOrder.setUser(this.repository.getUserByUserName(principal.getName()));
		myOrder.setReceipt(order.get("receipt"));
		this.myOrderRepository.save(myOrder);
		
		
		return order.toString();
	}
}
