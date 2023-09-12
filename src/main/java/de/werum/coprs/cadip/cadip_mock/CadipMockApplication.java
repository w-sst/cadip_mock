package de.werum.coprs.cadip.cadip_mock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class CadipMockApplication {

	public static void main(String[] args) {
		SpringApplication.run(CadipMockApplication.class, args);
	}

}
