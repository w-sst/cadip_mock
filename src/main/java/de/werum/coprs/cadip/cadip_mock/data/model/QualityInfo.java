package de.werum.coprs.cadip.cadip_mock.data.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class QualityInfo {

	private UUID sessionUUID;

	// oData fields
	private long channel;
	private String sessionId;
	private long acquiredTFs;
	private long errorTFs;
	private long correctedTFs;
	private long uncorrectableTFs;
	private long dataTFs;
	private long errorDataTFs;
	private long correctedDataTFs;
	private long uncorrectableDataTFs;
	private LocalDateTime deliveryStart;
	private LocalDateTime deliveryStop;
	private long totalChunks;
	private long totalVolume;

	public QualityInfo(UUID sessionUUID, long channel, String sessionId, long acquiredTFs, long errorTFs,
			long correctedTFs, long uncorrectableTFs, long dataTFs, long errorDataTFs, long correctedDataTFs,
			long uncorrectableDataTFs, LocalDateTime deliveryStart, LocalDateTime deliveryStop, long totalChunks,
			long totalVolume) {
		super();
		this.sessionUUID = sessionUUID;
		this.channel = channel;
		this.sessionId = sessionId;
		this.acquiredTFs = acquiredTFs;
		this.errorTFs = errorTFs;
		this.correctedTFs = correctedTFs;
		this.uncorrectableTFs = uncorrectableTFs;
		this.dataTFs = dataTFs;
		this.errorDataTFs = errorDataTFs;
		this.correctedDataTFs = correctedDataTFs;
		this.uncorrectableDataTFs = uncorrectableDataTFs;
		this.deliveryStart = deliveryStart;
		this.deliveryStop = deliveryStop;
		this.totalChunks = totalChunks;
		this.totalVolume = totalVolume;
	}

	@Override
	public String toString() {
		return "QualityInfo [channel=" + channel + ", sessionId=" + sessionId + ", acquiredTFs=" + acquiredTFs
				+ ", errorTFs=" + errorTFs + ", correctedTFs=" + correctedTFs + ", uncorrectableTFs=" + uncorrectableTFs
				+ ", dataTFs=" + dataTFs + ", errorDataTFs=" + errorDataTFs + ", correctedDataTFs=" + correctedDataTFs
				+ ", uncorrectableDataTFs=" + uncorrectableDataTFs + ", deliveryStart=" + deliveryStart
				+ ", deliveryStop=" + deliveryStop + ", totalChunks=" + totalChunks + ", totalVolume=" + totalVolume
				+ "]";
	}

	public long getChannel() {
		return channel;
	}

	public String getSessionId() {
		return sessionId;
	}

	public long getAcquiredTFs() {
		return acquiredTFs;
	}

	public long getErrorTFs() {
		return errorTFs;
	}

	public long getCorrectedTFs() {
		return correctedTFs;
	}

	public long getUncorrectableTFs() {
		return uncorrectableTFs;
	}

	public long getDataTFs() {
		return dataTFs;
	}

	public long getErrorDataTFs() {
		return errorDataTFs;
	}

	public long getCorrectedDataTFs() {
		return correctedDataTFs;
	}

	public long getUncorrectableDataTFs() {
		return uncorrectableDataTFs;
	}

	public LocalDateTime getDeliveryStart() {
		return deliveryStart;
	}

	public LocalDateTime getDeliveryStop() {
		return deliveryStop;
	}

	public long getTotalChunks() {
		return totalChunks;
	}

	public long getTotalVolume() {
		return totalVolume;
	}

	public UUID getSessionUUID() {
		return sessionUUID;
	}
}
