package pl.smarttesting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SmartTestingApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartTestingApplication.class, args);
	}

}
