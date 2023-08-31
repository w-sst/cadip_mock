package de.werum.coprs.cadip.cadip_mock.data;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.ex.ODataRuntimeException;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.uri.UriParameter;

import de.werum.coprs.cadip.cadip_mock.service.edm.EdmProvider;
import de.werum.coprs.cadip.cadip_mock.util.OlingoUtil;
import de.werum.coprs.cadip.cadip_mock.util.TimeUtil;

public class Storage {

    private List<Entity> sessionsList;
    private List<Entity> filesList;
    
    public Storage() {
        sessionsList = new ArrayList<Entity>();
        filesList = new ArrayList<Entity>();
        initSampleData();
    }

    /* PUBLIC FACADE */

    public EntityCollection readEntitySetData(EdmEntitySet edmEntitySet)throws ODataApplicationException{
    	String entityName = edmEntitySet.getEntityType().getName();
    	switch(entityName) {
    		case EdmProvider.ET_SESSION_NAME:
    		case EdmProvider.ET_FILE_NAME:
    			return getProducts(entityName);
    		default:
    			return null;
    	}
    }

    public Entity readEntityData(EdmEntitySet edmEntitySet, List<UriParameter> keyParams) throws ODataApplicationException{

        EdmEntityType edmEntityType = edmEntitySet.getEntityType();

        switch(edmEntityType.getName()) {
			case EdmProvider.ET_SESSION_NAME:
			case EdmProvider.ET_FILE_NAME:
				return getProduct(edmEntityType, keyParams);
			default:
				return null;
        }
            
    }

    /*  INTERNAL */

    private List<Entity> getEntityList(String entityName) throws ODataApplicationException {
    	switch (entityName) {
    		case EdmProvider.ET_SESSION_NAME:
    			return sessionsList;
    		case EdmProvider.ET_FILE_NAME:
    			return filesList;
    		default:
    			throw new ODataApplicationException("Entity for requested key doesn't exist",
                        HttpStatusCode.NOT_FOUND.getStatusCode(), Locale.ENGLISH);
    	}
    }
    
    private EntityCollection getProducts(String entityName) throws ODataApplicationException{
        EntityCollection retEntitySet = new EntityCollection();
        List<Entity> entityList = getEntityList(entityName);
        
        for(Entity productEntity : entityList){
            retEntitySet.getEntities().add(productEntity);
        }

        return retEntitySet;
    }


    private Entity getProduct(EdmEntityType edmEntityType, List<UriParameter> keyParams) throws ODataApplicationException{

        // the list of entities at runtime
        EntityCollection entitySet = getProducts(edmEntityType.getName());

        /*  generic approach  to find the requested entity */
        Entity requestedEntity = OlingoUtil.findEntity(edmEntityType, entitySet, keyParams);

        if(requestedEntity == null){
            // this variable is null if our data doesn't contain an entity for the requested key
            // Throw suitable exception
            throw new ODataApplicationException("Entity for requested key doesn't exist",
                                       HttpStatusCode.NOT_FOUND.getStatusCode(), Locale.ENGLISH);
        }

        return requestedEntity;
     }

     /* HELPER */
     private void initSampleData(){

    	 DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.[[SSSSSS][SSS]]'Z'");
         
         final Entity sE1 = new Entity()
            .addProperty(new Property(null, "Id", ValueType.PRIMITIVE, UUID.fromString("00000000-0000-0000-0000-00001")))
            .addProperty(new Property(null, "SessionId", ValueType.PRIMITIVE, "1"))
         	.addProperty(new Property(null, "NumChannels", ValueType.PRIMITIVE, 10L))
         	.addProperty(new Property(null, "PublicationDate", ValueType.PRIMITIVE, TimeUtil.convertStringToTimestamp("2014-01-01T00:00:00.123Z", dateTimeFormatter)))
         	.addProperty(new Property(null, "Satellite", ValueType.PRIMITIVE, "S1A"))
         	.addProperty(new Property(null, "StationUnitId", ValueType.PRIMITIVE, "123"))
         	.addProperty(new Property(null, "DownlinkOrbit", ValueType.PRIMITIVE, 123L))
         	.addProperty(new Property(null, "AcquisitionId", ValueType.PRIMITIVE, "123"))
         	.addProperty(new Property(null, "AntennaId", ValueType.PRIMITIVE, "123"))
         	.addProperty(new Property(null, "FrontEndId", ValueType.PRIMITIVE, "123"))
         	.addProperty(new Property(null, "Retransfer", ValueType.PRIMITIVE, false))
         	.addProperty(new Property(null, "AntennaStatusOK", ValueType.PRIMITIVE, true))
         	.addProperty(new Property(null, "FrontEndStatusOK", ValueType.PRIMITIVE, true))
         	.addProperty(new Property(null, "PlannedDataStart", ValueType.PRIMITIVE, TimeUtil.convertStringToTimestamp("2014-01-01T01:20:00.123Z", dateTimeFormatter)))
         	.addProperty(new Property(null, "PlannedDataStop", ValueType.PRIMITIVE, TimeUtil.convertStringToTimestamp("2014-01-01T01:30:00.001123Z", dateTimeFormatter)))
         	.addProperty(new Property(null, "DownlinkStart", ValueType.PRIMITIVE, TimeUtil.convertStringToTimestamp("2014-01-01T02:10:00.001Z", dateTimeFormatter)))
         	.addProperty(new Property(null, "DownlinkStop", ValueType.PRIMITIVE, TimeUtil.convertStringToTimestamp("2014-01-01T02:21:00.000Z", dateTimeFormatter)))
         	.addProperty(new Property(null, "DownlinkStatusOK", ValueType.PRIMITIVE, true))
         	.addProperty(new Property(null, "DeliveryPushOK", ValueType.PRIMITIVE, true));
        sE1.setId(createId("Sessions", 1));
        sessionsList.add(sE1);

        final Entity sE2 = new Entity()
     		   .addProperty(new Property(null, "Id", ValueType.PRIMITIVE, UUID.fromString("00000000-0000-0000-0000-00002")))
                .addProperty(new Property(null, "SessionId", ValueType.PRIMITIVE, "2"))
             	.addProperty(new Property(null, "NumChannels", ValueType.PRIMITIVE, 20L))
             	.addProperty(new Property(null, "PublicationDate", ValueType.PRIMITIVE, TimeUtil.convertStringToTimestamp("2014-01-02T00:00:00.123Z", dateTimeFormatter)))
             	.addProperty(new Property(null, "Satellite", ValueType.PRIMITIVE, "S2A"))
             	.addProperty(new Property(null, "StationUnitId", ValueType.PRIMITIVE, "234"))
             	.addProperty(new Property(null, "DownlinkOrbit", ValueType.PRIMITIVE, 234L))
             	.addProperty(new Property(null, "AcquisitionId", ValueType.PRIMITIVE, "234"))
             	.addProperty(new Property(null, "AntennaId", ValueType.PRIMITIVE, "234"))
             	.addProperty(new Property(null, "FrontEndId", ValueType.PRIMITIVE, "234"))
             	.addProperty(new Property(null, "Retransfer", ValueType.PRIMITIVE, false))
             	.addProperty(new Property(null, "AntennaStatusOK", ValueType.PRIMITIVE, true))
             	.addProperty(new Property(null, "FrontEndStatusOK", ValueType.PRIMITIVE, true))
             	.addProperty(new Property(null, "PlannedDataStart", ValueType.PRIMITIVE, TimeUtil.convertStringToTimestamp("2014-01-02T01:20:00.000Z", dateTimeFormatter)))
             	.addProperty(new Property(null, "PlannedDataStop", ValueType.PRIMITIVE, TimeUtil.convertStringToTimestamp("2014-01-02T01:30:00.000Z", dateTimeFormatter)))
             	.addProperty(new Property(null, "DownlinkStart", ValueType.PRIMITIVE, TimeUtil.convertStringToTimestamp("2014-01-02T02:10:00.000Z", dateTimeFormatter)))
             	.addProperty(new Property(null, "DownlinkStop", ValueType.PRIMITIVE, TimeUtil.convertStringToTimestamp("2014-01-02T02:21:00.000Z", dateTimeFormatter)))
             	.addProperty(new Property(null, "DownlinkStatusOK", ValueType.PRIMITIVE, true))
             	.addProperty(new Property(null, "DeliveryPushOK", ValueType.PRIMITIVE, true));
        sE2.setId(createId("Sessions", 2));
        sessionsList.add(sE2);
        
        final Entity sE3 = new Entity()
     		   .addProperty(new Property(null, "Id", ValueType.PRIMITIVE, UUID.fromString("00000000-0000-0000-0000-00003")))
                .addProperty(new Property(null, "SessionId", ValueType.PRIMITIVE, "3"))
             	.addProperty(new Property(null, "NumChannels", ValueType.PRIMITIVE, 30L))
             	.addProperty(new Property(null, "PublicationDate", ValueType.PRIMITIVE, TimeUtil.convertStringToTimestamp("2014-01-03T00:00:00.123Z", dateTimeFormatter)))
             	.addProperty(new Property(null, "Satellite", ValueType.PRIMITIVE, "S3A"))
             	.addProperty(new Property(null, "StationUnitId", ValueType.PRIMITIVE, "345"))
             	.addProperty(new Property(null, "DownlinkOrbit", ValueType.PRIMITIVE, 345L))
             	.addProperty(new Property(null, "AcquisitionId", ValueType.PRIMITIVE, "345"))
             	.addProperty(new Property(null, "AntennaId", ValueType.PRIMITIVE, "345"))
             	.addProperty(new Property(null, "FrontEndId", ValueType.PRIMITIVE, "345"))
             	.addProperty(new Property(null, "Retransfer", ValueType.PRIMITIVE, false))
             	.addProperty(new Property(null, "AntennaStatusOK", ValueType.PRIMITIVE, true))
             	.addProperty(new Property(null, "FrontEndStatusOK", ValueType.PRIMITIVE, true))
             	.addProperty(new Property(null, "PlannedDataStart", ValueType.PRIMITIVE, TimeUtil.convertStringToTimestamp("2014-01-03T01:20:00.000Z", dateTimeFormatter)))
             	.addProperty(new Property(null, "PlannedDataStop", ValueType.PRIMITIVE, TimeUtil.convertStringToTimestamp("2014-01-03T01:30:00.000Z", dateTimeFormatter)))
             	.addProperty(new Property(null, "DownlinkStart", ValueType.PRIMITIVE, TimeUtil.convertStringToTimestamp("2014-01-03T02:10:00.000Z", dateTimeFormatter)))
             	.addProperty(new Property(null, "DownlinkStop", ValueType.PRIMITIVE, TimeUtil.convertStringToTimestamp("2014-01-03T02:21:00.000Z", dateTimeFormatter)))
             	.addProperty(new Property(null, "DownlinkStatusOK", ValueType.PRIMITIVE, true))
             	.addProperty(new Property(null, "DeliveryPushOK", ValueType.PRIMITIVE, true));
        sE3.setId(createId("Sessions", 3));
        sessionsList.add(sE3);
        
        final Entity fE1 = new Entity()
        		.addProperty(new Property(null, "Id", ValueType.PRIMITIVE, UUID.fromString("00000000-0000-0000-0000-00001")))
        		.addProperty(new Property(null, "Name", ValueType.PRIMITIVE, "blub"))
        		.addProperty(new Property(null, "SessionId", ValueType.PRIMITIVE, "1"))
        		.addProperty(new Property(null, "Channel", ValueType.PRIMITIVE, 1L))
        		.addProperty(new Property(null, "FinalBlock", ValueType.PRIMITIVE, false))
        		.addProperty(new Property(null, "PublicationDate", ValueType.PRIMITIVE, TimeUtil.convertStringToTimestamp("2014-01-03T01:00:00.123Z", dateTimeFormatter)))
        		.addProperty(new Property(null, "EvictionDate", ValueType.PRIMITIVE, TimeUtil.convertStringToTimestamp("2015-01-03T00:00:00.123Z", dateTimeFormatter)))
        		.addProperty(new Property(null, "Size", ValueType.PRIMITIVE, 100L))
        		.addProperty(new Property(null, "Retransfer", ValueType.PRIMITIVE, false));
        fE1.setId(createId("Files", 1));
        filesList.add(fE1);
        
        final Entity fE2 = new Entity()
        		.addProperty(new Property(null, "Id", ValueType.PRIMITIVE, UUID.fromString("00000000-0000-0000-0000-00002")))
        		.addProperty(new Property(null, "Name", ValueType.PRIMITIVE, "blab"))
        		.addProperty(new Property(null, "SessionId", ValueType.PRIMITIVE, "2"))
        		.addProperty(new Property(null, "Channel", ValueType.PRIMITIVE, 2L))
        		.addProperty(new Property(null, "FinalBlock", ValueType.PRIMITIVE, false))
        		.addProperty(new Property(null, "PublicationDate", ValueType.PRIMITIVE, TimeUtil.convertStringToTimestamp("2014-01-03T02:00:00.123Z", dateTimeFormatter)))
        		.addProperty(new Property(null, "EvictionDate", ValueType.PRIMITIVE, TimeUtil.convertStringToTimestamp("2016-01-03T00:00:00.123Z", dateTimeFormatter)))
        		.addProperty(new Property(null, "Size", ValueType.PRIMITIVE, 200L))
        		.addProperty(new Property(null, "Retransfer", ValueType.PRIMITIVE, false));
        fE2.setId(createId("Files", 2));
        filesList.add(fE2);
        
        final Entity fE3 = new Entity()
        		.addProperty(new Property(null, "Id", ValueType.PRIMITIVE, UUID.fromString("00000000-0000-0000-0000-00003")))
        		.addProperty(new Property(null, "Name", ValueType.PRIMITIVE, "blab"))
        		.addProperty(new Property(null, "SessionId", ValueType.PRIMITIVE, "3"))
        		.addProperty(new Property(null, "Channel", ValueType.PRIMITIVE, 3L))
        		.addProperty(new Property(null, "FinalBlock", ValueType.PRIMITIVE, false))
        		.addProperty(new Property(null, "PublicationDate", ValueType.PRIMITIVE, TimeUtil.convertStringToTimestamp("2014-01-03T03:00:00.123Z", dateTimeFormatter)))
        		.addProperty(new Property(null, "EvictionDate", ValueType.PRIMITIVE, TimeUtil.convertStringToTimestamp("2017-01-03T00:00:00.123Z", dateTimeFormatter)))
        		.addProperty(new Property(null, "Size", ValueType.PRIMITIVE, 300L))
        		.addProperty(new Property(null, "Retransfer", ValueType.PRIMITIVE, false));
        fE3.setId(createId("Files", 3));
        filesList.add(fE3);
    }

    private URI createId(String entitySetName, Object id) {
        try {
            return new URI(entitySetName + "(" + String.valueOf(id) + ")");
        } catch (URISyntaxException e) {
            throw new ODataRuntimeException("Unable to create id for entity: " + entitySetName, e);
        }
    }
}