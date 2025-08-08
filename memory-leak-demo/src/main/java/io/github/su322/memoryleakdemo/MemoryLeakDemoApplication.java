package io.github.su322.memoryleakdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MemoryLeakDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(MemoryLeakDemoApplication.class, args);
	}

}
