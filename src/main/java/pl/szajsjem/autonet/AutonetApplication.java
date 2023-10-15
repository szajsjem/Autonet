package pl.szajsjem.autonet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AutonetApplication {

	public static void main(String[] args) {
		System.getenv().forEach((k, v) -> {
			System.out.println(k + ":" + v);
		});
		SpringApplication.run(AutonetApplication.class, args);
	}

}
