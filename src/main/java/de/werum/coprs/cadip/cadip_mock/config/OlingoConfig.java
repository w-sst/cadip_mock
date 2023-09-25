package de.werum.coprs.cadip.cadip_mock.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import de.werum.coprs.cadip.cadip_mock.data.PollTrigger;
import de.werum.coprs.cadip.cadip_mock.data.Storage;
import de.werum.coprs.cadip.cadip_mock.service.edm.EdmProvider;

@Configuration
@EnableScheduling
public class OlingoConfig {

	@Bean
	EdmProvider getEdmProvider() {
		return new EdmProvider();
	}

	@Bean
	Storage getStorage() {
		return new Storage();
	}

	@Bean
	PollTrigger getPollTrigger() {
		return new PollTrigger();
	}

}