package com.retrovault.retrovault;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync //Multiprocesor para ejecutar tareas en segundo plano

public class RetrovaultApplication {

	public static void main(String[] args) {
		SpringApplication.run(RetrovaultApplication.class, args);
	}

}
