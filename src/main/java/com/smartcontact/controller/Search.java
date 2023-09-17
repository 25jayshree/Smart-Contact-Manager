package com.smartcontact.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.smartcontact.dao.UserRepository;
import com.smartcontact.dao.contactRepository;
import com.smartcontact.model.Contact;
import com.smartcontact.model.User;

@RestController
public class Search
{
	@Autowired
	private UserRepository repository;
	
	@Autowired
	private contactRepository contactRepository;
	
	
	//search handler
	@GetMapping("/search/{query}")
	public ResponseEntity<?> search(@PathVariable("query") String query, Principal principal)
	{
		System.out.println(query);
		User user = this.repository.getUserByUserName(principal.getName());
		
		List<Contact> findByNameContaining = this.contactRepository.findByNameContainingAndUser(query, user);
		
		return ResponseEntity.ok(findByNameContaining);
		
	}

}
