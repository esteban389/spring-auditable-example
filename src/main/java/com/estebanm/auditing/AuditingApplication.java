package com.estebanm.auditing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class AuditingApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuditingApplication.class, args);
	}

}
