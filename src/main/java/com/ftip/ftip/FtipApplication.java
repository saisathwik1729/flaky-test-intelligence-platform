package com.ftip.ftip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FtipApplication {

	public static void main(String[] args) {
		SpringApplication.run(FtipApplication.class, args);
	}

}
