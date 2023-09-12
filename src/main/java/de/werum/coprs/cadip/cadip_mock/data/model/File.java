package de.werum.coprs.cadip.cadip_mock.data.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class File {

	private String filePath;
	
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
	
	public File(String filePath, UUID id, String name, String sessionId, long channel, long blockNumber,
			boolean finalBlock, LocalDateTime publicationDate, LocalDateTime evictionDate, long size,
			boolean retransfer) {
		super();
		this.filePath = filePath;
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
	
	@Override
	public String toString() {
		return "File [filePath=" + filePath + ", id=" + id + ", name=" + name + ", sessionId=" + sessionId
				+ ", channel=" + channel + ", blockNumber=" + blockNumber + ", finalBlock=" + finalBlock
				+ ", publicationDate=" + publicationDate + ", evictionDate=" + evictionDate + ", size=" + size
				+ ", retransfer=" + retransfer + "]";
	}
	
	// @Override
	// public int hashCode() {
	// 	return Objects.hash(filePath);
	// }
    // 
	// @Override
	// public boolean equals(Object obj) {
	// 	return Objects.equals(filePath, obj);
	// }

	@Override
	public int hashCode() {
		return Objects.hash(filePath);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if(obj == String.class) {
			return Objects.equals(filePath, obj);
		}
		if (getClass() != obj.getClass())
			return false;
		return Objects.equals(filePath, ((File) obj).filePath);
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
	public void setFinalBlock(boolean finalBlock) {
		this.finalBlock = finalBlock;
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

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
}
