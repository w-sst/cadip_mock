package de.werum.coprs.cadip.cadip_mock.data;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
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

public class Storage {

    private List<Entity> productList;

    public Storage() {
        productList = new ArrayList<Entity>();
        initSampleData();
    }

    /* PUBLIC FACADE */

    public EntityCollection readEntitySetData(EdmEntitySet edmEntitySet)throws ODataApplicationException{

        // actually, this is only required if we have more than one Entity Sets
        if(edmEntitySet.getName().equals(EdmProvider.ES_SESSIONS_NAME)){
            return getProducts();
        }

        return null;
    }

    public Entity readEntityData(EdmEntitySet edmEntitySet, List<UriParameter> keyParams) throws ODataApplicationException{

        EdmEntityType edmEntityType = edmEntitySet.getEntityType();

        // actually, this is only required if we have more than one Entity Type
        if(edmEntityType.getName().equals(EdmProvider.ET_SESSION_NAME)){
            return getProduct(edmEntityType, keyParams);
        }

        return null;
    }



    /*  INTERNAL */

    private EntityCollection getProducts(){
        EntityCollection retEntitySet = new EntityCollection();

        for(Entity productEntity : this.productList){
            retEntitySet.getEntities().add(productEntity);
        }

        return retEntitySet;
    }


    private Entity getProduct(EdmEntityType edmEntityType, List<UriParameter> keyParams) throws ODataApplicationException{

        // the list of entities at runtime
        EntityCollection entitySet = getProducts();

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

    	 DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
 		// LocalDateTime source = LocalDateTime.parse("2000-01-01T00:00:00.123456Z", dateTimeFormatter);
 		// Timestamp destination = MappingUtil.convertLocalDateTimeToTimestamp(source);
         
         final Entity e1 = new Entity()
            .addProperty(new Property(null, "Id", ValueType.PRIMITIVE, UUID.fromString("00000000-0000-0000-0000-00001")))
            .addProperty(new Property(null, "SessionId", ValueType.PRIMITIVE, "1"))
         	.addProperty(new Property(null, "NumChannels", ValueType.PRIMITIVE, 10))
         	.addProperty(new Property(null, "PublicationDate", ValueType.PRIMITIVE, convertStringToTimestamp("2014-01-01T00:00:00.123Z", dateTimeFormatter)))
         	.addProperty(new Property(null, "Satellite", ValueType.PRIMITIVE, "S1A"))
         	.addProperty(new Property(null, "StationUnitId", ValueType.PRIMITIVE, "123"))
         	.addProperty(new Property(null, "DownlinkOrbit", ValueType.PRIMITIVE, 123))
         	.addProperty(new Property(null, "AcquisitionId", ValueType.PRIMITIVE, "123"))
         	.addProperty(new Property(null, "AntennaId", ValueType.PRIMITIVE, "123"))
         	.addProperty(new Property(null, "FrontEndId", ValueType.PRIMITIVE, "123"))
         	.addProperty(new Property(null, "Retransfer", ValueType.PRIMITIVE, false))
         	.addProperty(new Property(null, "AntennaStatusOK", ValueType.PRIMITIVE, true))
         	.addProperty(new Property(null, "FrontEndStatusOK", ValueType.PRIMITIVE, true))
         	.addProperty(new Property(null, "PlannedDataStart", ValueType.PRIMITIVE, convertStringToTimestamp("2014-01-01T01:20:00.000Z", dateTimeFormatter)))
         	.addProperty(new Property(null, "PlannedDataStop", ValueType.PRIMITIVE, convertStringToTimestamp("2014-01-01T01:30:00.000Z", dateTimeFormatter)))
         	.addProperty(new Property(null, "DownlinkStart", ValueType.PRIMITIVE, convertStringToTimestamp("2014-01-01T02:10:00.000Z", dateTimeFormatter)))
         	.addProperty(new Property(null, "DownlinkStop", ValueType.PRIMITIVE, convertStringToTimestamp("2014-01-01T02:21:00.000Z", dateTimeFormatter)))
         	.addProperty(new Property(null, "DownlinkStatusOK", ValueType.PRIMITIVE, true))
         	.addProperty(new Property(null, "DeliveryPushOK", ValueType.PRIMITIVE, true));
        e1.setId(createId("Sessions", 1));
        productList.add(e1);

        final Entity e2 = new Entity()
     		   .addProperty(new Property(null, "Id", ValueType.PRIMITIVE, UUID.fromString("00000000-0000-0000-0000-00002")))
                .addProperty(new Property(null, "SessionId", ValueType.PRIMITIVE, "2"))
             	.addProperty(new Property(null, "NumChannels", ValueType.PRIMITIVE, 20))
             	.addProperty(new Property(null, "PublicationDate", ValueType.PRIMITIVE, convertStringToTimestamp("2014-01-02T00:00:00.123Z", dateTimeFormatter)))
             	.addProperty(new Property(null, "Satellite", ValueType.PRIMITIVE, "S1A"))
             	.addProperty(new Property(null, "StationUnitId", ValueType.PRIMITIVE, "234"))
             	.addProperty(new Property(null, "DownlinkOrbit", ValueType.PRIMITIVE, 234))
             	.addProperty(new Property(null, "AcquisitionId", ValueType.PRIMITIVE, "234"))
             	.addProperty(new Property(null, "AntennaId", ValueType.PRIMITIVE, "234"))
             	.addProperty(new Property(null, "FrontEndId", ValueType.PRIMITIVE, "234"))
             	.addProperty(new Property(null, "Retransfer", ValueType.PRIMITIVE, false))
             	.addProperty(new Property(null, "AntennaStatusOK", ValueType.PRIMITIVE, true))
             	.addProperty(new Property(null, "FrontEndStatusOK", ValueType.PRIMITIVE, true))
             	.addProperty(new Property(null, "PlannedDataStart", ValueType.PRIMITIVE, convertStringToTimestamp("2014-01-02T01:20:00.000Z", dateTimeFormatter)))
             	.addProperty(new Property(null, "PlannedDataStop", ValueType.PRIMITIVE, convertStringToTimestamp("2014-01-02T01:30:00.000Z", dateTimeFormatter)))
             	.addProperty(new Property(null, "DownlinkStart", ValueType.PRIMITIVE, convertStringToTimestamp("2014-01-02T02:10:00.000Z", dateTimeFormatter)))
             	.addProperty(new Property(null, "DownlinkStop", ValueType.PRIMITIVE, convertStringToTimestamp("2014-01-02T02:21:00.000Z", dateTimeFormatter)))
             	.addProperty(new Property(null, "DownlinkStatusOK", ValueType.PRIMITIVE, true))
             	.addProperty(new Property(null, "DeliveryPushOK", ValueType.PRIMITIVE, true));
        e2.setId(createId("Sessions", 2));
        productList.add(e2);
        
        final Entity e3 = new Entity()
     		   .addProperty(new Property(null, "Id", ValueType.PRIMITIVE, UUID.fromString("00000000-0000-0000-0000-00003")))
                .addProperty(new Property(null, "SessionId", ValueType.PRIMITIVE, "3"))
             	.addProperty(new Property(null, "NumChannels", ValueType.PRIMITIVE, 30))
             	.addProperty(new Property(null, "PublicationDate", ValueType.PRIMITIVE, convertStringToTimestamp("2014-01-03T00:00:00.123Z", dateTimeFormatter)))
             	.addProperty(new Property(null, "Satellite", ValueType.PRIMITIVE, "S1A"))
             	.addProperty(new Property(null, "StationUnitId", ValueType.PRIMITIVE, "345"))
             	.addProperty(new Property(null, "DownlinkOrbit", ValueType.PRIMITIVE, 345))
             	.addProperty(new Property(null, "AcquisitionId", ValueType.PRIMITIVE, "345"))
             	.addProperty(new Property(null, "AntennaId", ValueType.PRIMITIVE, "345"))
             	.addProperty(new Property(null, "FrontEndId", ValueType.PRIMITIVE, "345"))
             	.addProperty(new Property(null, "Retransfer", ValueType.PRIMITIVE, false))
             	.addProperty(new Property(null, "AntennaStatusOK", ValueType.PRIMITIVE, true))
             	.addProperty(new Property(null, "FrontEndStatusOK", ValueType.PRIMITIVE, true))
             	.addProperty(new Property(null, "PlannedDataStart", ValueType.PRIMITIVE, convertStringToTimestamp("2014-01-03T01:20:00.000Z", dateTimeFormatter)))
             	.addProperty(new Property(null, "PlannedDataStop", ValueType.PRIMITIVE, convertStringToTimestamp("2014-01-03T01:30:00.000Z", dateTimeFormatter)))
             	.addProperty(new Property(null, "DownlinkStart", ValueType.PRIMITIVE, convertStringToTimestamp("2014-01-03T02:10:00.000Z", dateTimeFormatter)))
             	.addProperty(new Property(null, "DownlinkStop", ValueType.PRIMITIVE, convertStringToTimestamp("2014-01-03T02:21:00.000Z", dateTimeFormatter)))
             	.addProperty(new Property(null, "DownlinkStatusOK", ValueType.PRIMITIVE, true))
             	.addProperty(new Property(null, "DeliveryPushOK", ValueType.PRIMITIVE, true));
        e3.setId(createId("Sessions", 3));
        productList.add(e3);
    }

    private URI createId(String entitySetName, Object id) {
        try {
            return new URI(entitySetName + "(" + String.valueOf(id) + ")");
        } catch (URISyntaxException e) {
            throw new ODataRuntimeException("Unable to create id for entity: " + entitySetName, e);
        }
    }
    
    public static Timestamp convertStringToTimestamp(String dateString, DateTimeFormatter dateTimeFormatter) {
		return convertLocalDateTimeToTimestamp(LocalDateTime.parse(dateString, dateTimeFormatter));
	}
    
    // aus /rs-core-prip-frontend/src/main/java/esa/s1pdgs/cpoc/prip/frontend/service/mapping/MappingUtil.java
 	public static Timestamp convertLocalDateTimeToTimestamp(LocalDateTime localDateTime) {
 		if (null != localDateTime) {
 			try {
 				Instant instant = localDateTime.toInstant(ZoneOffset.UTC);
 				Timestamp stamp = new Timestamp(instant.getEpochSecond() * 1000);
 				stamp.setNanos(instant.getNano() / 1000000 * 1000000); // results in cutting off places
 				return stamp;
 			} catch (ArithmeticException ex) {
 				throw new IllegalArgumentException(ex);
 			}
 		} else {
 			return null;
 		}
 	}
}