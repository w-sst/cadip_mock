package de.werum.coprs.cadip.cadip_mock.data;

import java.io.IOException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import de.werum.coprs.cadip.cadip_mock.config.CadipMockProperties;
import de.werum.coprs.cadip.cadip_mock.config.InboxConfiguration;

public class PollTrigger {
	
	@Autowired
	CadipMockProperties properties;
	@Autowired
	Storage storage;
	
	@Scheduled(fixedDelayString = "${cadip.trigger.interval-ms}")
	public void poll() throws IOException {
		System.out.println("poll start");
		// LocalDateTime t = LocalDateTime.now();
		for(InboxConfiguration config : properties.getInboxes().values()) {
			System.out.println("Inbox: " + config.getPath());
			new PollRun(config, storage).run();
			
		}
		storage.printAll();
		// LocalDateTime t2 = LocalDateTime.now();
		
		// System.out.println(t + " == " + t2);
	}
}
