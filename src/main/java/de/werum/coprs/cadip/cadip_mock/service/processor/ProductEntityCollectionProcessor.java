package de.werum.coprs.cadip.cadip_mock.service.processor;

import java.io.InputStream;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataLibraryException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.processor.EntityCollectionProcessor;
import org.apache.olingo.server.api.serializer.EntityCollectionSerializerOptions;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.SerializerResult;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriInfoResource;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.apache.olingo.server.api.uri.UriResourceNavigation;
import org.apache.olingo.server.api.uri.UriResourcePrimitiveProperty;
import org.apache.olingo.server.api.uri.queryoption.CountOption;
import org.apache.olingo.server.api.uri.queryoption.FilterOption;
import org.apache.olingo.server.api.uri.queryoption.OrderByItem;
import org.apache.olingo.server.api.uri.queryoption.OrderByOption;
import org.apache.olingo.server.api.uri.queryoption.SkipOption;
import org.apache.olingo.server.api.uri.queryoption.TopOption;
import org.apache.olingo.server.api.uri.queryoption.expression.Expression;
import org.apache.olingo.server.api.uri.queryoption.expression.ExpressionVisitException;
import org.apache.olingo.server.api.uri.queryoption.expression.Member;
import de.werum.coprs.cadip.cadip_mock.data.Storage;

public class ProductEntityCollectionProcessor implements EntityCollectionProcessor {

	private static final Logger LOG = LogManager.getLogger(ProductEntityCollectionProcessor.class);

	private OData odata;
	private ServiceMetadata serviceMetadata;
	private Storage storage;

	public ProductEntityCollectionProcessor(Storage storage) {
		this.storage = storage;
	}

	public void init(OData odata, ServiceMetadata serviceMetadata) {
		this.odata = odata;
		this.serviceMetadata = serviceMetadata;
	}

	@Override
	public void readEntityCollection(ODataRequest request, ODataResponse response, UriInfo uriInfo,
			ContentType responseFormat) throws ODataApplicationException, ODataLibraryException {
		// read infos from oData request
		List<UriResource> resourcePaths = uriInfo.getUriResourceParts();
		int segmentCount = resourcePaths.size();
		UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) resourcePaths.get(0);

		EdmEntitySet firstEdmEntitySet = uriResourceEntitySet.getEntitySet();
		EdmEntitySet edmEntitySet;

		List<Entity> entityList;
		EntityCollection entitySet;
		// segmentCount == 1 means no navigation was used
		if (segmentCount == 1) {
			edmEntitySet = firstEdmEntitySet;
			LOG.debug("Request for Collection: {}", request.getRawRequestUri());
			entitySet = storage.readEntitySetData(edmEntitySet);
			entityList = entitySet.getEntities();

			// segmentCount == 2 means to get the entities for a specific session
			// as "foreign key" is the sessionId used that all entities have
		} else if (segmentCount == 2) {
			LOG.debug("Request for Collection of a specific Session: {}", request.getRawRequestUri());
			UriResource lastSegment = resourcePaths.get(1);
			if (lastSegment instanceof UriResourceNavigation) {
				UriResourceNavigation uriResourceNavigation = (UriResourceNavigation) lastSegment;
				EdmNavigationProperty edmNavigationProperty = uriResourceNavigation.getProperty();
				EdmEntityType targetEntityType = edmNavigationProperty.getType();
				edmEntitySet = (EdmEntitySet) firstEdmEntitySet
						.getRelatedBindingTarget(edmNavigationProperty.getName());
				if (edmEntitySet == null) {
					throw new ODataApplicationException("Nothing found for given Navigation Set",
							HttpStatusCode.BAD_REQUEST.getStatusCode(),
							Locale.ENGLISH);
				}
				List<UriParameter> keyPredicates = uriResourceEntitySet.getKeyPredicates();
				Entity sourceEntity = storage.readEntityData(firstEdmEntitySet, keyPredicates);
				entitySet = storage.getEntitiesForSession(sourceEntity, targetEntityType);
				entityList = entitySet.getEntities();
			} else {
				throw new ODataApplicationException("Only Navigation is supported as 2. segment",
						HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(),
						Locale.ROOT);
			}
		} else {
			throw new ODataApplicationException("More than 2 Segments is not supported",
					HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(),
					Locale.ROOT);
		}

		// apply all query functions
		filterEntities(entityList, uriInfo.getFilterOption());
		orderEntities(entityList, uriInfo.getOrderByOption());
		skipEntities(entityList, uriInfo.getSkipOption());
		limitEntities(entityList, uriInfo.getTopOption());
		CountOption countOption = uriInfo.getCountOption();
		countEntities(entityList, entitySet, countOption);

		// convert Set to InputStream
		ODataSerializer serializer = odata.createSerializer(responseFormat);

		EdmEntityType edmEntityType = edmEntitySet.getEntityType();
		ContextURL contextUrl = ContextURL.with().entitySet(edmEntitySet).build();

		final String id = request.getRawBaseUri() + "/" + edmEntitySet.getName();
		EntityCollectionSerializerOptions opts = EntityCollectionSerializerOptions.with().id(id).count(countOption)
				.contextURL(contextUrl).build();
		SerializerResult serializerResult = serializer.entityCollection(serviceMetadata, edmEntityType, entitySet,
				opts);
		InputStream serializedContent = serializerResult.getContent();

		// set results as response
		response.setContent(serializedContent);
		response.setStatusCode(HttpStatusCode.OK.getStatusCode());
		response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());

	}

	private void filterEntities(List<Entity> entityList, FilterOption filterOption) throws ODataApplicationException {
		if (filterOption != null) {
			Expression filterExpression = filterOption.getExpression();
			try {
				Iterator<Entity> entityIterator = entityList.iterator();

				// calls the FilterExpressioVisitor for each entity and removes them from the
				// list if it returns false.
				while (entityIterator.hasNext()) {

					Entity currentEntity = entityIterator.next();
					FilterExpressionVisitor expressionVisitor = new FilterExpressionVisitor(currentEntity);

					Object visitorResult = filterExpression.accept(expressionVisitor);
					if (visitorResult instanceof Boolean) {
						if (!Boolean.TRUE.equals(visitorResult)) {
							entityIterator.remove();
						}
					} else {
						throw new ODataApplicationException("A filter expression must evaluate to type Edm.Boolean",
								HttpStatusCode.BAD_REQUEST.getStatusCode(),
								Locale.ENGLISH);
					}
				}
			} catch (ExpressionVisitException e) {
				throw new ODataApplicationException("Exception in filter evaluation",
						HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(),
						Locale.ENGLISH);
			}
		}
	}

	private void countEntities(List<Entity> entityList, EntityCollection entitySet, CountOption countOption) {
		if (countOption != null) {
			boolean isCount = countOption.getValue();
			if (isCount) {
				entitySet.setCount(entityList.size());
			}
		}
	}

	private void skipEntities(List<Entity> entityList, SkipOption skipOption) throws ODataApplicationException {
		if (skipOption != null) {
			int skipAmount = skipOption.getValue();
			if (skipAmount < 0) {
				throw new ODataApplicationException("Value for $skip cant be <0",
						HttpStatusCode.BAD_REQUEST.getStatusCode(),
						Locale.ENGLISH);
			}
			if (skipAmount > entityList.size()) {
				entityList.clear();
			} else {
				entityList.subList(0, skipAmount).clear();
			}
		}
	}

	private void limitEntities(List<Entity> entityList, TopOption topOption) throws ODataApplicationException {
		if (topOption != null) {
			int limit = topOption.getValue();
			if (limit < 0) {
				throw new ODataApplicationException("Value for $top cant be <0",
						HttpStatusCode.BAD_REQUEST.getStatusCode(),
						Locale.ENGLISH);
			}
			if (limit <= entityList.size()) {
				entityList.subList(limit, entityList.size()).clear();
			}
		}
	}

	private void orderEntities(List<Entity> entityList, OrderByOption orderByOption) {
		// if not specified, the Entities will be sorted by PublicationDate ascending
		if (orderByOption != null) {
			List<OrderByItem> orderItemList = orderByOption.getOrders();
			final OrderByItem orderByItem = orderItemList.get(0);
			Expression expression = orderByItem.getExpression();
			if (expression instanceof Member) {
				UriInfoResource resourcePath = ((Member) expression).getResourcePath();
				UriResource uriResource = resourcePath.getUriResourceParts().get(0);
				if (uriResource instanceof UriResourcePrimitiveProperty) {
					EdmProperty edmProperty = ((UriResourcePrimitiveProperty) uriResource).getProperty();

					final String sortPropertyName = edmProperty.getName();

					FullQualifiedName fqn = edmProperty.getType().getFullQualifiedName();
					EdmPrimitiveTypeKind typeOfProp = EdmPrimitiveTypeKind.valueOfFQN(fqn);

					sortEntities(entityList, sortPropertyName, typeOfProp, orderByItem.isDescending());
				}
			}
		} else {
			sortEntities(entityList, "PublicationDate", EdmPrimitiveTypeKind.DateTimeOffset, false);
		}
	}

	private void sortEntities(List<Entity> entityList, String sortPropertyName, EdmPrimitiveTypeKind typeOfProp,
			boolean isDescending) {

		Collections.sort(entityList, new Comparator<Entity>() {
			public int compare(Entity entity1, Entity entity2) {
				int compareResult = 0;

				Object left = entity1.getProperty(sortPropertyName).getValue();
				Object right = entity2.getProperty(sortPropertyName).getValue();
				int result = 0;
				switch (typeOfProp) {
				case DateTimeOffset:
					result = ((Timestamp) left).compareTo((Timestamp) right);
					break;
				case Int64:
					result = (((Long) left).compareTo((Long) right));
					break;
				case String:
					result = ((String) left).compareTo((String) right);
					break;
				case Boolean:
					result = ((Boolean) left).compareTo((Boolean) right);
					break;
				case Guid:
					result = ((UUID) left).compareTo((UUID) right);
					break;
				default:
					break;
				}
				// reverse the order if 'desc' is specified in the orderby option
				if (isDescending) {
					return -result;
				}

				return compareResult;
			}
		});
	}

}
