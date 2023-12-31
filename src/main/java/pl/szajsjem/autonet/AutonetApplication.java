package pl.szajsjem.autonet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession;

@SpringBootApplication
@EnableSpringHttpSession
public class AutonetApplication {

	public static void main(String[] args) {
		SpringApplication.run(AutonetApplication.class, args);
	}

}
