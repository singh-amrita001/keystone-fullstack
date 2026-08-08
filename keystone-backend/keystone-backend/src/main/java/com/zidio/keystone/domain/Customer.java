package com.zidio.keystone.domain;

import jakarta.persistence.ManyToOne;

public class Customer {
	
	@ManyToOne
	private Customer customer;

}
