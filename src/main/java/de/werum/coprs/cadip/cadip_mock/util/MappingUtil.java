package de.werum.coprs.cadip.cadip_mock.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.ex.ODataRuntimeException;

import de.werum.coprs.cadip.cadip_mock.data.model.File;
import de.werum.coprs.cadip.cadip_mock.data.model.Session;

public class MappingUtil {
	
	public static Entity mapFileToEntity(File file) {
		
		final Entity fE = new Entity()                                                                                                                                 
				.addProperty(new Property(null, "Id", ValueType.PRIMITIVE, file.getId()))                                             
				.addProperty(new Property(null, "Name", ValueType.PRIMITIVE, file.getName()))                                                                                     
				.addProperty(new Property(null, "SessionId", ValueType.PRIMITIVE, file.getSessionId()))                                                                                   
				.addProperty(new Property(null, "Channel", ValueType.PRIMITIVE, file.getChannel()))
				.addProperty(new Property(null, "BlockNumber", ValueType.PRIMITIVE, file.getBlockNumber()))
				.addProperty(new Property(null, "FinalBlock", ValueType.PRIMITIVE, file.isFinalBlock()))                                                                                
				.addProperty(new Property(null, "PublicationDate", ValueType.PRIMITIVE, TimeUtil.convertLocalDateTimeToTimestamp(file.getPublicationDate())))
				.addProperty(new Property(null, "EvictionDate", ValueType.PRIMITIVE, TimeUtil.convertLocalDateTimeToTimestamp(file.getEvictionDate())))   
				.addProperty(new Property(null, "Size", ValueType.PRIMITIVE, file.getSize()))                                                                                       
				.addProperty(new Property(null, "Retransfer", ValueType.PRIMITIVE, file.isRetransfer()));                                                                               
		fE.setId(createId("Files", file.getId()));                                                                                                                                       
		
		return fE;
	}
	
	public static Entity mapSessionToEntity(Session session) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.[[SSSSSS][SSS]]'Z'");
		// TimeUtil.convertStringToTimestamp("2014-01-01T00:00:00.123Z", dateTimeFormatter)
		
		// TimeUtil.convertLocalDateTimeToTimestamp(LocalDateTime.parse(session.getPublicationDate().format(dateTimeFormatter), dateTimeFormatter))))
		
		final Entity sE = new Entity()                                                                                                                               
				   .addProperty(new Property(null, "Id", ValueType.PRIMITIVE, session.getId()))                                              
				   .addProperty(new Property(null, "SessionId", ValueType.PRIMITIVE, session.getSessionId()))                                                                                    
					.addProperty(new Property(null, "NumChannels", ValueType.PRIMITIVE, session.getNumChannels()))                                                                                    
					.addProperty(new Property(null, "PublicationDate", ValueType.PRIMITIVE, TimeUtil.convertLocalDateTimeToTimestamp(session.getPublicationDate())))  
					.addProperty(new Property(null, "Satellite", ValueType.PRIMITIVE, session.getSatellite()))                                                                                    
					.addProperty(new Property(null, "StationUnitId", ValueType.PRIMITIVE, session.getStationUnitId()))                                                                                
					.addProperty(new Property(null, "DownlinkOrbit", ValueType.PRIMITIVE, session.getDownlinkOrbit()))                                                                                 
					.addProperty(new Property(null, "AcquisitionId", ValueType.PRIMITIVE, session.getAcquisitionId()))                                                                                
					.addProperty(new Property(null, "AntennaId", ValueType.PRIMITIVE, session.getAntennaId()))                                                                                    
					.addProperty(new Property(null, "FrontEndId", ValueType.PRIMITIVE, session.getFrontEndId()))                                                                                   
					.addProperty(new Property(null, "Retransfer", ValueType.PRIMITIVE, session.isRetransfer()))                                                                                   
					.addProperty(new Property(null, "AntennaStatusOK", ValueType.PRIMITIVE, session.isAntennaStatusOK()))                                                                               
					.addProperty(new Property(null, "FrontEndStatusOK", ValueType.PRIMITIVE, session.isFrontEndStatusOK()))                                                                              
					.addProperty(new Property(null, "PlannedDataStart", ValueType.PRIMITIVE, TimeUtil.convertLocalDateTimeToTimestamp(session.getPlannedDataStart())))  
					.addProperty(new Property(null, "PlannedDataStop", ValueType.PRIMITIVE, TimeUtil.convertLocalDateTimeToTimestamp(session.getPlannedDataStop())))
					.addProperty(new Property(null, "DownlinkStart", ValueType.PRIMITIVE, TimeUtil.convertLocalDateTimeToTimestamp(session.getDownlinkStart())))     
					.addProperty(new Property(null, "DownlinkStop", ValueType.PRIMITIVE, TimeUtil.convertLocalDateTimeToTimestamp(session.getDownlinkStop())))      
					.addProperty(new Property(null, "DownlinkStatusOK", ValueType.PRIMITIVE, session.isDownlinkStatusOK()))                                                                              
					.addProperty(new Property(null, "DeliveryPushOK", ValueType.PRIMITIVE, session.isDeliveryPushOK()));                                                                               
		sE.setId(createId("Sessions", session.getId()));     
		return sE;
	}
	
	// public static EntityCollection
	
	private static URI createId(String entitySetName, Object id) {
        try {
            return new URI(entitySetName + "(" + String.valueOf(id) + ")");
        } catch (URISyntaxException e) {
            throw new ODataRuntimeException("Unable to create id for entity: " + entitySetName, e);
        }
    }
	
}
