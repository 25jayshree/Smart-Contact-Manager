package com.smartcontact.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartcontact.model.myOrder;

public interface MyOrderRepository extends JpaRepository<myOrder, Long> {

}
