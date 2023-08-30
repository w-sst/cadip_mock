package de.werum.coprs.cadip.cadip_mock.service.model;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

public class File {
	private UUID id;
	private String name;
	private String sessionId;
	private long channel;
	private long blockNumber;
	private boolean finalBlock;
	private LocalDateTime publicationDate;
	private LocalDateTime evictionDate;
	private long size;
	private boolean retransfer;
	
	public File(UUID id, String name, String sessionId, long channel, long blockNumber, boolean finalBlock,
			LocalDateTime publicationDate, LocalDateTime evictionDate, long size, boolean retransfer) {
		super();
		this.id = id;
		this.name = name;
		this.sessionId = sessionId;
		this.channel = channel;
		this.blockNumber = blockNumber;
		this.finalBlock = finalBlock;
		this.publicationDate = publicationDate;
		this.evictionDate = evictionDate;
		this.size = size;
		this.retransfer = retransfer;
	}

	public UUID getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getSessionId() {
		return sessionId;
	}
	public long getChannel() {
		return channel;
	}
	public long getBlockNumber() {
		return blockNumber;
	}
	public boolean isFinalBlock() {
		return finalBlock;
	}
	public LocalDateTime getPublicationDate() {
		return publicationDate;
	}
	public LocalDateTime getEvictionDate() {
		return evictionDate;
	}
	public long getSize() {
		return size;
	}
	public boolean isRetransfer() {
		return retransfer;
	}
}
