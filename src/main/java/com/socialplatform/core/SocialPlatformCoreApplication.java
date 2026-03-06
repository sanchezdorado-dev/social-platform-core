package com.socialplatform.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SocialPlatformCoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(SocialPlatformCoreApplication.class, args);
	}

}
