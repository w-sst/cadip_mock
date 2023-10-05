package de.werum.coprs.cadip.cadip_mock.service.processor;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.ContextURL.Suffix;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Link;
import org.apache.olingo.commons.api.edm.EdmElement;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.edm.EdmNavigationPropertyBinding;
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
import org.apache.olingo.server.api.uri.UriResourceNavigation;
import org.apache.olingo.server.api.uri.queryoption.ExpandItem;
import org.apache.olingo.server.api.uri.queryoption.ExpandOption;

import de.werum.coprs.cadip.cadip_mock.data.Storage;
import de.werum.coprs.cadip.cadip_mock.data.model.File;
import de.werum.coprs.cadip.cadip_mock.service.edm.EdmProvider;
import de.werum.coprs.cadip.cadip_mock.util.OlingoUtil;

public class ProductEntityProcessor implements EntityProcessor, MediaEntityProcessor {

	private static final Logger LOG = LogManager.getLogger(ProductEntityProcessor.class);

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

		UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) resourcePaths.get(0);
		EdmEntitySet edmEntitySet = uriResourceEntitySet.getEntitySet();

		LOG.debug("Request for Entity: {}", request.getRawRequestUri());

		List<UriParameter> keyPredicates = uriResourceEntitySet.getKeyPredicates();
		Entity entity = storage.readEntityData(edmEntitySet, keyPredicates);

		ExpandOption expandOption = uriInfo.getExpandOption();
		if (expandOption != null) {
			List<ExpandItem> expandItems = new ArrayList<ExpandItem>();
			expandItems.addAll(expandOption.getExpandItems());

			ExpandItem expandItem = expandOption.getExpandItems().get(0);
			List<EdmNavigationProperty> edmNavigationProperties = new ArrayList<EdmNavigationProperty>();
			if (expandItem.isStar()) {
				List<EdmNavigationPropertyBinding> bindings = edmEntitySet.getNavigationPropertyBindings();
				for (EdmNavigationPropertyBinding binding : bindings) {
					EdmElement property = edmEntitySet.getEntityType().getProperty(binding.getPath());
					if (property instanceof EdmNavigationProperty) {
						edmNavigationProperties.add((EdmNavigationProperty) property);
					}
				}
			} else {
				UriResource expandUriResource = expandItem.getResourcePath().getUriResourceParts().get(0);
				if (expandUriResource instanceof UriResourceNavigation) {
					edmNavigationProperties.add(((UriResourceNavigation) expandUriResource).getProperty());
				}
			}

			if (edmNavigationProperties.size() > 0) {
				for (EdmNavigationProperty edmNavigationProperty : edmNavigationProperties) {
					EdmEntityType expandEdmEntityType = edmNavigationProperty.getType();
					String navPropName = edmNavigationProperty.getName();

					Link link = new Link();
					link.setTitle(navPropName);
					link.setRel("TODO");
					if (edmNavigationProperty.isCollection()) {
						EntityCollection expandEntityCollection = storage.getEntitiesForSession(entity,
								expandEdmEntityType);
						if (expandEntityCollection != null) {
							link.setInlineEntitySet(expandEntityCollection);
						}
					} else {
						Entity expandEntity = storage.getEntityForSession(entity, expandEdmEntityType);
						if (expandEntity != null) {
							link.setInlineEntity(expandEntity);
						}
					}
					entity.getNavigationLinks().add(link);
				}
			}

		}

		// convert entity to InputStream
		EdmEntityType entityType = edmEntitySet.getEntityType();

		String selectList = odata.createUriHelper().buildContextURLSelectList(entityType, expandOption, null);
		ContextURL contextUrl = ContextURL.with().entitySet(edmEntitySet).selectList(selectList).suffix(Suffix.ENTITY)
				.build();

		EntitySerializerOptions serializerOptions = EntitySerializerOptions.with().contextURL(contextUrl)
				.expand(expandOption).build();

		ODataSerializer serializer = odata.createSerializer(responseFormat);
		SerializerResult serializerResult = serializer.entity(serviceMetadata, entityType, entity, serializerOptions);
		InputStream entityStream = serializerResult.getContent();

		// set results as response
		response.setContent(entityStream);
		response.setStatusCode(HttpStatusCode.OK.getStatusCode());
		response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());

	}

	@Override
	public void readMediaEntity(ODataRequest request, ODataResponse response, UriInfo uriInfo,
			ContentType responseFormat) throws ODataApplicationException, ODataLibraryException {
		final UriResource firstResoucePart = uriInfo.getUriResourceParts().get(0);

		final EdmEntitySet edmEntitySet = OlingoUtil.getEdmEntitySet(uriInfo);
		String entityName = edmEntitySet.getEntityType().getName();
		if (firstResoucePart instanceof UriResourceEntitySet && entityName.equals(EdmProvider.ET_FILE_NAME)) {

			final UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) firstResoucePart;

			File file = storage.getFile(uriResourceEntitySet.getKeyPredicates().get(0).getText());
			if (file == null) {
				throw new ODataApplicationException("File not found",
						HttpStatusCode.NOT_FOUND.getStatusCode(),
						Locale.ENGLISH);
			}
			LOG.debug("Request to download {}: {}", file.getName(), request.getRawRequestUri());

			InputStream fileStream;
			try {
				String rangeContent = request.getHeader("Range");
				if (rangeContent != null) {
					try {
						int mid = rangeContent.indexOf("-");
						int start = rangeContent.indexOf("=");
						String f = rangeContent.substring(start + 1, mid);
						long from = Long.parseLong(f);
						String t = rangeContent.substring(mid + 1, rangeContent.length());
						long length = Long.parseLong(t) - from + 1;
						fileStream = storage.readMedia(file.getFilePath(), from, length);
					} catch (NumberFormatException | IndexOutOfBoundsException | NegativeArraySizeException e) {
						e.printStackTrace();
						throw new ODataApplicationException("Bad request",
								HttpStatusCode.BAD_REQUEST.getStatusCode(),
								Locale.ENGLISH);
					}
				} else {
					fileStream = storage.readMedia(file.getFilePath());
				}
			} catch (FileNotFoundException e) {
				throw new ODataApplicationException("File not found",
						HttpStatusCode.NOT_FOUND.getStatusCode(),
						Locale.ENGLISH);
			}

			response.setStatusCode(HttpStatusCode.OK.getStatusCode());
			response.setContent(fileStream);
			response.setHeader(HttpHeader.CONTENT_TYPE, "application/octet-stream");
			response.setHeader("Content-disposition", "attachment; filename=" + file.getName());
		} else {
			throw new ODataApplicationException("Not implemented",
					HttpStatusCode.BAD_REQUEST.getStatusCode(),
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
