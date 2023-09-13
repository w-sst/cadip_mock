package de.werum.coprs.cadip.cadip_mock.service.processor;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
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
import org.apache.olingo.server.api.processor.EntityCollectionProcessor;
import org.apache.olingo.server.api.serializer.EntityCollectionSerializerOptions;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.SerializerResult;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.apache.olingo.server.api.uri.queryoption.FilterOption;
import org.apache.olingo.server.api.uri.queryoption.expression.Expression;
import org.apache.olingo.server.api.uri.queryoption.expression.ExpressionVisitException;

import de.werum.coprs.cadip.cadip_mock.data.Storage;

public class ProductEntityCollectionProcessor implements EntityCollectionProcessor{

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

		// 1st we have retrieve the requested EntitySet from the uriInfo object
		// (representation of the parsed service URI)
		List<UriResource> resourcePaths = uriInfo.getUriResourceParts();
		UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) resourcePaths.get(0); // in our example, the
																									// first segment is
																									// the EntitySet
		EdmEntitySet edmEntitySet = uriResourceEntitySet.getEntitySet();
		
		// 2nd: fetch the data from backend for this requested EntitySetName
		// it has to be delivered as EntitySet object
		EntityCollection entitySet = storage.readEntitySetData(edmEntitySet);
		
		//Options wie Orderby, oder diese ins getData auslagern
		FilterOption filterOption = uriInfo.getFilterOption();
		if(filterOption != null) {
			Expression filterExpression = filterOption.getExpression();
		    try {
		        List<Entity> entityList = entitySet.getEntities();
		        Iterator<Entity> entityIterator = entityList.iterator();

		        // Evaluate the expression for each entity
		        // If the expression is evaluated to "true", keep the entity otherwise remove it from
		        // the entityList
		        while (entityIterator.hasNext()) {
		          // To evaluate the the expression, create an instance of the Filter Expression
		          // Visitor and pass the current entity to the constructor
		          Entity currentEntity = entityIterator.next();
		          FilterExpressionVisitor expressionVisitor = new FilterExpressionVisitor(currentEntity);

		          // Evaluating the expression
		          Object visitorResult = filterExpression.accept(expressionVisitor);
		          // The result of the filter expression must be of type Edm.Boolean
		          if(visitorResult instanceof Boolean) {
		             if(!Boolean.TRUE.equals(visitorResult)) {
		               // The expression evaluated to false (or null), so we have to remove the
		               // currentEntity from entityList
		     	      entityIterator.remove();
		             }
		          } else {
		              throw new ODataApplicationException("A filter expression must evaulate to type Edm.Boolean", HttpStatusCode.BAD_REQUEST.getStatusCode(), Locale.ENGLISH);
		          }
		       } // End while
		     } catch (ExpressionVisitException e) {
		        throw new ODataApplicationException("Exception in filter evaluation",
		                      HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), Locale.ENGLISH);
		     }
		}
		
		
		// 3rd: create a serializer based on the requested format (json)
		ODataSerializer serializer = odata.createSerializer(responseFormat);

		// 4th: Now serialize the content: transform from the EntitySet object to
		// InputStream
		EdmEntityType edmEntityType = edmEntitySet.getEntityType();
		ContextURL contextUrl = ContextURL.with().entitySet(edmEntitySet).build();

		final String id = request.getRawBaseUri() + "/" + edmEntitySet.getName();
		EntityCollectionSerializerOptions opts = EntityCollectionSerializerOptions.with().id(id).contextURL(contextUrl)
				.build();
		SerializerResult serializerResult = serializer.entityCollection(serviceMetadata, edmEntityType, entitySet,
				opts);
		InputStream serializedContent = serializerResult.getContent();

		// Finally: configure the response object: set the body, headers and status code
		response.setContent(serializedContent);
		response.setStatusCode(HttpStatusCode.OK.getStatusCode());
		response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());

	}

}
