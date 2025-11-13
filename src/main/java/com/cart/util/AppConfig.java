package com.cart.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

	@Bean
	@LoadBalanced
	RestTemplate restTemplate() {
		return new RestTemplate();
	}
	
	@Bean
	ExecutorService executor() {
		return Executors.newFixedThreadPool(5);
	}
}
