package de.werum.coprs.cadip.cadip_mock.util;

import java.util.List;
import java.util.Locale;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.uri.UriInfoResource;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;

public class OlingoUtil {

	public static EdmEntitySet getEdmEntitySet(UriInfoResource uriInfo) throws ODataApplicationException {

		List<UriResource> resourcePaths = uriInfo.getUriResourceParts();

		if (!(resourcePaths.get(0) instanceof UriResourceEntitySet)) {
			throw new ODataApplicationException("Invalid resource type for first segment.",
					HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(),
					Locale.ENGLISH);
		}

		UriResourceEntitySet uriResource = (UriResourceEntitySet) resourcePaths.get(0);

		return uriResource.getEntitySet();
	}

	public static Entity findEntity(EdmEntityType edmEntityType, EntityCollection rt_entitySet,
			List<UriParameter> keyParams) throws ODataApplicationException {

		List<Entity> entityList = rt_entitySet.getEntities();

		for (Entity rt_entity : entityList) {
			boolean foundEntity = entityMatchesAllKeys(edmEntityType, rt_entity, keyParams);
			if (foundEntity) {
				return rt_entity;
			}
		}

		return null;
	}

	public static boolean entityMatchesAllKeys(EdmEntityType edmEntityType, Entity rt_entity,
			List<UriParameter> keyParams) throws ODataApplicationException {

		for (final UriParameter key : keyParams) {
			String keyName = key.getName();
			String keyText = key.getText();

			EdmProperty edmKeyProperty = (EdmProperty) edmEntityType.getProperty(keyName);
			Boolean isNullable = edmKeyProperty.isNullable();
			Integer maxLength = edmKeyProperty.getMaxLength();
			Integer precision = edmKeyProperty.getPrecision();
			Boolean isUnicode = edmKeyProperty.isUnicode();
			Integer scale = edmKeyProperty.getScale();

			EdmType edmType = edmKeyProperty.getType();
			EdmPrimitiveType edmPrimitiveType = (EdmPrimitiveType) edmType;

			Object valueObject = rt_entity.getProperty(keyName).getValue();

			String valueAsString = null;
			try {
				valueAsString = edmPrimitiveType.valueToString(valueObject, isNullable, maxLength, precision, scale,
						isUnicode);
			} catch (EdmPrimitiveTypeException e) {
				throw new ODataApplicationException("Failed to retrieve String value",
						HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(),
						Locale.ENGLISH,
						e);
			}

			if (valueAsString == null) {
				return false;
			}

			boolean matches = valueAsString.equals(keyText);
			if (!matches) {
				return false;
			}
		}

		return true;
	}
}
