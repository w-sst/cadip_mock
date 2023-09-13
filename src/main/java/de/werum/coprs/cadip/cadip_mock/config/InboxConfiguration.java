package de.werum.coprs.cadip.cadip_mock.config;

public class InboxConfiguration {
	
	private String path;
	private boolean retransfer = false;

	private long numChannels = 2;
	private String stationUnitId;
	private String acquisitionId;
	private String antennaId;
	private String frontEndId;
	private boolean antennaStatusOK = true;
	private boolean frontEndStatusOK = true;
	private boolean downlinkStatusOK = true;
	private boolean deliveryPushOK = true;	
	
	public String getPath() {
		return path;
	}

	public void setPath(String filePath) {
		this.path = filePath;
	}

	public boolean isRetransfer() {
		return retransfer;
	}

	public void setRetransfer(boolean retransfer) {
		this.retransfer = retransfer;
	}
	
	public long getNumChannels() {
		return numChannels;
	}

	public void setNumChannels(long numChannels) {
		this.numChannels = numChannels;
	}

	public String getAcquisitionId() {
		return acquisitionId;
	}

	public void setAcquisitionId(String acquisitionId) {
		this.acquisitionId = acquisitionId;
	}

	public String getAntennaId() {
		return antennaId;
	}

	public void setAntennaId(String antennaId) {
		this.antennaId = antennaId;
	}

	public String getFrontEndId() {
		return frontEndId;
	}

	public void setFrontEndId(String frontEndId) {
		this.frontEndId = frontEndId;
	}

	public boolean isAntennaStatusOK() {
		return antennaStatusOK;
	}

	public void setAntennaStatusOK(boolean antennaStatusOK) {
		this.antennaStatusOK = antennaStatusOK;
	}

	public boolean isFrontEndStatusOK() {
		return frontEndStatusOK;
	}

	public void setFrontEndStatusOK(boolean frontEndStatusOK) {
		this.frontEndStatusOK = frontEndStatusOK;
	}

	public boolean isDownlinkStatusOK() {
		return downlinkStatusOK;
	}

	public void setDownlinkStatusOK(boolean downlinkStatusOK) {
		this.downlinkStatusOK = downlinkStatusOK;
	}

	public boolean isDeliveryPushOK() {
		return deliveryPushOK;
	}

	public void setDeliveryPushOK(boolean deliveryPushOK) {
		this.deliveryPushOK = deliveryPushOK;
	}

	public String getStationUnitId() {
		return stationUnitId;
	}

	public void setStationUnitId(String stationUnitId) {
		this.stationUnitId = stationUnitId;
	}
}
