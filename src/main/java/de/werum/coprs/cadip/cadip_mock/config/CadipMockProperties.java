package de.werum.coprs.cadip.cadip_mock.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "cadip")
public class CadipMockProperties {

	private Map<String, InboxConfiguration> inboxes = new HashMap<>();

	public void setInboxes(Map<String, InboxConfiguration> inboxes) {
		this.inboxes = inboxes;
	}

	public Map<String, InboxConfiguration> getInboxes() {
		return inboxes;
	}

}
