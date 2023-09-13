package de.werum.coprs.cadip.cadip_mock.data.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Session {
	
	private UUID id;
	private String sessionId;
	private long numChannels; // 2
	private LocalDateTime publicationDate; // now
	private String satellite; //SessionID 
	private String stationUnitId;  // Filename 
	private long downlinkOrbit; //SessionID letzte 6
	private String acquisitionId; // Statisch
	private String antennaId;	// Statisch
	private String frontEndId;	// Statisch
	private boolean retransfer;	// Statisch false
	private boolean antennaStatusOK;	// Statisch true
	private boolean frontEndStatusOK;	// Statisch true
	private LocalDateTime plannedDataStart;
	private LocalDateTime plannedDataStop;
	private LocalDateTime downlinkStart; // Timestamp von SessionID
	private LocalDateTime downlinkStop;	// Timestamp von SessionID
	private boolean downlinkStatusOK;	// Statisch true
	private boolean deliveryPushOK;	// Statisch true
	
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

	@Override
	public String toString() {
		return "Session [id=" + id + ", sessionId=" + sessionId + ", numChannels=" + numChannels + ", publicationDate="
				+ publicationDate + ", satellite=" + satellite + ", stationUnitId=" + stationUnitId + ", downlinkOrbit="
				+ downlinkOrbit + ", acquisitionId=" + acquisitionId + ", antennaId=" + antennaId + ", frontEndId="
				+ frontEndId + ", retransfer=" + retransfer + ", antennaStatusOK=" + antennaStatusOK
				+ ", frontEndStatusOK=" + frontEndStatusOK + ", plannedDataStart=" + plannedDataStart
				+ ", plannedDataStop=" + plannedDataStop + ", downlinkStart=" + downlinkStart + ", downlinkStop="
				+ downlinkStop + ", downlinkStatusOK=" + downlinkStatusOK + ", deliveryPushOK=" + deliveryPushOK + "]";
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
