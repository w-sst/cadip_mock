package de.werum.coprs.cadip.cadip_mock.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.werum.coprs.cadip.cadip_mock.data.Storage;
import de.werum.coprs.cadip.cadip_mock.service.edm.EdmProvider;

@Configuration
public class OlingoConfig {

	@Bean
	EdmProvider getEdmProvider() {
		return new EdmProvider();
	}
	
	@Bean
	Storage getStorage() {
		return new Storage();
	}

}