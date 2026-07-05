package com.likelion_collb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class LikelionCollbApplication {

	public static void main(String[] args) {
		SpringApplication.run(LikelionCollbApplication.class, args);
	}

}
