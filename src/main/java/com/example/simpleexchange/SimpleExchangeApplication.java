package com.example.simpleexchange;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.*;
import org.springframework.retry.annotation.*;

@SpringBootApplication
@EnableFeignClients
@EnableRetry
public class SimpleExchangeApplication {

	public static void main(String[] args) {
		SpringApplication.run(SimpleExchangeApplication.class, args);
	}

}
