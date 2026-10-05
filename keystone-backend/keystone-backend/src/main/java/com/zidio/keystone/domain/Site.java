package com.zidio.keystone.domain;

import jakarta.persistence.ManyToOne;

public class Site {
	
	@ManyToOne
	private Site site;

}
