package org.ncd.raspserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RaspserverApplication {

	public static void main(String[] args) {
//		Platform.startup(() ->{});
		SpringApplication.run(RaspserverApplication.class, args);
		
	}

}
