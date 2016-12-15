package com.homework;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot Class, used just to simplify and run the application locally with
 * Embedded instance of Tomcat.
 * 
 * @author Tarek Sahalia
 *
 */
@SpringBootApplication(scanBasePackageClasses = { TicketApplication.class })
public class TicketApplication {

	public static void main(String[] args) {
		SpringApplication.run(TicketApplication.class, args);
	}

}
