package net.koko.kaspar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KasparApplication {
	public static Logger logger = LoggerFactory.getLogger(KasparApplication.class);
	public static void main(String[] args) {
		SpringApplication.run(KasparApplication.class, args);
	}

}
