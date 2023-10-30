package com.example.springsecurityoauthclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

	Logger logger = LoggerFactory.getLogger(CommandLineRunner.class);

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Autowired
	private WebClient webClient;


	@Override
	public void run(String... args) throws Exception {
		String body = webClient.get()
			.uri("http://localhost:8082/user/authorities")
			.retrieve()
			.bodyToMono(String.class)
			.block();
		logger.info(body);
	}
}