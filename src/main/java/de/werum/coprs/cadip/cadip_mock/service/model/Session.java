package de.werum.coprs.cadip.cadip_mock.service.model;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

public class Session {
	private UUID id;
	private String sessionId;
	private long numChannels;
	private LocalDateTime publicationDate;
	private String satellite;
	private String stationUnitId;
	private long downlinkOrbit;
	private String acquisitionId;
	private String antennaId;
	private String frontEndId;
	private boolean retransfer;
	private boolean antennaStatusOK;
	private boolean frontEndStatusOK;
	private LocalDateTime plannedDataStart;
	private LocalDateTime plannedDataStop;
	private LocalDateTime downlinkStart;
	private LocalDateTime downlinkStop;
	private boolean downlinkStatusOK;
	private boolean deliveryPushOK;
	
	public Session(UUID id, String sessionId, long numChannels, LocalDateTime publicationDate, String satellite,
			String stationUnitId, long downlinkOrbit, String acquisitionId, String antennaId, String frontEndId,
			boolean retransfer, boolean antennaStatusOK, boolean frontEndStatusOK, LocalDateTime plannedDataStart,
			LocalDateTime plannedDataStop, LocalDateTime downlinkStart, LocalDateTime downlinkStop,
			boolean downlinkStatusOK, boolean deliveryPushOK) {
		super();
		this.id = id;
		this.sessionId = sessionId;
		this.numChannels = numChannels;
		this.publicationDate = publicationDate;
		this.satellite = satellite;
		this.stationUnitId = stationUnitId;
		this.downlinkOrbit = downlinkOrbit;
		this.acquisitionId = acquisitionId;
		this.antennaId = antennaId;
		this.frontEndId = frontEndId;
		this.retransfer = retransfer;
		this.antennaStatusOK = antennaStatusOK;
		this.frontEndStatusOK = frontEndStatusOK;
		this.plannedDataStart = plannedDataStart;
		this.plannedDataStop = plannedDataStop;
		this.downlinkStart = downlinkStart;
		this.downlinkStop = downlinkStop;
		this.downlinkStatusOK = downlinkStatusOK;
		this.deliveryPushOK = deliveryPushOK;
	}

	public UUID getId() {
		return id;
	}
	public String getSessionId() {
		return sessionId;
	}
	public long getNumChannels() {
		return numChannels;
	}
	public LocalDateTime getPublicationDate() {
		return publicationDate;
	}
	public String getSatellite() {
		return satellite;
	}
	public String getStationUnitId() {
		return stationUnitId;
	}
	public long getDownlinkOrbit() {
		return downlinkOrbit;
	}
	public String getAcquisitionId() {
		return acquisitionId;
	}
	public String getAntennaId() {
		return antennaId;
	}
	public String getFrontEndId() {
		return frontEndId;
	}
	public boolean isRetransfer() {
		return retransfer;
	}
	public boolean isAntennaStatusOK() {
		return antennaStatusOK;
	}
	public boolean isFrontEndStatusOK() {
		return frontEndStatusOK;
	}
	public LocalDateTime getPlannedDataStart() {
		return plannedDataStart;
	}
	public LocalDateTime getPlannedDataStop() {
		return plannedDataStop;
	}
	public LocalDateTime getDownlinkStart() {
		return downlinkStart;
	}
	public LocalDateTime getDownlinkStop() {
		return downlinkStop;
	}
	public boolean isDownlinkStatusOK() {
		return downlinkStatusOK;
	}
	public boolean isDeliveryPushOK() {
		return deliveryPushOK;
	}
}
