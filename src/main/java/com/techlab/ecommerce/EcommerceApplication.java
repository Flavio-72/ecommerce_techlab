package com.techlab.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.techlab.ecommerce.config.SecurityUserProperties;

@SpringBootApplication
@EnableConfigurationProperties(SecurityUserProperties.class)
public class EcommerceApplication {

	public static void main(String[] args) {

		SpringApplication.run(EcommerceApplication.class, args);
	}

}
