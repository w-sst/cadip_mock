package de.werum.coprs.cadip.cadip_mock.data;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import de.werum.coprs.cadip.cadip_mock.config.CadipMockProperties;
import de.werum.coprs.cadip.cadip_mock.config.InboxConfiguration;

public class PollTrigger {
	
	private static final Logger LOG = LogManager.getLogger(PollTrigger.class);
	@Autowired
	CadipMockProperties properties;
	@Autowired
	Storage storage;
	
	@Scheduled(fixedDelayString = "${cadip.trigger.interval-ms}")
	public void poll() throws IOException {
		// LocalDateTime t = LocalDateTime.now();
		for(InboxConfiguration config : properties.getInboxes().values()) {
			LOG.debug("Polling Inbox " + config.getPath());
			try {
				new PollRun(config, storage).run();
			} catch (Exception e) {
				LOG.error("Poll failed for Inbox " + config.getPath(), e);
			}
		}
		LOG.debug("Currently in Storage:\n" + storage.toString());
		// LOG.debug(storage.printAll());
		// LocalDateTime t2 = LocalDateTime.now();
		
		// System.out.println(t + " == " + t2);
	}
}
