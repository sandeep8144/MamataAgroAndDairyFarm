package com.sandeep.ecom.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sandeep.ecom.model.OrderAddress;

public interface OrderAddressRepository extends JpaRepository<OrderAddress, Integer> {

}
