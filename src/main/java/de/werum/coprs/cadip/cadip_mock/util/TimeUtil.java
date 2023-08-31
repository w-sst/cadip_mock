package de.werum.coprs.cadip.cadip_mock.util;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class TimeUtil {
    public static Timestamp convertStringToTimestamp(String dateString, DateTimeFormatter dateTimeFormatter) {
		return convertLocalDateTimeToTimestamp(LocalDateTime.parse(dateString, dateTimeFormatter));
	}
    
    // aus /rs-core-prip-frontend/src/main/java/esa/s1pdgs/cpoc/prip/frontend/service/mapping/MappingUtil.java
 	public static Timestamp convertLocalDateTimeToTimestamp(LocalDateTime localDateTime) {
 		if (null != localDateTime) {
 			try {
 				Instant instant = localDateTime.toInstant(ZoneOffset.UTC);
 				Timestamp stamp = new Timestamp(instant.getEpochSecond() * 1000);
 				stamp.setNanos(instant.getNano() / 1000 * 1000); // results in cutting off places
 				return stamp;
 			} catch (ArithmeticException ex) {
 				throw new IllegalArgumentException(ex);
 			}
 		} else {
 			return null;
 		}
 	}
}
