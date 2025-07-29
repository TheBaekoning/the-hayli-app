package app.asclepius;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AsclepiusBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(AsclepiusBackendApplication.class, args);
	}

}
