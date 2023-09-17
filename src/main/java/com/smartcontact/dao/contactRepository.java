package com.smartcontact.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smartcontact.model.Contact;
import com.smartcontact.model.User;

public interface contactRepository extends JpaRepository<Contact, Integer>
{
	//pagination .....
	//pageable - current page and contact per page
	
	@Query("from Contact as c where c.user.id=:userId")
	public Page<Contact> findContactByUser(@Param("userId") int userId, Pageable pageable);
	
	
	public List<Contact> findByNameContainingAndUser(String keyword , User user);
	
	

}
