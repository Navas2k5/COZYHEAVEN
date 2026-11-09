package com.springboot.cozyheavenstay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class CozyheavenstayBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(CozyheavenstayBackendApplication.class, args);
	}

}
