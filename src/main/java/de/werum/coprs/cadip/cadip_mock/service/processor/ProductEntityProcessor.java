package de.werum.coprs.cadip.cadip_mock.service.processor;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataLibraryException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.processor.EntityProcessor;
import org.apache.olingo.server.api.processor.MediaEntityProcessor;
import org.apache.olingo.server.api.serializer.EntitySerializerOptions;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.SerializerResult;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import de.werum.coprs.cadip.cadip_mock.data.Storage;
import de.werum.coprs.cadip.cadip_mock.data.model.File;
import de.werum.coprs.cadip.cadip_mock.service.edm.EdmProvider;
import de.werum.coprs.cadip.cadip_mock.util.OlingoUtil;

public class ProductEntityProcessor implements EntityProcessor, MediaEntityProcessor {

	private OData odata;
	private ServiceMetadata serviceMetadata;
	private Storage storage;

	public ProductEntityProcessor(Storage storage) {
		this.storage = storage;
	}
	
	@Override
	public void init(OData odata, ServiceMetadata serviceMetadata) {
		this.odata = odata;
		this.serviceMetadata = serviceMetadata;
		
	}
	@Override
	public void readEntity(ODataRequest request, ODataResponse response, UriInfo uriInfo, ContentType responseFormat)
			throws ODataApplicationException, ODataLibraryException {
		
    	// 1. retrieve the Entity Type
    	List<UriResource> resourcePaths = uriInfo.getUriResourceParts();
    	// Note: only in our example we can assume that the first segment is the EntitySet
    	UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) resourcePaths.get(0);
    	EdmEntitySet edmEntitySet = uriResourceEntitySet.getEntitySet();

    	// 2. retrieve the data from backend
    	List<UriParameter> keyPredicates = uriResourceEntitySet.getKeyPredicates();
    	Entity entity = storage.readEntityData(edmEntitySet, keyPredicates);

    	// 3. serialize
    	EdmEntityType entityType = edmEntitySet.getEntityType();

    	ContextURL contextUrl = ContextURL.with().entitySet(edmEntitySet).build();
        // expand and select currently not supported
    	EntitySerializerOptions options = EntitySerializerOptions.with().contextURL(contextUrl).build();

    	ODataSerializer serializer = odata.createSerializer(responseFormat);
    	SerializerResult serializerResult = serializer.entity(serviceMetadata, entityType, entity, options);
    	InputStream entityStream = serializerResult.getContent();

    	//4. configure the response object
    	response.setContent(entityStream);
    	response.setStatusCode(HttpStatusCode.OK.getStatusCode());
    	response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
		
	}
	@Override
	public void readMediaEntity(ODataRequest request, ODataResponse response, UriInfo uriInfo,
			ContentType responseFormat) throws ODataApplicationException, ODataLibraryException {
		// Since our scenario do not contain navigations from media entities. We can
		// keep things simple and check only the first resource path of the URI.
		LocalDateTime t1 = LocalDateTime.now();
		final UriResource firstResoucePart = uriInfo.getUriResourceParts().get(0);
		
		final EdmEntitySet edmEntitySet = OlingoUtil.getEdmEntitySet(uriInfo);
		String entityName = edmEntitySet.getEntityType().getName();
		if (firstResoucePart instanceof UriResourceEntitySet && entityName.equals(EdmProvider.ET_FILE_NAME)) {

			final UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) firstResoucePart;

			File file = storage.getFile(uriResourceEntitySet.getKeyPredicates().get(0).getText());
			if (file == null) {
				throw new ODataApplicationException("File not found", HttpStatusCode.NOT_FOUND.getStatusCode(),
						Locale.ENGLISH);
			}

			InputStream fileStream = null;
			try {
				String rangeContent = request.getHeader("Range");
				if (rangeContent != null) {
					try {
						int mid = rangeContent.indexOf("-");
						int start = rangeContent.indexOf("=");
						String f = rangeContent.substring(start + 1, mid);
						long from = Long.parseLong(f);
						String t = rangeContent.substring(mid + 1, rangeContent.length());
						long to = Long.parseLong(t) - from + 1;
						fileStream = storage.readMedia(file.getFilePath(), from, to);
					} catch (NumberFormatException | IndexOutOfBoundsException | NegativeArraySizeException e) {
						e.printStackTrace();
						throw new ODataApplicationException("Bad request", HttpStatusCode.BAD_REQUEST.getStatusCode(),
								Locale.ENGLISH);
					}
				} else {
					fileStream = storage.readMedia(file.getFilePath());
				}
			} catch (FileNotFoundException e) {
				throw new ODataApplicationException("File not found", HttpStatusCode.NOT_FOUND.getStatusCode(),
						Locale.ENGLISH);
			}
			
			LocalDateTime t2 = LocalDateTime.now();
			System.out.println(t1 + " != " + t2);
			
			response.setStatusCode(HttpStatusCode.OK.getStatusCode());
			response.setContent(fileStream);
			response.setHeader(HttpHeader.CONTENT_TYPE, "application/octet-stream");
			response.setHeader("Content-disposition", "attachment; filename=" + file.getName());
		} else {
			throw new ODataApplicationException("Not implemented", HttpStatusCode.BAD_REQUEST.getStatusCode(),
					Locale.ENGLISH);
		}
	}

	@Override
	public void createEntity(ODataRequest request, ODataResponse response, UriInfo uriInfo, ContentType requestFormat,
			ContentType responseFormat) throws ODataApplicationException, ODataLibraryException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void updateEntity(ODataRequest request, ODataResponse response, UriInfo uriInfo, ContentType requestFormat,
			ContentType responseFormat) throws ODataApplicationException, ODataLibraryException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void deleteEntity(ODataRequest request, ODataResponse response, UriInfo uriInfo)
			throws ODataApplicationException, ODataLibraryException {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void createMediaEntity(ODataRequest request, ODataResponse response, UriInfo uriInfo,
			ContentType requestFormat, ContentType responseFormat)
			throws ODataApplicationException, ODataLibraryException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateMediaEntity(ODataRequest request, ODataResponse response, UriInfo uriInfo,
			ContentType requestFormat, ContentType responseFormat)
			throws ODataApplicationException, ODataLibraryException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteMediaEntity(ODataRequest request, ODataResponse response, UriInfo uriInfo)
			throws ODataApplicationException, ODataLibraryException {
		// TODO Auto-generated method stub
		
	}
	
}
